package fr.ecodeli.ecodelidesktop.view.auth;

import fr.ecodeli.ecodelidesktop.api.AuthApi;
import fr.ecodeli.ecodelidesktop.model.AuthResponse;
import fr.ecodeli.ecodelidesktop.model.LoginRequest;
import fr.ecodeli.ecodelidesktop.service.TokenStorage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
                TokenStorage.saveTokens(response.getAccessToken(), response.getRefreshToken());
                System.out.println("Succès : Connexion réussie !");
            }
        } catch (IOException e) {
            System.out.println("Erreur : Une erreur est survenue pendant la connexion : " + e.getMessage());
        }
    }

    @FXML
    private void handleGoToMainView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/navigation/MainView.fxml"));
            Parent mainView = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(mainView));
            stage.setTitle("EcoDeli - Tableau de bord");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleGoToStats(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/dashboard/StatsView.fxml"));
            Parent navigationRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(navigationRoot));
            stage.setTitle("EcoDeli - Tableau de bord");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void handleGoToMerchants(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/merchant/merchantView.fxml"));
            Parent merchantView = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(merchantView));
            stage.setTitle("Liste des Commerçants");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void handleGoToClients(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/client/clientView.fxml"));
            Parent clientView = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(clientView));
            stage.setTitle("Liste des Clients");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleGoToDeliveries(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/delivery/DeliveryTableView.fxml"));
            Parent livraisonView = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(livraisonView));
            stage.setTitle("Liste des Livraisons");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleGoToServices(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/service/ServicesView.fxml"));
            Parent view = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(view));
            stage.setTitle("Liste des Prestations");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
