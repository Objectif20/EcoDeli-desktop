package fr.ecodeli.ecodelidesktop.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class NavigationController {

    @FXML
    private AnchorPane mainContent;

    @FXML
    public void goToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/dashboard/StatsView.fxml"));
            Parent navigationRoot = loader.load();
            AnchorPane.setTopAnchor(navigationRoot, 0.0);
            AnchorPane.setBottomAnchor(navigationRoot, 0.0);
            AnchorPane.setLeftAnchor(navigationRoot, 0.0);
            AnchorPane.setRightAnchor(navigationRoot, 0.0);
            mainContent.getChildren().setAll(navigationRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToClients() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/client/ClientView.fxml"));
            Parent navigationRoot = loader.load();
            AnchorPane.setTopAnchor(navigationRoot, 0.0);
            AnchorPane.setBottomAnchor(navigationRoot, 0.0);
            AnchorPane.setLeftAnchor(navigationRoot, 0.0);
            AnchorPane.setRightAnchor(navigationRoot, 0.0);
            mainContent.getChildren().setAll(navigationRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToMerchants() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/merchant/MerchantView.fxml"));
            Parent navigationRoot = loader.load();
            AnchorPane.setTopAnchor(navigationRoot, 0.0);
            AnchorPane.setBottomAnchor(navigationRoot, 0.0);
            AnchorPane.setLeftAnchor(navigationRoot, 0.0);
            AnchorPane.setRightAnchor(navigationRoot, 0.0);
            mainContent.getChildren().setAll(navigationRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToDeliveries() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/delivery/DeliveryTableView.fxml"));
            Parent navigationRoot = loader.load();
            AnchorPane.setTopAnchor(navigationRoot, 0.0);
            AnchorPane.setBottomAnchor(navigationRoot, 0.0);
            AnchorPane.setLeftAnchor(navigationRoot, 0.0);
            AnchorPane.setRightAnchor(navigationRoot, 0.0);
            mainContent.getChildren().setAll(navigationRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToServices() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/service/ServicesView.fxml"));
            Parent navigationRoot = loader.load();
            AnchorPane.setTopAnchor(navigationRoot, 0.0);
            AnchorPane.setBottomAnchor(navigationRoot, 0.0);
            AnchorPane.setLeftAnchor(navigationRoot, 0.0);
            AnchorPane.setRightAnchor(navigationRoot, 0.0);
            mainContent.getChildren().setAll(navigationRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
