package fr.ecodeli.ecodelidesktop.services;

import fr.ecodeli.ecodelidesktop.api.ServicesAPI;
import fr.ecodeli.ecodelidesktop.controller.MainController;
import fr.ecodeli.ecodelidesktop.merchant.Merchant;
import fr.ecodeli.ecodelidesktop.merchant.MerchantDetailsController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ServiceTableController {

    @FXML private TableView<Service> serviceTable;
    @FXML private TableColumn<Service, String> nameColumn;
    @FXML private TableColumn<Service, String> cityColumn;
    @FXML private TableColumn<Service, Double> priceColumn;
    @FXML private TableColumn<Service, Integer> durationColumn;
    @FXML private TableColumn<Service, Boolean> availableColumn;
    @FXML private TableColumn<Service, Void> actionColumn;
    @FXML private TableColumn<Service, String> typeColumn;
    @FXML private TableColumn<Service, Boolean> statusColumn;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;

    private final ServicesAPI servicesAPI = new ServicesAPI();

    private int currentPage = 1;
    private int totalPages = 1;
    private final int limit = 10;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationTime"));

        typeColumn.setCellValueFactory(new PropertyValueFactory<>("serviceType"));

        centerCellContent(nameColumn);
        centerCellContent(cityColumn);
        centerCellContent(durationColumn);
        centerCellContent(typeColumn);

        availableColumn.setCellFactory(col -> createBooleanCell());
        statusColumn.setCellFactory(col -> createBooleanCell());

        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
        availableColumn.setCellFactory(col -> new TableCell<Service, Boolean>() {
            @Override
            protected void updateItem(Boolean value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(value ? "Oui" : "Non");
                }
                setAlignment(javafx.geometry.Pos.CENTER);
            }
        });

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("validated"));
        statusColumn.setCellFactory(col -> new TableCell<Service, Boolean>() {
            @Override
            protected void updateItem(Boolean value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(value ? "Oui" : "Non");
                }
                setAlignment(javafx.geometry.Pos.CENTER);
            }
        });

        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setCellFactory(col -> new TableCell<Service, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    if (price == price.longValue()) {
                        setText(String.format("%d €", price.longValue()));
                    } else {
                        setText(String.format(Locale.FRANCE, "%.2f €", price));
                    }
                }
                setAlignment(javafx.geometry.Pos.CENTER);
            }
        });

        addButtonToTable();

        prevButton.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadServicesFromAPI(currentPage, limit);
            }
        });

        nextButton.setOnAction(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadServicesFromAPI(currentPage, limit);
            }
        });

        loadServicesFromAPI(currentPage, limit);
    }

    private <T> void centerCellContent(TableColumn<Service, T> column) {
        column.setCellFactory(getCenteredCellFactory());
    }

    private void viewServiceDetails(Service service) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/service/ServicesDetailsView.fxml"));
            Parent detailView = loader.load();

            ServiceDetailsController detailController = loader.getController();
            detailController.setServiceId(service.getId());

            MainController.setContent(detailView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> Callback<TableColumn<Service, T>, TableCell<Service, T>> getCenteredCellFactory() {
        return new Callback<TableColumn<Service, T>, TableCell<Service, T>>() {
            @Override
            public TableCell<Service, T> call(TableColumn<Service, T> param) {
                return new TableCell<Service, T>() {
                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.toString());
                        }
                        setAlignment(javafx.geometry.Pos.CENTER);
                    }
                };
            }
        };
    }

    private void loadServicesFromAPI(int page, int limit) {
        try {
            ServicesAPI.ServiceResponse response = servicesAPI.getAllServices(page, limit);
            List<Service> services = response.getData();
            int totalItems = response.getTotal();

            totalPages = (int) Math.ceil((double) totalItems / limit);

            ObservableList<Service> observableList = FXCollections.observableArrayList(services);
            serviceTable.setItems(observableList);

            pageLabel.setText(String.format("Page %d sur %d", currentPage, totalPages));

            prevButton.setDisable(currentPage == 1);
            nextButton.setDisable(currentPage == totalPages);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addButtonToTable() {
        Callback<TableColumn<Service, Void>, TableCell<Service, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Service, Void> call(final TableColumn<Service, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Afficher");

                    {
                        btn.setOnAction(event -> {
                            Service service = getTableView().getItems().get(getIndex());
                            if (service != null) {
                                viewServiceDetails(service);
                            } else {
                                System.err.println("Aucun marchand associé au service.");
                            }
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
                        setAlignment(javafx.geometry.Pos.CENTER);
                    }
                };
            }
        };

        actionColumn.setCellFactory(cellFactory);
    }

    private TableCell<Service, Boolean> createBooleanCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Boolean value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(value ? "Oui" : "Non");
                }
            }
        };
    }
}
