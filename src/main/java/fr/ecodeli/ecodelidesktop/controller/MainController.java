package fr.ecodeli.ecodelidesktop.controller;

import fr.ecodeli.ecodelidesktop.dashboard.PdfMergerService;
import fr.ecodeli.ecodelidesktop.dashboard.StatsPrestationsViewController;
import fr.ecodeli.ecodelidesktop.dashboard.StatsViewController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Button btnStats, btnClients, btnMerchants, btnDeliveries, btnServices, btnGeneratePdf, btnLogout, btnWarehouse;
    private static StackPane staticContentArea;

    private StatsViewController statsViewController;
    private StatsPrestationsViewController statsPrestationsViewController;

    @FXML
    public void initialize() {

        staticContentArea = contentArea;
        btnStats.setOnAction(e -> loadPage("/fr/ecodeli/ecodelidesktop/view/dashboard/StatsWrapperView.fxml"));
        btnClients.setOnAction(e -> loadPage("/fr/ecodeli/ecodelidesktop/view/client/ClientView.fxml"));
        btnMerchants.setOnAction(e -> loadPage("/fr/ecodeli/ecodelidesktop/view/merchant/MerchantView.fxml"));
        btnDeliveries.setOnAction(e -> loadPage("/fr/ecodeli/ecodelidesktop/view/delivery/DeliveryTableView.fxml"));
        btnServices.setOnAction(e -> loadPage("/fr/ecodeli/ecodelidesktop/view/service/ServicesView.fxml"));
        btnWarehouse.setOnAction(e -> {loadPage("/fr/ecodeli/ecodelidesktop/view/warehouse/WarehouseView.fxml");});
        btnLogout.setOnAction(e -> handleLogout());
        btnGeneratePdf.setOnAction(e -> generateCompletePdf());

        loadPage("/fr/ecodeli/ecodelidesktop/view/dashboard/StatsWrapperView.fxml");

        initializeStatsControllers();
    }

    private void loadPage(String fxmlPath) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(fxml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeStatsControllers() {
        try {
            statsViewController = new StatsViewController();
            statsPrestationsViewController = new StatsPrestationsViewController();

            statsViewController.initialize();
            statsPrestationsViewController.initialize();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'initialisation des contrôleurs de stats");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/auth/AuthView.fxml"));
            Parent loginRoot = loader.load();

            Stage stage = (Stage) contentArea.getScene().getWindow();

            Scene scene = new Scene(loginRoot);
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/auth/auth-style.css")).toExternalForm()
            );

            stage.setScene(scene);
            stage.setTitle("Connexion - EcoDeli");
            stage.getIcons().clear();
            stage.getIcons().add(new Image(
                    Objects.requireNonNull(getClass().getResourceAsStream("/fr/ecodeli/ecodelidesktop/view/global/ecodeli.png"))
            ));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setContent(Parent node) {
        staticContentArea.getChildren().setAll(node);
    }

    @FXML
    private void generateCompletePdf() {
        try {
            if (statsViewController == null || statsPrestationsViewController == null) {
                showAlert("Erreur", "Les contrôleurs de statistiques ne sont pas initialisés.", Alert.AlertType.ERROR);
                return;
            }

            showAlert("Génération en cours", "Le rapport complet est en cours de génération...", Alert.AlertType.INFORMATION);

            PdfMergerService pdfMerger = new PdfMergerService(statsViewController, statsPrestationsViewController);

            pdfMerger.generateCompletePdf();

            showAlert("Succès", "Le rapport complet a été généré avec succès dans le dossier Téléchargements !", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de la génération du rapport : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/fr/ecodeli/ecodelidesktop/view/global/ecodeli.png"))));
        alert.showAndWait();
    }
}