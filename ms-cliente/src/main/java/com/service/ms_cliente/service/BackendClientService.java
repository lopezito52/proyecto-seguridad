package com.service.ms_cliente.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
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

    // Obtener token del Backend A
    public Map obtenerToken(String usuario, String password) {
        Map<String, String> body = Map.of(
            "usuario", usuario,
            "password", password
        );
        ResponseEntity<Map> response = restTemplateTLS.postForEntity(
            backendAUrl + "/api/auth/token",
            body,
            Map.class
        );
        return response.getBody();
    }

    // Consultar Backend A con TLS + JWT
    public Map consultarBackendA(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplateTLS.exchange(
            backendAUrl + "/api/datos",
            HttpMethod.GET,
            entity,
            Map.class
        );
        return response.getBody();
    }

    // Consultar Backend B con mTLS + JWT
    public Map consultarBackendB(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplateMTLS.exchange(
            backendBUrl + "/api/datos-sensibles",
            HttpMethod.GET,
            entity,
            Map.class
        );
        return response.getBody();
    }
}
