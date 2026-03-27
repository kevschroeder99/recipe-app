package com.recipeapp.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${app.admin.password}")
    private String adminPassword;

    private final TokenStore tokenStore;

    public AuthController(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String password = body.get("password");
        if (adminPassword.equals(password)) {
            return ResponseEntity.ok(Map.of("token", tokenStore.createToken()));
        }

        return ResponseEntity.status(401).body(Map.of("error", "Invalid password"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String auth) {
        if (auth != null && auth.startsWith("Bearer ")) {
            tokenStore.removeToken(auth.substring(7));
        }

        return ResponseEntity.ok().build();
    }
}
