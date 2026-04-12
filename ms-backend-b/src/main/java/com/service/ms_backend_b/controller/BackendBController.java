package com.service.ms_backend_b.controller;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BackendBController {

    @GetMapping("/datos-sensibles")
    public ResponseEntity<Map<String, Object>> getDatosSensibles(
            HttpServletRequest request) {

        Claims claims = (Claims) request.getAttribute("claims");

        if (!"ADMIN".equals(claims.get("role"))) {
            return ResponseEntity.status(403)
                .body(Map.of("error", "Rol insuficiente para Backend B"));
        }

        // Obtener el CN del certificado cliente (prueba que mTLS funcionó)
        String clienteCN = "no disponible";
        X509Certificate[] certs = (X509Certificate[])
            request.getAttribute("jakarta.servlet.request.X509Certificate");
        if (certs != null && certs.length > 0) {
            clienteCN = certs[0].getSubjectX500Principal().getName();
        }

        return ResponseEntity.ok(Map.of(
            "servicio",           "Backend B",
            "transporte",         "mTLS (autenticación mutua)",
            "usuario",            claims.getSubject(),
            "rol",                claims.get("role"),
            "cliente-certificado", clienteCN,
            "timestamp",          new Date().toString()
        ));
    }
}