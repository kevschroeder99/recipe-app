package com.recipeapp.auth;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class TokenStore {

    private final Set<String> tokens = Collections.synchronizedSet(new HashSet<>());

    public String createToken() {
        String token = UUID.randomUUID().toString();
        tokens.add(token);
        return token;
    }

    public boolean isValid(String token) {
        return token != null && tokens.contains(token);
    }

    public void removeToken(String token) {
        tokens.remove(token);
    }
}
