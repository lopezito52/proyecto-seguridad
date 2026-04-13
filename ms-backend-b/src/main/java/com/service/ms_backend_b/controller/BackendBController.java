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
    public ResponseEntity<?> getDatosSensibles(HttpServletRequest request) {
        System.out.println(">>> [BackendBController] GET /api/datos-sensibles");
        Claims claims = (Claims) request.getAttribute("claims");

        System.out.println(">>> [BackendBController] Claims recibidos:");
        System.out.println("    subject: " + claims.getSubject());
        System.out.println("    role: " + claims.get("role"));
        System.out.println("    issuer: " + claims.getIssuer());
        System.out.println("    expiration: " + claims.getExpiration());

        if (!"ADMIN".equals(claims.get("role"))) {
            System.out.println(">>> [BackendBController] Rol insuficiente");
            return ResponseEntity.status(403)
                .body(Map.of("error", "Rol insuficiente para Backend B"));
        }

        // Obtener CN del certificado cliente (prueba de mTLS)
        String clienteCN = "no disponible";
        X509Certificate[] certs = (X509Certificate[])
            request.getAttribute("jakarta.servlet.request.X509Certificate");
        if (certs != null && certs.length > 0) {
            clienteCN = certs[0].getSubjectX500Principal().getName();
            System.out.println(">>> [BackendBController] Certificado cliente CN: " + clienteCN);
        } else {
            System.out.println(">>> [BackendBController] No se encontro certificado cliente");
        }

        return ResponseEntity.ok(Map.of(
            "servicio",            "Backend B",
            "transporte",          "mTLS - autenticacion mutua",
            "usuario",             claims.getSubject(),
            "rol",                 claims.get("role"),
            "cliente-certificado", clienteCN,
            "timestamp",           new Date().toString()
        ));
    }
}