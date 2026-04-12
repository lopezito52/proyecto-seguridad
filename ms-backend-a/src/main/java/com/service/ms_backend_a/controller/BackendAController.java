package com.service.ms_backend_a.controller;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.service.ms_backend_a.service.JwtService;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BackendAController {

    @Autowired
    private JwtService jwtService;

    // ── Endpoint público: emite JWT ──────────────────────────────────────────
    @PostMapping("/auth/token")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody Map<String, String> body) {

        String usuario = body.get("usuario");
        String password = body.get("password");

        // Validación simple (en producción usarías BD)
        if (!"admin".equals(usuario) || !"pass123".equals(password)) {
            return ResponseEntity.status(401)
                .body(Map.of("error", "Credenciales inválidas"));
        }

        String token = jwtService.generarToken(usuario, "ADMIN");
        return ResponseEntity.ok(Map.of("token", token));
    }

    // ── Endpoint protegido: requiere JWT con rol ADMIN ───────────────────────
    @GetMapping("/datos")
    public ResponseEntity<Map<String, Object>> getDatos(HttpServletRequest request) {

        Claims claims = (Claims) request.getAttribute("claims");

        if (!"ADMIN".equals(claims.get("role"))) {
            return ResponseEntity.status(403)
                .body(Map.of("error", "Rol insuficiente"));
        }

        return ResponseEntity.ok(Map.of(
            "servicio",   "Backend A",
            "transporte", "TLS (solo servidor autenticado)",
            "usuario",    claims.getSubject(),
            "rol",        claims.get("role"),
            "timestamp",  new Date().toString()
        ));
    }
}