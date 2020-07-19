package com.praveen.security.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.praveen.security.models.ApiError;
import com.praveen.security.services.JwtService;
import com.praveen.security.services.MyUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
	@Autowired
	private MyUserDetailsService myUserDetailsService;

	@Autowired
	private JwtService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String jwtToken = request.getHeader("Authorization");
		String userName = null;
		try {
			if (jwtToken != null && !jwtToken.isEmpty()) {
				userName = jwtService.extractUsername(jwtToken);
			}
			if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = this.myUserDetailsService.loadUserByUsername(userName);
				if (jwtService.validateToken(jwtToken, userDetails)) {
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					usernamePasswordAuthenticationToken
							.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

				}
			}
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException ex) {
			raiseException(request, response, "Token Expired!");
		} catch (SignatureException ex) {
			raiseException(request, response, "Invalid Token!");
		}

	}

	private void raiseException(HttpServletRequest request, HttpServletResponse response, String message)
			throws IOException, ServletException {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED);
		apiError.setMessage(message);
		apiError.setPath(request.getRequestURI());
		byte[] body = new ObjectMapper().writeValueAsBytes(apiError);
		response.getOutputStream().write(body);
	}

	public String convertObjectToJson(Object object) throws JsonProcessingException {
		if (object == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(object);
	}
}
