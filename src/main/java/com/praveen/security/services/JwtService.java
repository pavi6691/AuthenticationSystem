package com.praveen.security.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.praveen.security.models.JwtToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {

	@Value("${security.minutesToExpire}")
	private int timeForTokenToExpire = 0;

	@Value("${security.secrekey}")
	private String SECRET_KEY = "";

	public boolean validateToken(String token, UserDetails userDetails) {
		return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	public JwtToken generateRefreshToken(UserDetails userDetails) {
		if (userDetails.getUsername().isEmpty()) {
			throw new IllegalArgumentException("Cannot create JWT Token without username");
		}

		LocalDateTime currentTime = LocalDateTime.now();

		Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
		claims.put("scopes", Arrays.asList(com.praveen.security.models.Scopes.REFRESH_TOKEN.authority()));

		String token = Jwts.builder().setClaims(claims).setIssuer("").setId(UUID.randomUUID().toString())
				.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(currentTime.plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(SignatureAlgorithm.HS512, SECRET_KEY).compact();

		return new JwtToken(token, claims);
	}

	public JwtToken generateAccessToken(UserDetails userDetails) {
		Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
		claims.put("scopes", userDetails.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList()));
		return new JwtToken(createToken(claims, userDetails.getUsername()), claims);
	}

	private String createToken(Claims claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(timeForTokenToExpire)))
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		return claimResolver.apply(extractAllClaims(token));
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
	}

	public boolean isTokenExpired(String token) {
		return extractClaim(token, Claims::getExpiration).before(new Date());
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public int getTimeForTokenToExpire() {
		return timeForTokenToExpire;
	}

	public String getSECRET_KEY() {
		return SECRET_KEY;
	}

}
