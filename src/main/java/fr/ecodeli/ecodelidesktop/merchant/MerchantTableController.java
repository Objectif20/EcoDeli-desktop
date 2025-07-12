package fr.ecodeli.ecodelidesktop.merchant;

import com.github.javafaker.Faker;
import fr.ecodeli.ecodelidesktop.api.MerchantAPI;
import fr.ecodeli.ecodelidesktop.controller.MainController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.*;

public class MerchantTableController {

    @FXML private TableView<Merchant> merchantTable;
    @FXML private TableColumn<Merchant, Void> merchantColumn;
    @FXML private TableColumn<Merchant, String> companyColumn;
    @FXML private TableColumn<Merchant, String> siretColumn;
    @FXML private TableColumn<Merchant, String> countryColumn;
    @FXML private TableColumn<Merchant, String> phoneColumn;
    @FXML private TableColumn<Merchant, String> descriptionColumn;
    @FXML private TableColumn<Merchant, Void> actionsColumn;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    @FXML private Label totalMerchantsLabel;
    @FXML private Button refreshButton;

    @FXML private Label totalMerchantsCard;
    @FXML private Label activeMerchantsCard;
    @FXML private Label totalDeliveriesCard;
    @FXML private Label monthlyRevenueCard;


    @FXML private PieChart subscriptionChart;
    @FXML private BarChart<String, Number> deliveryChart;
    @FXML private LineChart<String, Number> merchantGrowthChart;
    @FXML private BarChart<String, Number> revenueChart;

    private MerchantAPI merchantAPI;
    private int currentPage = 1;
    private int itemsPerPage = 10;
    private int totalPages = 1;
    private Faker faker;

    @FXML
    public void initialize() {
        merchantAPI = new MerchantAPI();
        faker = new Faker(new Locale("fr"));

        setupTable();
        setupCharts();
        loadMerchants();
        loadStatistics();

        prevButton.setOnAction(e -> previousPage());
        nextButton.setOnAction(e -> nextPage());
        refreshButton.setOnAction(e -> refreshData());
    }

    private void setupTable() {
        setupMerchantColumn();
        companyColumn.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        siretColumn.setCellValueFactory(new PropertyValueFactory<>("siret"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        centerCellContent(companyColumn);
        centerCellContent(siretColumn);
        centerCellContent(countryColumn);
        centerCellContent(phoneColumn);

        setupDescriptionColumn();
        setupActionsColumn();
    }

    private void setupCharts() {
        subscriptionChart.setTitle("Répartition des abonnements");
        subscriptionChart.setLegendVisible(true);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Villes");
        yAxis.setLabel("Nombre de livraisons");
        deliveryChart.setTitle("Livraisons par ville");

        CategoryAxis xAxisLine = new CategoryAxis();
        NumberAxis yAxisLine = new NumberAxis();
        xAxisLine.setLabel("Mois");
        yAxisLine.setLabel("Nouveaux commerçants");
        merchantGrowthChart.setTitle("Croissance mensuelle");

        CategoryAxis xAxisRevenue = new CategoryAxis();
        NumberAxis yAxisRevenue = new NumberAxis();
        xAxisRevenue.setLabel("Trimestres");
        yAxisRevenue.setLabel("Revenus (€)");
        revenueChart.setTitle("Revenus trimestriels");
    }


    private void loadStatistics() {
        int totalMerchants = faker.number().numberBetween(150, 300);
        int activeMerchants = faker.number().numberBetween(120, totalMerchants);
        int totalDeliveries = faker.number().numberBetween(1000, 5000);
        double monthlyRevenue = faker.number().randomDouble(2, 50000, 200000);

        totalMerchantsCard.setText(String.valueOf(totalMerchants));
        activeMerchantsCard.setText(String.valueOf(activeMerchants));
        totalDeliveriesCard.setText(String.valueOf(totalDeliveries));
        monthlyRevenueCard.setText(String.format("%.2f €", monthlyRevenue));

        ObservableList<PieChart.Data> subscriptionData = FXCollections.observableArrayList(
                new PieChart.Data("Basic", faker.number().numberBetween(40, 60)),
                new PieChart.Data("Premium", faker.number().numberBetween(30, 50)),
                new PieChart.Data("Enterprise", faker.number().numberBetween(10, 30))
        );
        subscriptionChart.setData(subscriptionData);

        XYChart.Series<String, Number> deliverySeries = new XYChart.Series<>();
        deliverySeries.setName("Livraisons");
        String[] cities = {"Paris", "Lyon", "Marseille", "Lille", "Toulouse", "Nantes"};
        for (String city : cities) {
            deliverySeries.getData().add(new XYChart.Data<>(city, faker.number().numberBetween(50, 200)));
        }
        deliveryChart.getData().clear();
        deliveryChart.getData().add(deliverySeries);

        XYChart.Series<String, Number> growthSeries = new XYChart.Series<>();
        growthSeries.setName("Nouveaux commerçants");
        String[] months = {"Jan", "Fév", "Mar", "Avr", "Mai", "Jun"};
        for (String month : months) {
            growthSeries.getData().add(new XYChart.Data<>(month, faker.number().numberBetween(5, 25)));
        }
        merchantGrowthChart.getData().clear();
        merchantGrowthChart.getData().add(growthSeries);

        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("Revenus");
        String[] quarters = {"T1", "T2", "T3", "T4"};
        for (String quarter : quarters) {
            revenueSeries.getData().add(new XYChart.Data<>(quarter, faker.number().numberBetween(20000, 100000)));
        }
        revenueChart.getData().clear();
        revenueChart.getData().add(revenueSeries);
    }

    @FXML
    private void refreshData() {
        loadStatistics();
        loadMerchants();
    }

    private <T> void centerCellContent(TableColumn<Merchant, T> column) {
        column.setCellFactory(getCenteredCellFactory());
    }

    private <T> Callback<TableColumn<Merchant, T>, TableCell<Merchant, T>> getCenteredCellFactory() {
        return new Callback<TableColumn<Merchant, T>, TableCell<Merchant, T>>() {
            @Override
            public TableCell<Merchant, T> call(TableColumn<Merchant, T> param) {
                return new TableCell<Merchant, T>() {
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

    private void setupMerchantColumn() {
        merchantColumn.setCellFactory(new Callback<TableColumn<Merchant, Void>, TableCell<Merchant, Void>>() {
            @Override
            public TableCell<Merchant, Void> call(TableColumn<Merchant, Void> param) {
                return new TableCell<Merchant, Void>() {
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= getTableView().getItems().size()) {
                            setGraphic(null);
                        } else {
                            Merchant merchant = getTableView().getItems().get(getIndex());
                            setGraphic(createMerchantCell(merchant));
                        }
                    }
                };
            }
        });
    }

    private HBox createMerchantCell(Merchant merchant) {
        HBox container = new HBox(12);
        container.setAlignment(Pos.CENTER_LEFT);
        container.getStyleClass().add("merchant-cell");

        ImageView profileImage = new ImageView();
        profileImage.setFitWidth(40);
        profileImage.setFitHeight(40);
        profileImage.getStyleClass().add("profile-image");

        if (merchant.getProfilePicture() != null && !merchant.getProfilePicture().isEmpty()) {
            try {
                Image image = new Image(merchant.getProfilePicture(), true);
                profileImage.setImage(image);
            } catch (Exception e) {
                setDefaultProfileImage(profileImage);
            }
        } else {
            setDefaultProfileImage(profileImage);
        }

        VBox infoBox = new VBox(2);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        Label nameLabel = new Label(merchant.getFirstName() + " " + merchant.getLastName());
        nameLabel.getStyleClass().add("merchant-name");
        Label titleLabel = new Label("Commerçant");
        titleLabel.getStyleClass().add("merchant-title");
        infoBox.getChildren().addAll(nameLabel, titleLabel);

        container.getChildren().addAll(profileImage, infoBox);
        return container;
    }

    private void setDefaultProfileImage(ImageView imageView) {
        try {
            Image defaultImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/fr/ecodeli/ecodelidesktop/view/global/ecodeli.png")));
            imageView.setImage(defaultImage);
        } catch (Exception e) {
        }
    }

    private void setupDescriptionColumn() {
        descriptionColumn.setCellFactory(new Callback<TableColumn<Merchant, String>, TableCell<Merchant, String>>() {
            @Override
            public TableCell<Merchant, String> call(TableColumn<Merchant, String> param) {
                return new TableCell<Merchant, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setTooltip(null);
                        } else {
                            String truncated = item.length() > 30 ? item.substring(0, 30) + "..." : item;
                            setText(truncated);
                            if (item.length() > 30) {
                                setTooltip(new Tooltip(item));
                            }
                        }
                    }
                };
            }
        });
    }

    private void setupActionsColumn() {
        Callback<TableColumn<Merchant, Void>, TableCell<Merchant, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Merchant, Void> call(final TableColumn<Merchant, Void> param) {
                final TableCell<Merchant, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("Voir détails");

                    {
                        btn.getStyleClass().add("action-button");
                        btn.setOnAction((event) -> {
                            Merchant merchant = getTableView().getItems().get(getIndex());
                            viewMerchantDetails(merchant);
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
                        setAlignment(Pos.CENTER);
                    }
                };
                return cell;
            }
        };
        actionsColumn.setCellFactory(cellFactory);
    }

    private void loadMerchants() {
        try {
            MerchantAPI.MerchantResponse response = merchantAPI.getAllMerchants(currentPage, itemsPerPage);
            ObservableList<Merchant> merchants = FXCollections.observableArrayList(response.getData());
            merchantTable.setItems(merchants);
            if (response.getMeta() != null) {
                int total = response.getMeta().getTotal();
                int limit = response.getMeta().getLimit();
                totalPages = (int) Math.ceil((double) total / limit);
                String labelText = total + " commerçant" + (total > 1 ? "s" : "") + " au total";
                totalMerchantsLabel.setText(labelText);                updatePaginationControls();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Impossible de charger les marchands: " + e.getMessage());
        }
    }

    private void updatePaginationControls() {
        pageLabel.setText(String.format("Page %d sur %d", currentPage, totalPages));
        prevButton.setDisable(currentPage <= 1);
        nextButton.setDisable(currentPage >= totalPages);
    }

    @FXML
    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            loadMerchants();
        }
    }

    @FXML
    private void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            loadMerchants();
        }
    }

    private void viewMerchantDetails(Merchant merchant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/merchant/MerchantDetailsView.fxml"));
            Parent detailView = loader.load();

            MerchantDetailsController detailController = loader.getController();
            detailController.setClientId(merchant.getId());

            MainController.setContent(detailView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/fr/ecodeli/ecodelidesktop/view/global/ecodeli.png"))));
        alert.showAndWait();
    }
}