package com.apsusm.dto;

public class PaymentInitResponse {
    private boolean success;
    private String authorizationUrl;
    private String reference;
    private String message;

    public PaymentInitResponse() {}

    public PaymentInitResponse(boolean success, String authorizationUrl, String reference, String message) {
        this.success = success;
        this.authorizationUrl = authorizationUrl;
        this.reference = reference;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getAuthorizationUrl() { return authorizationUrl; }
    public void setAuthorizationUrl(String authorizationUrl) { this.authorizationUrl = authorizationUrl; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
