package fr.ecodeli.ecodelidesktop.controller;

import fr.ecodeli.ecodelidesktop.dashboard.PdfMergerService;
import fr.ecodeli.ecodelidesktop.dashboard.StatsPrestationsViewController;
import fr.ecodeli.ecodelidesktop.dashboard.StatsViewController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Button btnStats, btnClients, btnMerchants, btnDeliveries, btnServices, btnGeneratePdf;

    // Références aux contrôleurs pour la génération PDF
    private StatsViewController statsViewController;
    private StatsPrestationsViewController statsPrestationsViewController;

    @FXML
    public void initialize() {
        btnStats.setOnAction(e -> loadPage("/fr/ecodeli/ecodelidesktop/view/dashboard/StatsWrapperView.fxml"));
        btnClients.setOnAction(e -> loadPage("/fr/ecodeli/ecodelidesktop/view/client/ClientView.fxml"));
        btnMerchants.setOnAction(e -> loadPage("/fr/ecodeli/ecodelidesktop/view/merchant/MerchantView.fxml"));
        btnDeliveries.setOnAction(e -> loadPage("/fr/ecodeli/ecodelidesktop/view/delivery/DeliveryTableView.fxml"));
        btnServices.setOnAction(e -> loadPage("/fr/ecodeli/ecodelidesktop/view/service/servicesView.fxml"));

        // Action pour générer le PDF complet
        btnGeneratePdf.setOnAction(e -> generateCompletePdf());

        loadPage("/fr/ecodeli/ecodelidesktop/view/dashboard/StatsWrapperView.fxml");

        // Initialiser les contrôleurs de stats
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
        alert.showAndWait();
    }
}