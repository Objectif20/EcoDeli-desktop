package fr.ecodeli.ecodelidesktop.clients;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import fr.ecodeli.ecodelidesktop.controller.MainController;

import java.io.IOException;

public class ClientDetailController {

    @FXML private Label clientIdLabel;

    private String clientId;

    public void setClientId(String id) {
        this.clientId = id;
        System.out.println("Client ID reçu : " + clientId);
        clientIdLabel.setText("Détail du client ID: " + clientId);
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
