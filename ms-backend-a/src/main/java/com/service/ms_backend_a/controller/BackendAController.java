package com.service.ms_backend_a.controller;


import com.service.ms_backend_a.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BackendAController {

    @Autowired
    private JwtService jwtService;

    @PostMapping("/auth/token")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        System.out.println(">>> [BackendAController] POST /api/auth/token");
        System.out.println(">>> [BackendAController] Usuario recibido: " + body.get("usuario"));

        String usuario = body.get("usuario");
        String password = body.get("password");

        if (!"admin".equals(usuario) || !"pass123".equals(password)) {
            System.out.println(">>> [BackendAController] Credenciales invalidas");
            return ResponseEntity.status(401)
                .body(Map.of("error", "Credenciales invalidas"));
        }

        String token = jwtService.generarToken(usuario, "ADMIN");
        System.out.println(">>> [BackendAController] Token generado y retornado");
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/auth/token-expirado")
    public ResponseEntity<?> getTokenExpirado() {
        System.out.println(">>> [BackendAController] Generando token expirado para pruebas");
        try {
            String token = Jwts.builder()
                .setSubject("admin")
                .claim("role", "ADMIN")
                .setIssuer("ms-backend-a")
                .setIssuedAt(new Date(System.currentTimeMillis() - 7200_000))
                .setExpiration(new Date(System.currentTimeMillis() - 3600_000))
                .signWith(jwtService.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
            return ResponseEntity.ok(Map.of("token-expirado", token));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/datos")
    public ResponseEntity<?> getDatos(HttpServletRequest request) {
        System.out.println(">>> [BackendAController] GET /api/datos");
        Claims claims = (Claims) request.getAttribute("claims");

        System.out.println(">>> [BackendAController] Claims recibidos:");
        System.out.println("    subject: " + claims.getSubject());
        System.out.println("    role: " + claims.get("role"));
        System.out.println("    issuer: " + claims.getIssuer());
        System.out.println("    expiration: " + claims.getExpiration());

        if (!"ADMIN".equals(claims.get("role"))) {
            System.out.println(">>> [BackendAController] Rol insuficiente");
            return ResponseEntity.status(403)
                .body(Map.of("error", "Rol insuficiente"));
        }

        return ResponseEntity.ok(Map.of(
            "servicio",   "Backend A",
            "transporte", "TLS - solo servidor autenticado",
            "usuario",    claims.getSubject(),
            "rol",        claims.get("role"),
            "issuer",     claims.getIssuer(),
            "timestamp",  new Date().toString()
        ));
    }
}