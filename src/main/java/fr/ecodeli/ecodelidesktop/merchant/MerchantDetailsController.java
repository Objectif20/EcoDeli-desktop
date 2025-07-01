package fr.ecodeli.ecodelidesktop.merchant;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import fr.ecodeli.ecodelidesktop.api.MerchantAPI;
import fr.ecodeli.ecodelidesktop.controller.MainController;

import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;


public class MerchantDetailsController {

    @FXML private Label merchantNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label cityLabel;
    @FXML private Label companyNameLabel;
    @FXML private Label addressLabel;
    @FXML private Label subscriptionLabel;
    @FXML private Label deliveryRequestsLabel;
    @FXML private Label statusLabel;
    @FXML private ScrollPane pdfScrollPane;
    @FXML private ImageView pdfImageView;
    @FXML private VBox noPdfMessage;
    @FXML private Button downloadButton;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageLabel;
    @FXML private Label zoomLabel;

    private String clientId;
    private final MerchantAPI merchantAPI;
    private MerchantAPI.MerchantDetails merchantDetails;

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
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                showError("Erreur lors du chargement des détails du commerçant");
            });
        });

        new Thread(task).start();
    }

    private void displayMerchantDetails() {
        if (merchantDetails == null) return;

        String merchantName = "";
        if (merchantDetails.getFirstName() != null && !merchantDetails.getFirstName().isEmpty()) {
            merchantName += merchantDetails.getFirstName();
        }
        if (merchantDetails.getLastName() != null && !merchantDetails.getLastName().isEmpty()) {
            if (!merchantName.isEmpty()) merchantName += " ";
            merchantName += merchantDetails.getLastName();
        }
        if (merchantName.isEmpty()) merchantName = "Nom non renseigné";
        merchantNameLabel.setText(merchantName);

        String email = merchantDetails.getEmail() != null && !merchantDetails.getEmail().isEmpty()
                ? merchantDetails.getEmail() : "Non renseigné";
        emailLabel.setText("📧 " + email);

        String phone = merchantDetails.getPhone() != null && !merchantDetails.getPhone().isEmpty()
                ? merchantDetails.getPhone() : "Non renseigné";
        phoneLabel.setText("📱 " + phone);

        String city = merchantDetails.getCity() != null && !merchantDetails.getCity().isEmpty()
                ? merchantDetails.getCity() : "Non renseignée";
        cityLabel.setText("📍 " + city);

        String companyName = merchantDetails.getCompanyName() != null && !merchantDetails.getCompanyName().isEmpty()
                ? merchantDetails.getCompanyName() : "Non renseigné";
        companyNameLabel.setText(companyName);

        String address = merchantDetails.getAddress() != null && !merchantDetails.getAddress().isEmpty()
                ? merchantDetails.getAddress() : "Non renseignée";
        addressLabel.setText(address);

        String subscription = merchantDetails.getNomAbonnement() != null && !merchantDetails.getNomAbonnement().isEmpty()
                ? merchantDetails.getNomAbonnement() : "Non renseigné";
        subscriptionLabel.setText(subscription);

        deliveryRequestsLabel.setText("🚚 " + merchantDetails.getNbDemandeDeLivraison());

        statusLabel.setText("Actif");
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
                showError("Aucun PDF n'est disponible");
                showNoPdfMessage();
            });
        });

        new Thread(pdfTask).start();
    }

    private void displayPdf(byte[] pdfData) {
        try {
            if (currentDocument != null) {
                currentDocument.close();
            }

            currentDocument = PDDocument.load(new ByteArrayInputStream(pdfData));
            pdfRenderer = new PDFRenderer(currentDocument);
            totalPages = currentDocument.getNumberOfPages();
            currentPage = 0;
            zoomLevel = 0.8f;

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
                float dpi = 72f * zoomLevel * 2f;
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
                showError("Aucun PDF n'est disponible");
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/fr/ecodeli/ecodelidesktop/view/global/ecodeli.png"))));
        alert.showAndWait();
    }

    @FXML
    private void handleRetour() {
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
                showInfo("Contrat téléchargé avec succès dans : " + file.getAbsolutePath());
            });
        });

        downloadTask.setOnFailed(e -> {
            Platform.runLater(() -> {
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