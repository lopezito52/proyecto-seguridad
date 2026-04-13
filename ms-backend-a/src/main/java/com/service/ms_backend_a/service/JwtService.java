package com.service.ms_backend_a.service;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import java.util.Date;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
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
        System.out.println(">>> [JwtService] Instancia ID: " + System.identityHashCode(this));
        System.out.println(">>> [JwtService] Cargando llaves JWT...");

        String privPem = new String(privateKeyResource.getInputStream().readAllBytes())
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("-----BEGIN RSA PRIVATE KEY-----", "")
            .replace("-----END RSA PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
        byte[] privBytes = Base64.getDecoder().decode(privPem);
        privateKey = KeyFactory.getInstance("RSA")
            .generatePrivate(new PKCS8EncodedKeySpec(privBytes));
        System.out.println(">>> [JwtService] Llave privada cargada, hash: " + privateKey.hashCode());

        String pubPem = new String(publicKeyResource.getInputStream().readAllBytes())
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", "");
        byte[] pubBytes = Base64.getDecoder().decode(pubPem);
        publicKey = KeyFactory.getInstance("RSA")
            .generatePublic(new X509EncodedKeySpec(pubBytes));
        System.out.println(">>> [JwtService] Llave publica cargada, hash: " + publicKey.hashCode());
    }

    public String generarToken(String usuario, String rol) {
        System.out.println(">>> [JwtService] Firmando con llave hash: " + privateKey.hashCode());
        String token = Jwts.builder()
            .setSubject(usuario)
            .claim("role", rol)
            .setIssuer(issuer)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(privateKey, SignatureAlgorithm.RS256)
            .compact();
        System.out.println(">>> [JwtService] Token generado para: " + usuario);
        return token;
    }

    public Claims validarToken(String token) {
        System.out.println(">>> [JwtService] Validando con llave hash: " + publicKey.hashCode());
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .requireIssuer(issuer)
            .build()
            .parseClaimsJws(token)
            .getBody();
        System.out.println(">>> [JwtService] Token valido! subject: " + claims.getSubject());
        System.out.println(">>> [JwtService] role: " + claims.get("role"));
        return claims;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}