package com.praveen.security.models;

public enum Scopes {
	REFRESH_TOKEN;
	public String authority() {
		return "ROLE_" + this.name();
	}
}
