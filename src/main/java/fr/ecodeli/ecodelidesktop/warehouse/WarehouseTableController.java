package fr.ecodeli.ecodelidesktop.warehouse;

import fr.ecodeli.ecodelidesktop.api.WarehouseAPI;
import fr.ecodeli.ecodelidesktop.api.WarehouseAPI.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WarehouseTableController implements Initializable {

    @FXML public TableView<Warehouse> warehouseTable;
    @FXML public TableColumn<Warehouse, String> cityColumn;
    @FXML public TableColumn<Warehouse, String> addressColumn;
    @FXML public TableColumn<Warehouse, String> postalCodeColumn;
    @FXML public TableColumn<Warehouse, Integer> capacityColumn;
    @FXML public TableColumn<Warehouse, String> descriptionColumn;
    @FXML public TableColumn<Warehouse, Void> actionsColumn;

    @FXML public Button addWarehouseButton;
    @FXML public Label totalWarehousesLabel;

    private final WarehouseAPI warehouseAPI = new WarehouseAPI();
    private final ObservableList<Warehouse> warehouseList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupAddButton();
        loadWarehouses();
    }

    private void setupTableColumns() {
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setCellFactory(column -> new TableCell<Warehouse, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    String displayText = item.length() > 50 ? item.substring(0, 50) + "..." : item;
                    setText(displayText);

                    if (item.length() > 50) {
                        Tooltip tooltip = new Tooltip(item);
                        tooltip.setWrapText(true);
                        tooltip.setMaxWidth(400);
                        tooltip.setShowDelay(Duration.millis(500));
                        setTooltip(tooltip);
                    } else {
                        setTooltip(null);
                    }
                }
            }
        });

        setupActionsColumn();
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox buttonBox = new HBox(5, editButton, deleteButton);

            {
                editButton.getStyleClass().add("action-button");
                deleteButton.getStyleClass().addAll("action-button", "delete-button");

                editButton.setOnAction(e -> {
                    Warehouse warehouse = getTableView().getItems().get(getIndex());
                    openEditWarehouseModal(warehouse);
                });

                deleteButton.setOnAction(e -> {
                    Warehouse warehouse = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(warehouse);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
            }
        });
    }

    private void setupAddButton() {
        addWarehouseButton.setOnAction(e -> openAddWarehouseModal());
    }

    private void loadWarehouses() {
        Task<WarehouseResponse> task = new Task<>() {
            @Override
            protected WarehouseResponse call() throws Exception {
                return warehouseAPI.getAllWarehouses();
            }
        };

        task.setOnSucceeded(e -> {
            WarehouseResponse response = task.getValue();
            warehouseList.clear();
            if (response.getData() != null) {
                for (Warehouse warehouse : response.getData()) {
                    System.out.println("Warehouse: " + warehouse.getCity() + ", " + warehouse.getAddress() + ", " + warehouse.getCapacity() + ", " + warehouse.getDescription());
                }
                warehouseList.addAll(response.getData());
            }
            warehouseTable.setItems(warehouseList);
            updateTotalLabel(response.getTotalRows());
        });

        task.setOnFailed(e -> {
            Throwable exception = task.getException();
            exception.printStackTrace(); // Pour debug
            showErrorAlert("Erreur", "Impossible de charger les entrepôts: " + exception.getMessage());
        });

        new Thread(task).start();
    }

    private void updateTotalLabel(int totalWarehouses) {
        totalWarehousesLabel.setText(totalWarehouses + " entrepôts au total");
    }

    private void openAddWarehouseModal() {
        try {

            String fxmlPath = "/fr/ecodeli/ecodelidesktop/view/warehouse/WarehouseForm.fxml";

            URL resourceUrl = getClass().getResource(fxmlPath);

            if (resourceUrl == null) {
                showErrorAlert("Erreur", "Fichier FXML introuvable: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            System.out.println("FXMLLoader créé");

            Parent root = loader.load();
            System.out.println("FXML chargé avec succès");

            Stage modalStage = new Stage();
            modalStage.setTitle("Ajouter un entrepôt");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setScene(new Scene(root));
            System.out.println("Stage créé");

            WarehouseFormController controller = loader.getController();
            System.out.println("Contrôleur récupéré: " + (controller != null ? "OK" : "NULL"));

            if (controller != null) {
                controller.setModalStage(modalStage);
                controller.setOnWarehouseCreated(this::onWarehouseCreated);
                System.out.println("Contrôleur configuré");
            }

            modalStage.showAndWait();
            System.out.println("Modal fermée");

        } catch (IOException e) {
            System.out.println("❌ ERREUR IOException:");
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible d'ouvrir la fenêtre d'ajout: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ ERREUR générale:");
            e.printStackTrace();
            showErrorAlert("Erreur", "Erreur inattendue: " + e.getMessage());
        }
    }

    private void openEditWarehouseModal(Warehouse warehouse) {
        try {
            System.out.println("=== DEBUG: Ouverture modal modification entrepôt ===");
            System.out.println("Entrepôt à modifier: " + (warehouse != null ? warehouse.toString() : "NULL"));

            String fxmlPath = "/fr/ecodeli/ecodelidesktop/view/warehouse/WarehouseForm.fxml";
            System.out.println("Tentative de chargement: " + fxmlPath);

            URL resourceUrl = getClass().getResource(fxmlPath);
            System.out.println("URL ressource: " + resourceUrl);

            if (resourceUrl == null) {
                System.out.println("❌ ERREUR: Ressource non trouvée!");
                showErrorAlert("Erreur", "Fichier FXML introuvable: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            System.out.println("FXMLLoader créé");

            Parent root = loader.load();
            System.out.println("FXML chargé avec succès");

            Stage modalStage = new Stage();
            modalStage.setTitle("Modifier l'entrepôt");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setScene(new Scene(root));
            System.out.println("Stage créé");

            WarehouseFormController controller = loader.getController();
            System.out.println("Contrôleur récupéré: " + (controller != null ? "OK" : "NULL"));

            if (controller != null) {
                controller.setModalStage(modalStage);
                controller.setWarehouse(warehouse);
                controller.setOnWarehouseUpdated(this::onWarehouseUpdated);
                System.out.println("Contrôleur configuré avec entrepôt");
            }

            modalStage.showAndWait();
            System.out.println("Modal fermée");

        } catch (IOException e) {
            System.out.println("❌ ERREUR IOException:");
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ ERREUR générale:");
            e.printStackTrace();
            showErrorAlert("Erreur", "Erreur inattendue: " + e.getMessage());
        }
    }

    private void showDeleteConfirmation(Warehouse warehouse) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText("Supprimer l'entrepôt");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer l'entrepôt de " + warehouse.getCity() + " ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteWarehouse(warehouse);
            }
        });
    }

    private void deleteWarehouse(Warehouse warehouse) {
        // Implémentation de la suppression si l'API le permet
        // Pour l'instant, on simule juste le rafraîchissement
        loadWarehouses();
    }

    private void onWarehouseCreated() {
        loadWarehouses();
    }

    private void onWarehouseUpdated() {
        loadWarehouses();
    }

    private void showErrorAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}