package com.praveen.security.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.praveen.security.exceptions.InvalidJwtToken;
import com.praveen.security.exceptions.JwtExpiredTokenException;
import com.praveen.security.models.ApiError;

@RestControllerAdvice
public class ControllerAdvise {

	@ExceptionHandler(value = { InvalidJwtToken.class, JwtExpiredTokenException.class, BadCredentialsException.class })
	public ResponseEntity<ApiError> exceptionHandler(HttpServletRequest request, RuntimeException e) {
		ApiError apiError;
		if (e instanceof InvalidJwtToken) {
			apiError = new ApiError(HttpStatus.UNAUTHORIZED);
			apiError.setPath(request.getRequestURI());
			apiError.setMessage("Invalid Jwt Token! " + e.getMessage());
		} else if (e instanceof BadCredentialsException) {
			apiError = new ApiError(HttpStatus.UNAUTHORIZED);
			apiError.setPath(request.getRequestURI());
			apiError.setMessage("Bad Credential! " + e.getMessage());
		} else if (e instanceof JwtExpiredTokenException) {
			apiError = new ApiError(HttpStatus.UNAUTHORIZED);
			apiError.setPath(request.getRequestURI());
			apiError.setMessage("Jwt Token Expired! " + e.getMessage());
		} else {
			apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
			apiError.setPath(request.getRequestURI());
			apiError.setMessage("Internal Server error! " + e.getMessage());
		}
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}
}
