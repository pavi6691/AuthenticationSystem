package com.praveen.security.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.praveen.security.exceptions.InvalidJwtToken;
import com.praveen.security.models.AuthenticationRequest;
import com.praveen.security.models.JwtAccessTokens;
import com.praveen.security.models.JwtToken;
import com.praveen.security.services.JwtService;
import com.praveen.security.services.MyUserDetailsService;
import com.praveen.security.services.RawAccessJwtToken;
import com.praveen.security.services.RefreshToken;

@RestController
public class SecurityController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private MyUserDetailsService myUserDetailsService;

	@Autowired
	private JwtService jwtService;

	@RequestMapping(value = "/login", method = RequestMethod.POST, produces={ MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
			throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		} catch (Exception e) {
			throw new Exception("Incorrect UserName and Password", e);
		}
		final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		final JwtToken accessToken = jwtService.generateAccessToken(userDetails);
		final JwtToken refreshToken = jwtService.generateRefreshToken(userDetails);
		return ResponseEntity.ok(new JwtAccessTokens(accessToken.getToken(), refreshToken.getToken()));
	}
	
	@RequestMapping(value="/refresh", method=RequestMethod.GET, produces={ MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody JwtToken refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String tokenPayload = request.getHeader("Authorization");
        RawAccessJwtToken rawToken = new RawAccessJwtToken(tokenPayload);
        RefreshToken.create(rawToken, jwtService.getSECRET_KEY()).orElseThrow(() -> new InvalidJwtToken());
        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(tokenPayload);
        return jwtService.generateAccessToken(userDetails);
    }
}
