package fr.ecodeli.ecodelidesktop.merchant;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import fr.ecodeli.ecodelidesktop.api.MerchantAPI;
import fr.ecodeli.ecodelidesktop.controller.MainController;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class MerchantDetailsController {

    @FXML private Label titleLabel;
    @FXML private Label idLabel;
    @FXML private Label firstNameLabel;
    @FXML private Label lastNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label cityLabel;
    @FXML private Label companyNameLabel;
    @FXML private Label addressLabel;
    @FXML private Label subscriptionLabel;
    @FXML private Label deliveryRequestsLabel;
    @FXML private VBox contractSection;
    @FXML private ScrollPane pdfScrollPane;
    @FXML private ImageView pdfImageView;
    @FXML private VBox noPdfMessage;
    @FXML private Button downloadButton;
    @FXML private Button refreshPdfButton;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Button zoomInButton;
    @FXML private Button zoomOutButton;
    @FXML private Label pageLabel;
    @FXML private Label zoomLabel;
    @FXML private StackPane loadingPane;

    private String clientId;
    private MerchantAPI merchantAPI;
    private MerchantAPI.MerchantDetails merchantDetails;

    // PDF viewer state
    private PDDocument currentDocument;
    private PDFRenderer pdfRenderer;
    private int currentPage = 0;
    private int totalPages = 0;
    private float zoomLevel = 1.0f;
    private byte[] pdfData;

    public MerchantDetailsController() {
        this.merchantAPI = new MerchantAPI();
    }

    public void setClientId(String id) {
        this.clientId = id;
        loadMerchantDetails();
    }

    private void loadMerchantDetails() {
        showLoading(true);

        Task<MerchantAPI.MerchantDetails> task = new Task<MerchantAPI.MerchantDetails>() {
            @Override
            protected MerchantAPI.MerchantDetails call() throws Exception {
                return merchantAPI.getMerchantById(clientId);
            }
        };

        task.setOnSucceeded(e -> {
            merchantDetails = task.getValue();
            Platform.runLater(() -> {
                displayMerchantDetails();
                loadPdfContract();
                showLoading(false);
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                showError("Erreur lors du chargement des détails du commerçant");
                showLoading(false);
            });
        });

        new Thread(task).start();
    }

    private void displayMerchantDetails() {
        if (merchantDetails == null) return;

        titleLabel.setText("Détails du Commerçant - " +
                (merchantDetails.getCompanyName() != null && !merchantDetails.getCompanyName().isEmpty()
                        ? merchantDetails.getCompanyName()
                        : merchantDetails.getFirstName() + " " + merchantDetails.getLastName()));

        idLabel.setText(merchantDetails.getId() != null ? merchantDetails.getId() : "-");
        firstNameLabel.setText(merchantDetails.getFirstName() != null ? merchantDetails.getFirstName() : "-");
        lastNameLabel.setText(merchantDetails.getLastName() != null ? merchantDetails.getLastName() : "-");
        emailLabel.setText(merchantDetails.getEmail() != null ? merchantDetails.getEmail() : "-");
        phoneLabel.setText(merchantDetails.getPhone() != null ? merchantDetails.getPhone() : "-");
        cityLabel.setText(merchantDetails.getCity() != null ? merchantDetails.getCity() : "-");
        companyNameLabel.setText(merchantDetails.getCompanyName() != null ? merchantDetails.getCompanyName() : "-");
        addressLabel.setText(merchantDetails.getAddress() != null ? merchantDetails.getAddress() : "-");
        subscriptionLabel.setText(merchantDetails.getNomAbonnement() != null ? merchantDetails.getNomAbonnement() : "-");
        deliveryRequestsLabel.setText(String.valueOf(merchantDetails.getNbDemandeDeLivraison()));
    }

    private void loadPdfContract() {
        if (merchantDetails == null || merchantDetails.getContractUrl() == null || merchantDetails.getContractUrl().isEmpty()) {
            showNoPdfMessage();
            return;
        }

        Task<byte[]> pdfTask = new Task<byte[]>() {
            @Override
            protected byte[] call() throws Exception {
                return merchantAPI.downloadDocument(merchantDetails.getContractUrl());
            }
        };

        pdfTask.setOnSucceeded(e -> {
            pdfData = pdfTask.getValue();
            Platform.runLater(() -> displayPdf(pdfData));
        });

        pdfTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                showError("Erreur lors du chargement du contrat PDF");
                showNoPdfMessage();
            });
        });

        new Thread(pdfTask).start();
    }

    private void displayPdf(byte[] pdfData) {
        try {
            // Fermer le document précédent s'il existe
            if (currentDocument != null) {
                currentDocument.close();
            }

            // Charger le nouveau document PDF
            currentDocument = PDDocument.load(new ByteArrayInputStream(pdfData));
            pdfRenderer = new PDFRenderer(currentDocument);
            totalPages = currentDocument.getNumberOfPages();
            currentPage = 0;
            zoomLevel = 1.0f;

            // Afficher la première page
            renderCurrentPage();
            updateUI();

            pdfScrollPane.setVisible(true);
            noPdfMessage.setVisible(false);
            downloadButton.setDisable(false);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de l'affichage du PDF : " + e.getMessage());
            showNoPdfMessage();
        }
    }

    private void renderCurrentPage() {
        if (pdfRenderer == null || currentPage >= totalPages) return;

        Task<BufferedImage> renderTask = new Task<BufferedImage>() {
            @Override
            protected BufferedImage call() throws Exception {
                // Calculer la résolution basée sur le zoom (72 DPI * zoom = résolution finale)
                float dpi = 72f * zoomLevel * 2f; // Factor 2 pour une meilleure qualité
                return pdfRenderer.renderImageWithDPI(currentPage, dpi);
            }
        };

        renderTask.setOnSucceeded(e -> {
            BufferedImage bufferedImage = renderTask.getValue();
            Platform.runLater(() -> {
                javafx.scene.image.Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
                pdfImageView.setImage(fxImage);
            });
        });

        renderTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                showError("Erreur lors du rendu de la page PDF");
            });
        });

        new Thread(renderTask).start();
    }

    private void updateUI() {
        pageLabel.setText("Page " + (currentPage + 1) + " / " + totalPages);
        prevPageButton.setDisable(currentPage == 0);
        nextPageButton.setDisable(currentPage >= totalPages - 1);
        zoomLabel.setText(Math.round(zoomLevel * 100) + "%");
    }

    private void showNoPdfMessage() {
        pdfScrollPane.setVisible(false);
        noPdfMessage.setVisible(true);
        downloadButton.setDisable(true);

        // Réinitialiser les contrôles
        pageLabel.setText("Page 0 / 0");
        prevPageButton.setDisable(true);
        nextPageButton.setDisable(true);
        zoomLabel.setText("100%");
    }

    @FXML
    private void handlePrevPage() {
        if (currentPage > 0) {
            currentPage--;
            renderCurrentPage();
            updateUI();
        }
    }

    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            renderCurrentPage();
            updateUI();
        }
    }

    @FXML
    private void handleZoomIn() {
        if (zoomLevel < 3.0f) {
            zoomLevel += 0.25f;
            renderCurrentPage();
            updateUI();
        }
    }

    @FXML
    private void handleZoomOut() {
        if (zoomLevel > 0.25f) {
            zoomLevel -= 0.25f;
            renderCurrentPage();
            updateUI();
        }
    }

    private void showLoading(boolean show) {
        loadingPane.setVisible(show);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleRetour() {
        // Fermer le document PDF avant de quitter
        if (currentDocument != null) {
            try {
                currentDocument.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/merchant/MerchantView.fxml"));
            Parent tableView = loader.load();

            MerchantTableController tableController = loader.getController();
            tableController.initialize();

            MainController.setContent(tableView);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du retour à la liste des commerçants");
        }
    }

    @FXML
    private void handleDownloadContract() {
        if (pdfData == null) {
            showError("Aucun contrat disponible pour le téléchargement");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le contrat");
        fileChooser.setInitialFileName("contrat_" + merchantDetails.getId() + ".pdf");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File file = fileChooser.showSaveDialog(downloadButton.getScene().getWindow());
        if (file != null) {
            downloadContractToFile(file);
        }
    }

    private void downloadContractToFile(File file) {
        showLoading(true);

        Task<Void> downloadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(pdfData);
                }
                return null;
            }
        };

        downloadTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                showLoading(false);
                showInfo("Contrat téléchargé avec succès dans : " + file.getAbsolutePath());
            });
        });

        downloadTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                showLoading(false);
                showError("Erreur lors du téléchargement du contrat");
            });
        });

        new Thread(downloadTask).start();
    }

    @FXML
    private void handleRefreshPdf() {
        loadPdfContract();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour nettoyer les ressources lors de la fermeture
    public void cleanup() {
        if (currentDocument != null) {
            try {
                currentDocument.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}