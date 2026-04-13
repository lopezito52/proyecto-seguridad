package com.service.ms_backend_a.filter;

import com.service.ms_backend_a.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        boolean skip = path.equals("/api/auth/token")
                    || path.equals("/api/auth/token-expirado");
        System.out.println(">>> [JwtFilter] Path: " + path + " | Saltar filtro: " + skip);
        return skip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        System.out.println(">>> [JwtFilter] Revisando header Authorization...");
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            System.out.println(">>> [JwtFilter] ERROR: No hay token");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Token requerido: Authorization: Bearer <token>");
            return;
        }

        try {
            String token = header.substring(7);
            System.out.println(">>> [JwtFilter] Token recibido, validando...");
            Claims claims = jwtService.validarToken(token);

            // Registrar autenticacion en el contexto de Spring Security
            String role = (String) claims.get("role");
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    claims.getSubject(),
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println(">>> [JwtFilter] Autenticacion registrada en SecurityContext: ROLE_" + role);

            request.setAttribute("claims", claims);
            chain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            System.out.println(">>> [JwtFilter] ERROR: Token expirado");
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expirado");
        } catch (JwtException e) {
            System.out.println(">>> [JwtFilter] ERROR: Token invalido - " + e.getMessage());
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token invalido o manipulado");
        }
    }
}