package com.praveen.security.services;
import org.springframework.security.authentication.BadCredentialsException;

import com.praveen.security.exceptions.JwtExpiredTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class RawAccessJwtToken {
            
    private String token;
    
    public RawAccessJwtToken(String token) {
        this.token = token;
    }

    public Jws<Claims> parseClaims(String signingKey) {
        try {
            return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(this.token);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
            throw new BadCredentialsException("Invalid JWT token: ", ex);
        } catch (ExpiredJwtException expiredEx) {
            throw new JwtExpiredTokenException(expiredEx.getMessage());
        }
    }

    public String getToken() {
        return token;
    }
}