package fr.ecodeli.ecodelidesktop.delivery;

import fr.ecodeli.ecodelidesktop.api.DeliveryAPI;
import fr.ecodeli.ecodelidesktop.api.DeliveryAPI.DeliveryDetails;
import fr.ecodeli.ecodelidesktop.api.DeliveryAPI.DeliveryResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

public class DeliveryTableController {

    @FXML private TableView<DeliveryRow> livraisonTable;
    @FXML private TableColumn<DeliveryRow, String> departureDateColumn;
    @FXML private TableColumn<DeliveryRow, String> arrivalDateColumn;
    @FXML private TableColumn<DeliveryRow, String> statusColumn;
    @FXML private TableColumn<DeliveryRow, String> isBoxColumn;
    @FXML private TableColumn<DeliveryRow, String> departureCityColumn;
    @FXML private TableColumn<DeliveryRow, String> arrivalCityColumn;
    @FXML private TableColumn<DeliveryRow, String> priceColumn;
    @FXML private TableColumn<DeliveryRow, String> packageCountColumn;
    @FXML private TableColumn<DeliveryRow, Void> actionsColumn;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    @FXML private Label totalDeliveriesLabel;

    private int currentPage = 1;
    private final int limit = 10;
    private int totalPages = 1;
    private final DeliveryAPI deliveryAPI = new DeliveryAPI();

    @FXML
    public void initialize() {
        departureDateColumn.setCellValueFactory(new PropertyValueFactory<>("departureDate"));
        arrivalDateColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        isBoxColumn.setCellValueFactory(new PropertyValueFactory<>("isBox"));
        departureCityColumn.setCellValueFactory(new PropertyValueFactory<>("departureCity"));
        arrivalCityColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalCity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        packageCountColumn.setCellValueFactory(new PropertyValueFactory<>("packageCount"));

        setupActionsColumn();
        setupColumnWidths();

        previousButton.setOnAction(e -> previousPage());
        nextButton.setOnAction(e -> nextPage());

        loadDeliveries();
    }

    private void setupActionsColumn() {
        Callback<TableColumn<DeliveryRow, Void>, TableCell<DeliveryRow, Void>> cellFactory =
                new Callback<TableColumn<DeliveryRow, Void>, TableCell<DeliveryRow, Void>>() {
                    @Override
                    public TableCell<DeliveryRow, Void> call(final TableColumn<DeliveryRow, Void> param) {
                        final TableCell<DeliveryRow, Void> cell = new TableCell<DeliveryRow, Void>() {
                            private final Button btn = new Button("Voir détails");

                            {
                                btn.getStyleClass().add("action-button");
                                btn.setOnAction((event) -> {
                                    DeliveryRow delivery = getTableView().getItems().get(getIndex());
                                    viewDeliveryDetails(delivery);
                                });
                            }

                            @Override
                            public void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    setGraphic(btn);
                                }
                            }
                        };
                        return cell;
                    }
                };
        actionsColumn.setCellFactory(cellFactory);
    }

    private void loadDeliveries() {
        try {
            DeliveryResponse response = deliveryAPI.getAllOngoingDeliveries(currentPage, limit);
            totalPages = (int) Math.ceil((double) response.getTotalRows() / limit);

            List<DeliveryAPI.DeliveryOngoing> ongoingList = response.getDeliveries();
            ObservableList<DeliveryRow> rows = FXCollections.observableArrayList();
            for (DeliveryAPI.DeliveryOngoing d : ongoingList) {
                rows.add(new DeliveryRow(d));
            }

            livraisonTable.setItems(rows);
            totalDeliveriesLabel.setText(response.getTotalRows() + " livraisons au total");
            updatePaginationControls();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les livraisons: " + e.getMessage());
        }
    }

    private void updatePaginationControls() {
        pageLabel.setText(String.format("Page %d sur %d", currentPage, totalPages));
        previousButton.setDisable(currentPage <= 1);
        nextButton.setDisable(currentPage >= totalPages);
    }

    @FXML
    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            loadDeliveries();
        }
    }

    @FXML
    private void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            loadDeliveries();
        }
    }

    private void viewDeliveryDetails(DeliveryRow delivery) {
        System.out.println("Affichage des détails pour la livraison ID: " + delivery.getId());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupColumnWidths() {
        departureDateColumn.prefWidthProperty().bind(livraisonTable.widthProperty().multiply(0.12));
        arrivalDateColumn.prefWidthProperty().bind(livraisonTable.widthProperty().multiply(0.12));
        statusColumn.prefWidthProperty().bind(livraisonTable.widthProperty().multiply(0.10));
        isBoxColumn.prefWidthProperty().bind(livraisonTable.widthProperty().multiply(0.06));
        departureCityColumn.prefWidthProperty().bind(livraisonTable.widthProperty().multiply(0.12));
        arrivalCityColumn.prefWidthProperty().bind(livraisonTable.widthProperty().multiply(0.12));
        priceColumn.prefWidthProperty().bind(livraisonTable.widthProperty().multiply(0.08));
        packageCountColumn.prefWidthProperty().bind(livraisonTable.widthProperty().multiply(0.08));
        actionsColumn.prefWidthProperty().bind(livraisonTable.widthProperty().multiply(0.20));
    }
}
