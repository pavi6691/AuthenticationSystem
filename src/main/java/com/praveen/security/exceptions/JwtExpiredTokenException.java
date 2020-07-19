package com.praveen.security.exceptions;

import org.springframework.security.core.AuthenticationException;

import com.praveen.security.models.JwtToken;
public class JwtExpiredTokenException extends AuthenticationException {
    private static final long serialVersionUID = -5959543783324224864L;
    
    private JwtToken token;

    public JwtExpiredTokenException(String msg) {
        super(msg);
    }

    public String token() {
        return this.token.getToken();
    }
}
