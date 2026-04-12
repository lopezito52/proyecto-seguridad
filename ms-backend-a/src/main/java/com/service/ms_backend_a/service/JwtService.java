package com.service.ms_backend_a.service;


import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.private-key-path}")
    private Resource privateKeyResource;

    @Value("${jwt.public-key-path}")
    private Resource publicKeyResource;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        // Cargar llave privada
        String privPem = new String(privateKeyResource.getInputStream().readAllBytes())
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("-----BEGIN RSA PRIVATE KEY-----", "")
            .replace("-----END RSA PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
        byte[] privBytes = Base64.getDecoder().decode(privPem);
        privateKey = KeyFactory.getInstance("RSA")
            .generatePrivate(new PKCS8EncodedKeySpec(privBytes));

        // Cargar llave pública
        String pubPem = new String(publicKeyResource.getInputStream().readAllBytes())
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", "");
        byte[] pubBytes = Base64.getDecoder().decode(pubPem);
        publicKey = KeyFactory.getInstance("RSA")
            .generatePublic(new X509EncodedKeySpec(pubBytes));
    }

    // Genera un token firmado con la llave privada
    public String generarToken(String usuario, String rol) {
        return Jwts.builder()
            .setSubject(usuario)
            .claim("role", rol)
            .setIssuer(issuer)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(privateKey, SignatureAlgorithm.RS256)
            .compact();
    }

    // Valida el token y retorna los claims — lanza excepción si es inválido
    public Claims validarToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .requireIssuer(issuer)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
