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

    @PostMapping("/login")
    public ResponseEntity<Map> login(@RequestBody Map<String, String> body) {
        System.out.println(">>> [ClienteController] POST /cliente/login");
        return backendClientService.obtenerToken(
            body.get("usuario"),
            body.get("password")
        );
    }

    @GetMapping("/backend-a")
    public ResponseEntity<Map> consultarBackendA(@RequestParam String token) {
        System.out.println(">>> [ClienteController] GET /cliente/backend-a");
        return backendClientService.consultarBackendA(token);
    }

    @GetMapping("/backend-b")
    public ResponseEntity<Map> consultarBackendB(@RequestParam String token) {
        System.out.println(">>> [ClienteController] GET /cliente/backend-b");
        return backendClientService.consultarBackendB(token);
    }
}