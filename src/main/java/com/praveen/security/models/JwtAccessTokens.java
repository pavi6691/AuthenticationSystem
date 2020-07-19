package com.praveen.security.models;

public class JwtAccessTokens {
 
	private String accessToken;
	private String refreshToken;
	public JwtAccessTokens(String accessToken, String refreshToken) {
		super();
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
}
