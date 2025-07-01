package fr.ecodeli.ecodelidesktop.view.auth;

import fr.ecodeli.ecodelidesktop.api.AuthApi;
import fr.ecodeli.ecodelidesktop.model.AuthResponse;
import fr.ecodeli.ecodelidesktop.model.LoginRequest;
import fr.ecodeli.ecodelidesktop.service.TokenStorage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthViewController implements Initializable {

    @FXML private VBox loginForm;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    @FXML private VBox twoFactorForm;
    @FXML private TextField otp1, otp2, otp3, otp4, otp5, otp6;
    @FXML private Button verifyButton;

    @FXML private Label errorLabel;

    private final AuthApi authApi;
    private LoginRequest currentLoginRequest;

    public AuthViewController() {
        this.authApi = new AuthApi();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupOtpFields();
        hideError();
    }

    private void setupOtpFields() {
        TextField[] otpFields = {otp1, otp2, otp3, otp4, otp5, otp6};

        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;
            TextField field = otpFields[i];

            field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d?")) {
                    field.setText(oldValue);
                } else if (newValue.length() > 1) {
                    field.setText(newValue.substring(0, 1));
                }

                if (newValue.length() == 1 && index < otpFields.length - 1) {
                    otpFields[index + 1].requestFocus();
                }
            });

            field.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case BACK_SPACE:
                        if (field.getText().isEmpty() && index > 0) {
                            otpFields[index - 1].requestFocus();
                        }
                        break;
                    case ENTER:
                        if (index == otpFields.length - 1) {
                            handleVerify2FA();
                        }
                        break;
                }
            });
        }
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("L'email et le mot de passe sont requis.");
            return;
        }

        currentLoginRequest = new LoginRequest(email, password);

        loginButton.setDisable(true);
        loginButton.setText("Connexion...");
        hideError();

        new Thread(() -> {
            try {
                AuthResponse response = authApi.login(currentLoginRequest);

                Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    loginButton.setText("Se connecter");

                    if (response.isTwoFactorRequired()) {
                        showTwoFactorForm();
                    } else {
                        handleSuccessfulLogin(response);
                    }
                });

            } catch (IOException e) {
                Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    loginButton.setText("Se connecter");
                    showError("Email ou mot de passe incorrect");
                });
            }
        }).start();
    }

    @FXML
    private void handleVerify2FA() {
        String otpCode = getOtpCode();

        if (otpCode.length() != 6) {
            showError("Veuillez entrer un code à 6 chiffres.");
            return;
        }

        verifyButton.setDisable(true);
        verifyButton.setText("Vérification...");
        hideError();

        new Thread(() -> {
            try {
                AuthResponse response = authApi.loginWith2FA(currentLoginRequest, otpCode);

                Platform.runLater(() -> {
                    verifyButton.setDisable(false);
                    verifyButton.setText("Vérifier");
                    handleSuccessfulLogin(response);
                });

            } catch (IOException e) {
                Platform.runLater(() -> {
                    verifyButton.setDisable(false);
                    verifyButton.setText("Vérifier");
                    showError("Code incorrect ou erreur de vérification.");
                    clearOtpFields();
                    otp1.requestFocus();
                });
            }
        }).start();
    }

    @FXML
    private void handleBackToLogin() {
        showLoginForm();
        clearOtpFields();
        hideError();
    }

    private void handleSuccessfulLogin(AuthResponse response) {
        try {
            TokenStorage.saveTokens(response.getAccessToken(), response.getRefreshToken());

            redirectToMainApp();

        } catch (Exception e) {
            showError("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    private void redirectToMainApp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/navigation/MainView.fxml"));
            Parent mainView = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(mainView);

            stage.setScene(scene);
            stage.setTitle("EcoDeli - Tableau de bord");
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            showError("Erreur lors de la redirection : " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void showTwoFactorForm() {
        loginForm.setVisible(false);
        loginForm.setManaged(false);

        twoFactorForm.setVisible(true);
        twoFactorForm.setManaged(true);

        Platform.runLater(() -> otp1.requestFocus());
    }

    private void showLoginForm() {
        twoFactorForm.setVisible(false);
        twoFactorForm.setManaged(false);

        loginForm.setVisible(true);
        loginForm.setManaged(true);
    }

    private String getOtpCode() {
        return otp1.getText() + otp2.getText() + otp3.getText() +
                otp4.getText() + otp5.getText() + otp6.getText();
    }

    private void clearOtpFields() {
        otp1.clear();
        otp2.clear();
        otp3.clear();
        otp4.clear();
        otp5.clear();
        otp6.clear();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
    }
}