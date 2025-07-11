package fr.ecodeli.ecodelidesktop.warehouse;

import fr.ecodeli.ecodelidesktop.api.WarehouseAPI;
import fr.ecodeli.ecodelidesktop.api.WarehouseAPI.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class WarehouseFormController implements Initializable {

    @FXML private Label formTitle;
    @FXML private TextField cityField;
    @FXML private TextField addressField;
    @FXML private TextField postalCodeField;
    @FXML private TextField capacityField;
    @FXML private TextArea descriptionField;
    @FXML private Button selectFileButton;
    @FXML private Label fileLabel;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    private final WarehouseAPI warehouseAPI = new WarehouseAPI();
    private Stage modalStage;
    private Warehouse currentWarehouse;
    private File selectedFile;
    private Runnable onWarehouseCreated;
    private Runnable onWarehouseUpdated;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupEventHandlers();
        setupFieldValidation();
    }

    private void setupEventHandlers() {
        cancelButton.setOnAction(e -> modalStage.close());
        saveButton.setOnAction(e -> saveWarehouse());
        selectFileButton.setOnAction(e -> selectFile());
    }

    private void setupFieldValidation() {
        capacityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                capacityField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        postalCodeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 5) {
                postalCodeField.setText(oldValue);
            }
        });
    }

    public void setModalStage(Stage modalStage) {
        this.modalStage = modalStage;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.currentWarehouse = warehouse;
        if (warehouse != null) {
            formTitle.setText("Modifier l'entrepôt");
            populateFields(warehouse);
        } else {
            formTitle.setText("Ajouter un entrepôt");
        }
    }

    private void populateFields(Warehouse warehouse) {
        cityField.setText(warehouse.getCity());
        addressField.setText(warehouse.getAddress());
        postalCodeField.setText(warehouse.getPostalCode());
        capacityField.setText(String.valueOf(warehouse.getCapacity()));
        descriptionField.setText(warehouse.getDescription());
    }

    private void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        selectedFile = fileChooser.showOpenDialog(modalStage);
        if (selectedFile != null) {
            fileLabel.setText(selectedFile.getName());
        }
    }

    private void saveWarehouse() {
        if (!validateFields()) {
            return;
        }

        saveButton.setDisable(true);
        saveButton.setText("Enregistrement...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String coordinatesJson = WarehouseAPI.coordinatesToPointJson(0.000, 0.000);

                if (currentWarehouse == null) {
                    WarehouseCreateRequest request = new WarehouseCreateRequest();
                    request.city = cityField.getText().trim();
                    request.address = addressField.getText().trim();
                    request.postalCode = postalCodeField.getText().trim();
                    request.capacity = Integer.parseInt(capacityField.getText().trim());
                    request.coordinates = coordinatesJson;
                    request.description = descriptionField.getText().trim();

                    warehouseAPI.createWarehouse(request, selectedFile);
                } else {
                    WarehouseUpdateRequest request = new WarehouseUpdateRequest();
                    request.city = cityField.getText().trim();
                    request.address = addressField.getText().trim();
                    request.postalCode = postalCodeField.getText().trim();
                    request.capacity = Integer.parseInt(capacityField.getText().trim());
                    request.coordinates = coordinatesJson;
                    request.description = descriptionField.getText().trim();

                    warehouseAPI.updateWarehouse(currentWarehouse.getId(), request, selectedFile);
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                if (currentWarehouse == null && onWarehouseCreated != null) {
                    onWarehouseCreated.run();
                } else if (currentWarehouse != null && onWarehouseUpdated != null) {
                    onWarehouseUpdated.run();
                }
                modalStage.close();
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                saveButton.setDisable(false);
                saveButton.setText("Enregistrer");

                Throwable exception = task.getException();
                showErrorAlert("Erreur", "Impossible d'enregistrer l'entrepôt: " + exception.getMessage());
            });
        });

        new Thread(task).start();
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (cityField.getText().trim().isEmpty()) {
            errors.append("- La ville est obligatoire\n");
        }

        if (addressField.getText().trim().isEmpty()) {
            errors.append("- L'adresse est obligatoire\n");
        }

        if (postalCodeField.getText().trim().isEmpty()) {
            errors.append("- Le code postal est obligatoire\n");
        } else if (!postalCodeField.getText().trim().matches("\\d{5}")) {
            errors.append("- Le code postal doit contenir 5 chiffres\n");
        }

        if (capacityField.getText().trim().isEmpty()) {
            errors.append("- La capacité est obligatoire\n");
        } else {
            try {
                int capacity = Integer.parseInt(capacityField.getText().trim());
                if (capacity <= 0) {
                    errors.append("- La capacité doit être supérieure à 0\n");
                }
            } catch (NumberFormatException e) {
                errors.append("- La capacité doit être un nombre valide\n");
            }
        }

        if (errors.length() > 0) {
            showErrorAlert("Erreurs de validation", errors.toString());
            return false;
        }

        return true;
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setOnWarehouseCreated(Runnable onWarehouseCreated) {
        this.onWarehouseCreated = onWarehouseCreated;
    }

    public void setOnWarehouseUpdated(Runnable onWarehouseUpdated) {
        this.onWarehouseUpdated = onWarehouseUpdated;
    }
}