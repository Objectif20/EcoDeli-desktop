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
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
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
            private final HBox buttonBox = new HBox(5, editButton);

            {
                editButton.getStyleClass().add("action-button");

                editButton.setOnAction(e -> {
                    Warehouse warehouse = getTableView().getItems().get(getIndex());
                    openEditWarehouseModal(warehouse);
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
            exception.printStackTrace();
            showErrorAlert("Erreur", "Impossible de charger les entrepôts: " + exception.getMessage());
        });

        new Thread(task).start();
    }

    private void updateTotalLabel(int totalWarehouses) {
        totalWarehousesLabel.setText(totalWarehouses + " entrepôts au total");
    }

    private void configureModalStage(Stage modalStage) {
        Stage primaryStage = (Stage) warehouseTable.getScene().getWindow();

        double modalWidth = 950;
        double modalHeight = 750;

        modalStage.setWidth(modalWidth);
        modalStage.setHeight(modalHeight);
        modalStage.setMinWidth(950);
        modalStage.setMinHeight(750);

        if (primaryStage != null) {
            double primaryCenterX = primaryStage.getX() + primaryStage.getWidth() / 2;
            double primaryCenterY = primaryStage.getY() + primaryStage.getHeight() / 2;

            modalStage.setX(primaryCenterX - modalWidth / 2);
            modalStage.setY(primaryCenterY - modalHeight / 2);
        } else {
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            modalStage.setX((primaryScreenBounds.getWidth() - modalWidth) / 2);
            modalStage.setY((primaryScreenBounds.getHeight() - modalHeight) / 2);
        }
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
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle("Ajouter un entrepôt");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setScene(new Scene(root));

            configureModalStage(modalStage);

            WarehouseFormController controller = loader.getController();
            if (controller != null) {
                controller.setModalStage(modalStage);
                controller.setOnWarehouseCreated(this::onWarehouseCreated);
            }

            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible d'ouvrir la fenêtre d'ajout: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Erreur inattendue: " + e.getMessage());
        }
    }

    private void openEditWarehouseModal(Warehouse warehouse) {
        try {
            String fxmlPath = "/fr/ecodeli/ecodelidesktop/view/warehouse/WarehouseForm.fxml";
            URL resourceUrl = getClass().getResource(fxmlPath);

            if (resourceUrl == null) {
                showErrorAlert("Erreur", "Fichier FXML introuvable: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle("Modifier l'entrepôt");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setScene(new Scene(root));

            configureModalStage(modalStage);

            WarehouseFormController controller = loader.getController();
            if (controller != null) {
                controller.setModalStage(modalStage);
                controller.setWarehouse(warehouse);
                controller.setOnWarehouseUpdated(this::onWarehouseUpdated);
            }

            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Erreur inattendue: " + e.getMessage());
        }
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