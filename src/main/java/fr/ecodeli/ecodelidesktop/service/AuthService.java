package fr.ecodeli.ecodelidesktop.service;

import fr.ecodeli.ecodelidesktop.api.AuthAPI;
import fr.ecodeli.ecodelidesktop.model.AuthResponse;
import fr.ecodeli.ecodelidesktop.model.LoginRequest;

import java.io.IOException;

public class AuthService {
    private AuthAPI authApi;

    public AuthService() {
        this.authApi = new AuthAPI();
    }

    public AuthResponse login(LoginRequest loginRequest) throws IOException {
        return authApi.login(loginRequest);
    }

    public AuthResponse loginWith2FA(LoginRequest loginRequest, String code) throws IOException {
        return authApi.loginWith2FA(loginRequest, code);
    }
}
