package com.service.ms_backend_b.service;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class JwtService {

    @Value("${jwt.public-key-path}")
    private Resource publicKeyResource;

    @Value("${jwt.issuer}")
    private String issuer;

    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        String pubPem = new String(publicKeyResource.getInputStream().readAllBytes())
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", "");
        byte[] pubBytes = Base64.getDecoder().decode(pubPem);
        publicKey = KeyFactory.getInstance("RSA")
            .generatePublic(new X509EncodedKeySpec(pubBytes));
    }

    // Backend B solo VERIFICA, nunca emite tokens
    public Claims validarToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .requireIssuer(issuer)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}