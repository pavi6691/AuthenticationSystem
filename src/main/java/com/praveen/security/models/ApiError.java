package com.praveen.security.models;

import org.springframework.http.HttpStatus;

public class ApiError {
    private HttpStatus status;

    // holds a user-friendly message about the error.
    private String message;

    // holds a system message describing the error in more detail.

    // returns the part of this request's URL
    private String path;

    public ApiError(HttpStatus status) {
      this();
      this.status = status;
    }

    
	public HttpStatus getStatus() {
		return status;
	}


	public void setStatus(HttpStatus status) {
		this.status = status;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public ApiError() {
	}
}
