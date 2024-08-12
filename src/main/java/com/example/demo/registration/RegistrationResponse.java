package com.example.demo.registration;

public class RegistrationResponse {
    private String status;
    private String message;
    private String token; // Add a field for the token

    public RegistrationResponse(String status, String message, String token) {
        this.status = status;
        this.message = message;
        this.token = token;
    }

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
