package fr.ecodeli.ecodelidesktop.merchant;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import fr.ecodeli.ecodelidesktop.controller.MainController;

import java.io.IOException;

public class MerchantDetailsController {

    @FXML private Label merchantIdLabel;

    private String clientId;

    public void setClientId(String id) {
        this.clientId = id;
        System.out.println("Client ID reçu : " + clientId);
        merchantIdLabel.setText("Détail du client ID: " + clientId);
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/merchant/MerchantView.fxml"));
            Parent tableView = loader.load();

            MerchantTableController tableController = loader.getController();
            tableController.initialize();

            MainController.setContent(tableView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
