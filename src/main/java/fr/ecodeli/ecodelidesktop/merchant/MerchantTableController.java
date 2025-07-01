package fr.ecodeli.ecodelidesktop.merchant;

import fr.ecodeli.ecodelidesktop.api.MerchantAPI;
import fr.ecodeli.ecodelidesktop.controller.MainController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Objects;

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

    private MerchantAPI merchantAPI;
    private int currentPage = 1;
    private int itemsPerPage = 10;
    private int totalPages = 1;

    @FXML
    public void initialize() {
        merchantAPI = new MerchantAPI();
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

        loadMerchants();
        prevButton.setOnAction(e -> previousPage());
        nextButton.setOnAction(e -> nextPage());
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
                totalMerchantsLabel.setText(total + " marchands au total");
                updatePaginationControls();
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
