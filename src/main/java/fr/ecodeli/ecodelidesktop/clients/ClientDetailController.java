package fr.ecodeli.ecodelidesktop.clients;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import fr.ecodeli.ecodelidesktop.controller.MainController;
import fr.ecodeli.ecodelidesktop.api.ClientAPI;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientDetailController implements Initializable {
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label initialsLabel;
    @FXML private Label subscriptionBadge;
    @FXML private Label transporterBadge;
    @FXML private Label livraisonsCount;
    @FXML private Label prestationsCount;
    @FXML private Label signalementsCount;
    @FXML private Label abonnementLabel;
    @FXML private Label contactEmailLabel;
    @FXML private Label transporterStatusLabel;
    @FXML private VBox transporterStatusContainer;
    @FXML private Label transporterStatusIcon;

    private String clientId;
    private ClientAPI clientAPI;

    public ClientDetailController() {
        this.clientAPI = new ClientAPI();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setClientId(String id) {
        this.clientId = id;
        System.out.println("Client ID reçu : " + clientId);
        loadClientDetails();
    }

    private void loadClientDetails() {
        try {
            ClientAPI.ClientDetails clientDetails = clientAPI.getClientById(clientId);
            ClientAPI.ClientDetails.Info info = clientDetails.getInfo();
            String fullName = info.getFirstName() + " " + info.getLastName();
            nameLabel.setText(fullName);
            emailLabel.setText(info.getEmail());
            contactEmailLabel.setText(info.getEmail());
            String initials = getInitials(info.getFirstName(), info.getLastName());
            initialsLabel.setText(initials);
            subscriptionBadge.setText(info.getNomAbonnement());
            abonnementLabel.setText(info.getNomAbonnement());
            livraisonsCount.setText(String.valueOf(info.getNbDemandeDeLivraison()));
            prestationsCount.setText(String.valueOf(info.getNombreDePrestations()));
            signalementsCount.setText(String.valueOf(info.getNbSignalements()));

            if (info.isProfilTransporteur()) {
                transporterBadge.setVisible(true);
                transporterBadge.setText("Statut transporteur");
                transporterStatusContainer.setVisible(true);
                String status = (info.getIdTransporteur() != null && !info.getIdTransporteur().isEmpty()) ? "Actif" : "Inactif";
                transporterStatusLabel.setText(status);
                if ("Actif".equals(status)) {
                    transporterStatusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
                    transporterStatusIcon.setStyle("-fx-text-fill: #2e7d32;");
                } else {
                    transporterStatusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #d32f2f;");
                    transporterStatusIcon.setStyle("-fx-text-fill: #d32f2f;");
                }
            }
            updateSubscriptionBadgeStyle(info.getNomAbonnement());
        } catch (IOException e) {
            e.printStackTrace();
            nameLabel.setText("Erreur lors du chargement");
            emailLabel.setText("Impossible de charger les données");
        }
    }

    private String getInitials(String firstName, String lastName) {
        String firstInitial = (firstName != null && !firstName.isEmpty()) ? firstName.substring(0, 1).toUpperCase() : "";
        String lastInitial = (lastName != null && !lastName.isEmpty()) ? lastName.substring(0, 1).toUpperCase() : "";
        return firstInitial + lastInitial;
    }

    private void updateSubscriptionBadgeStyle(String subscription) {
        if (subscription == null || subscription.isBlank()) return;
        String label = subscription.substring(0, 1).toUpperCase() + subscription.substring(1);
        subscriptionBadge.setText(label);
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/client/ClientView.fxml"));
            Parent tableView = loader.load();
            ClientTableController tableController = loader.getController();
            tableController.initialize();
            MainController.setContent(tableView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
