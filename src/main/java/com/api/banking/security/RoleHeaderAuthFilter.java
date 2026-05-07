package com.api.banking.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class RoleHeaderAuthFilter extends OncePerRequestFilter {

    private static final String ROLE_HEADER = "X-Role";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String role = request.getHeader(ROLE_HEADER);

        if (role != null && !role.isBlank()) {
            String normalized = role.toUpperCase().trim();
            if (normalized.equals("ADMIN") || normalized.equals("CLIENT")) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        normalized, null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + normalized))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
