package fr.ecodeli.ecodelidesktop.delivery;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import fr.ecodeli.ecodelidesktop.controller.MainController;
import fr.ecodeli.ecodelidesktop.api.DeliveryAPI;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DeliveryDetailsController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private Label deliveryIdLabel;
    @FXML private Label departureLabel;
    @FXML private Label arrivalLabel;
    @FXML private Label departureDateLabel;
    @FXML private Label arrivalDateLabel;
    @FXML private Label totalPriceLabel;
    @FXML private Label cartDroppedLabel;
    @FXML private Label cartDroppedIcon;
    @FXML private Label packageCountLabel;
    @FXML private VBox packagesContainer;
    @FXML private VBox priceBreakdownContainer;

    private String deliveryId;
    private DeliveryAPI deliveryAPI;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.deliveryAPI = new DeliveryAPI();
    }

    public void setDeliveryId(String id) {
        this.deliveryId = id;
        loadDeliveryDetails();
    }

    private void loadDeliveryDetails() {
        try {
            DeliveryAPI.DeliveryDetails details = deliveryAPI.getDeliveryById(deliveryId);

            // Header
            titleLabel.setText("Détails de la livraison");
            deliveryIdLabel.setText("Référence: " + deliveryId);

            // Itinéraire
            departureLabel.setText(details.departure.city + " - " + details.departure.address);
            arrivalLabel.setText(details.arrival.city + " - " + details.arrival.address);
            departureDateLabel.setText(formatDate(details.departure_date));
            arrivalDateLabel.setText(details.arrival_date != null ? formatDate(details.arrival_date) : "À déterminer");

            // Prix et statut
            totalPriceLabel.setText(String.format("%.2f €", details.total_price));

            if (details.cart_dropped) {
                cartDroppedLabel.setText("Oui");
                cartDroppedIcon.setText("✓");
                cartDroppedLabel.getStyleClass().add("success");
                cartDroppedIcon.getStyleClass().add("success");
            } else {
                cartDroppedLabel.setText("Non");
                cartDroppedIcon.setText("✗");
                cartDroppedLabel.getStyleClass().add("error");
                cartDroppedIcon.getStyleClass().add("error");
            }

            // Nombre de colis
            packageCountLabel.setText("(" + details.packages.size() + ")");

            // Chargement des colis et prix détaillé
            loadPackages(details.packages);
            loadPriceBreakdown(details.packages);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPackages(java.util.List<DeliveryAPI.DeliveryDetails.Package> packages) {
        packagesContainer.getChildren().clear();

        for (DeliveryAPI.DeliveryDetails.Package pkg : packages) {
            HBox packageBox = createModernPackageBox(pkg);
            packagesContainer.getChildren().add(packageBox);
        }
    }

    private HBox createModernPackageBox(DeliveryAPI.DeliveryDetails.Package pkg) {
        HBox packageBox = new HBox(20);
        packageBox.getStyleClass().add("package-box");

        // Image du colis
        ImageView imageView = new ImageView();
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
        imageView.setPreserveRatio(true);

        if (pkg.picture != null && !pkg.picture.isEmpty()) {
            try {
                Image image = new Image(pkg.picture.get(0), true);
                imageView.setImage(image);
            } catch (Exception e) {
                imageView.setImage(new Image("/icons/package-default.png"));
            }
        } else {
            imageView.setImage(new Image("/icons/package-default.png"));
        }

        // Informations du colis
        VBox infoBox = new VBox(8);
        infoBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS);

        Label nameLabel = new Label(pkg.name);
        nameLabel.getStyleClass().add("package-name");

        HBox detailsBox = new HBox(15);
        Label weightLabel = new Label(String.format("%.1f kg", pkg.weight));
        weightLabel.getStyleClass().add("package-detail");
        Label volumeLabel = new Label(String.format("%.3f m³", pkg.volume));
        volumeLabel.getStyleClass().add("package-detail");

        detailsBox.getChildren().addAll(weightLabel, volumeLabel);

        if (pkg.fragility) {
            Label fragilityLabel = new Label("FRAGILE");
            fragilityLabel.getStyleClass().add("fragile-badge");
            detailsBox.getChildren().add(fragilityLabel);
        }

        infoBox.getChildren().addAll(nameLabel, detailsBox);

        // Prix
        VBox priceBox = new VBox(5);
        priceBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Label priceLabel = new Label(String.format("%.2f €", pkg.estimated_price));
        priceLabel.getStyleClass().add("package-price");

        priceBox.getChildren().add(priceLabel);

        packageBox.getChildren().addAll(imageView, infoBox, priceBox);

        return packageBox;
    }

    private void loadPriceBreakdown(java.util.List<DeliveryAPI.DeliveryDetails.Package> packages) {
        priceBreakdownContainer.getChildren().clear();

        for (DeliveryAPI.DeliveryDetails.Package pkg : packages) {
            HBox priceItem = new HBox(10);
            priceItem.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Label nameLabel = new Label(pkg.name);
            nameLabel.getStyleClass().add("price-breakdown-item");
            nameLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(nameLabel, javafx.scene.layout.Priority.ALWAYS);

            Label priceLabel = new Label(String.format("%.2f €", pkg.estimated_price));
            priceLabel.getStyleClass().add("price-breakdown-value");

            priceItem.getChildren().addAll(nameLabel, priceLabel);
            priceBreakdownContainer.getChildren().add(priceItem);
        }
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/delivery/DeliveryTableView.fxml"));
            Parent tableView = loader.load();

            DeliveryTableController tableController = loader.getController();
            tableController.initialize();

            MainController.setContent(tableView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
