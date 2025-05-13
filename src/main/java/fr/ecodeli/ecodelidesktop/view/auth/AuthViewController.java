package fr.ecodeli.ecodelidesktop.view.auth;

import fr.ecodeli.ecodelidesktop.api.AuthApi;
import fr.ecodeli.ecodelidesktop.model.AuthResponse;
import fr.ecodeli.ecodelidesktop.model.LoginRequest;
import fr.ecodeli.ecodelidesktop.service.TokenStorage;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class AuthViewController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField codeField;

    private final AuthApi authApi;

    public AuthViewController() {
        this.authApi = new AuthApi();
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();
        String code = codeField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("Erreur : L'email et le mot de passe sont requis.");
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email, password);

        try {
            AuthResponse response;
            if (code.isEmpty()) {
                response = authApi.login(loginRequest);
            } else {
                response = authApi.loginWith2FA(loginRequest, code);
            }

            if (response.isTwoFactorRequired()) {
                System.out.println("2FA requis : L'authentification à deux facteurs est nécessaire. Veuillez entrer votre code 2FA.");
            } else {
                System.out.println("Tokens : " + response.getAccessToken() + " " + response.getRefreshToken());
                System.out.println("Réponse : " + response);
                TokenStorage.saveTokens(response.getAccessToken(), response.getRefreshToken());
                System.out.println("Succès : Connexion réussie !");
            }
        } catch (IOException e) {
            System.out.println("Erreur : Une erreur est survenue pendant la connexion : " + e.getMessage());
        }
    }
}
