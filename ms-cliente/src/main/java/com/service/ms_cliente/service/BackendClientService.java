package com.service.ms_cliente.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class BackendClientService {

    @Value("${backend.a.url}")
    private String backendAUrl;

    @Value("${backend.b.url}")
    private String backendBUrl;

    @Autowired
    @Qualifier("restTemplateTLS")
    private RestTemplate restTemplateTLS;

    @Autowired
    @Qualifier("restTemplateMTLS")
    private RestTemplate restTemplateMTLS;

    public ResponseEntity<Map> obtenerToken(String usuario, String password) {
        System.out.println(">>> [BackendClientService] Pidiendo token para: " + usuario);
        try {
            Map<String, String> body = Map.of(
                "usuario", usuario,
                "password", password
            );
            ResponseEntity<Map> response = restTemplateTLS.postForEntity(
                backendAUrl + "/api/auth/token",
                body,
                Map.class
            );
            System.out.println(">>> [BackendClientService] Token recibido, status: " + response.getStatusCode());
            return response;
        } catch (HttpClientErrorException e) {
            System.out.println(">>> [BackendClientService] Error al obtener token: " + e.getStatusCode());
            return ResponseEntity
                .status(e.getStatusCode())
                .body(Map.of("error", e.getResponseBodyAsString()));
        }
    }

    public ResponseEntity<Map> consultarBackendA(String token) {
        System.out.println(">>> [BackendClientService] Consultando Backend A con TLS...");
        System.out.println(">>> [BackendClientService] Token enviado: " + token.substring(0, 20) + "...");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplateTLS.exchange(
                backendAUrl + "/api/datos",
                HttpMethod.GET,
                entity,
                Map.class
            );
            System.out.println(">>> [BackendClientService] Backend A respondio: " + response.getStatusCode());
            return response;
        } catch (HttpClientErrorException e) {
            System.out.println(">>> [BackendClientService] Error Backend A: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return ResponseEntity
                .status(e.getStatusCode())
                .body(Map.of("error", e.getResponseBodyAsString()));
        }
    }

    public ResponseEntity<Map> consultarBackendB(String token) {
        System.out.println(">>> [BackendClientService] Consultando Backend B con mTLS...");
        System.out.println(">>> [BackendClientService] Token enviado: " + token.substring(0, 20) + "...");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplateMTLS.exchange(
                backendBUrl + "/api/datos-sensibles",
                HttpMethod.GET,
                entity,
                Map.class
            );
            System.out.println(">>> [BackendClientService] Backend B respondio: " + response.getStatusCode());
            return response;
        } catch (HttpClientErrorException e) {
            System.out.println(">>> [BackendClientService] Error Backend B: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return ResponseEntity
                .status(e.getStatusCode())
                .body(Map.of("error", e.getResponseBodyAsString()));
        }
    }
}