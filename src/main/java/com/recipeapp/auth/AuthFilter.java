package com.recipeapp.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private final TokenStore tokenStore;

    public AuthFilter(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String method  = request.getMethod();
        String path    = request.getRequestURI();

        boolean isWriteOp    = !method.equals("GET") && !method.equals("OPTIONS");
        boolean isAuthEndpoint = path.startsWith("/api/auth");

        if (isWriteOp && !isAuthEndpoint) {
            String auth  = request.getHeader("Authorization");
            String token = (auth != null && auth.startsWith("Bearer ")) ? auth.substring(7) : null;

            if (!tokenStore.isValid(token)) {
                response.setStatus(401);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Unauthorized\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
