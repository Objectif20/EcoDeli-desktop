package fr.ecodeli.ecodelidesktop.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.scene.control.Button;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Button btnStats, btnClients, btnMerchants, btnDeliveries, btnServices;

    @FXML
    public void initialize() {
        btnStats.setOnAction(e -> loadPage("/fr/ecodeli/ecodelidesktop/view/dashboard/StatsView.fxml"));
        btnClients.setOnAction(e -> loadPage("/fr/ecodeli/ecodelidesktop/view/client/ClientView.fxml"));
        btnMerchants.setOnAction(e -> loadPage("/fr/ecodeli/ecodelidesktop/view/merchant/MerchantView.fxml"));
        btnDeliveries.setOnAction(e -> loadPage("/fr/ecodeli/ecodelidesktop/view/delivery/DeliveryTableView.fxml"));
        btnServices.setOnAction(e -> loadPage("/fr/ecodeli/ecodelidesktop/view/service/servicesView.fxml"));
        loadPage("/fr/ecodeli/ecodelidesktop/view/dashboard/StatsView.fxml");
    }

    private void loadPage(String fxmlPath) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(fxml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
