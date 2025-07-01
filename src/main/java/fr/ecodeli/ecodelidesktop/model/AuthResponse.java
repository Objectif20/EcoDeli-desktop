package fr.ecodeli.ecodelidesktop.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("two_factor_required")
    private boolean twoFactorRequired;

    @SerializedName("message")
    private String message;

    public AuthResponse() {}

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

    public boolean isTwoFactorRequired() {
        return twoFactorRequired;
    }

    public void setTwoFactorRequired(boolean twoFactorRequired) {
        this.twoFactorRequired = twoFactorRequired;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "accessToken='" + (accessToken != null ? "[PRESENT]" : "null") + '\'' +
                ", refreshToken='" + (refreshToken != null ? "[PRESENT]" : "null") + '\'' +
                ", twoFactorRequired=" + twoFactorRequired +
                ", message='" + message + '\'' +
                '}';
    }
}