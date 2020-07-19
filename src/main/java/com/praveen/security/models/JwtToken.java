package com.praveen.security.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.jsonwebtoken.Claims;
public final class JwtToken {
    private final String rawToken;
    @JsonIgnore private Claims claims;

    public JwtToken(final String token, Claims claims) {
        this.rawToken = token;
        this.claims = claims;
    }

    public String getToken() {
        return this.rawToken;
    }

    public Claims getClaims() {
        return claims;
    }
}
