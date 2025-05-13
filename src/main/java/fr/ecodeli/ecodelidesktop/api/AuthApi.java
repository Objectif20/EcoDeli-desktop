package fr.ecodeli.ecodelidesktop.api;

import fr.ecodeli.ecodelidesktop.client.CustomOkHttpClient;
import fr.ecodeli.ecodelidesktop.model.AuthResponse;
import fr.ecodeli.ecodelidesktop.model.LoginRequest;
import com.google.gson.Gson;

import java.io.IOException;

public class AuthApi {
    private final CustomOkHttpClient httpClient;
    private final Gson gson;

    public AuthApi() {
        this.httpClient = new CustomOkHttpClient();
        this.gson = new Gson();
    }

    public AuthResponse login(LoginRequest loginRequest) throws IOException {
        String jsonRequest = gson.toJson(loginRequest);
        String jsonResponse = httpClient.post("/desktop/auth/login", jsonRequest);
        return gson.fromJson(jsonResponse, AuthResponse.class);
    }

    public AuthResponse loginWith2FA(LoginRequest loginRequest, String code) throws IOException {
        String jsonRequest = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"code\":\"%s\"}",
                loginRequest.getEmail(), loginRequest.getPassword(), code);
        String jsonResponse = httpClient.post("/desktop/auth/login-2fa", jsonRequest);
        return gson.fromJson(jsonResponse, AuthResponse.class);
    }
}
