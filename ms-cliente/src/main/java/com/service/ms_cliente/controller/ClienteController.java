package com.service.ms_cliente.controller;

import com.service.ms_cliente.service.BackendClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private BackendClientService backendClientService;

    // No necesita Authorization — aquí se OBTIENE el token
    @PostMapping("/login")
    public ResponseEntity<Map> login(@RequestBody Map<String, String> body) {
        Map resultado = backendClientService.obtenerToken(
            body.get("usuario"),
            body.get("password")
        );
        return ResponseEntity.ok(resultado);
    }

    // Recibe el token como parámetro en la URL para simplificar las pruebas
    @GetMapping("/backend-a")
    public ResponseEntity<Map> consultarBackendA(@RequestParam String token) {
        Map resultado = backendClientService.consultarBackendA(token);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/backend-b")
    public ResponseEntity<Map> consultarBackendB(@RequestParam String token) {
        Map resultado = backendClientService.consultarBackendB(token);
        return ResponseEntity.ok(resultado);
    }
}