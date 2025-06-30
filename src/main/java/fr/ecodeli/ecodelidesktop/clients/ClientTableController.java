package fr.ecodeli.ecodelidesktop.clients;

import fr.ecodeli.ecodelidesktop.api.ClientAPI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ClientTableController {

    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, Void> clientColumn;
    @FXML private TableColumn<Client, String> emailColumn;
    @FXML private TableColumn<Client, String> nomAbonnementColumn;
    @FXML private TableColumn<Client, Integer> nbLivraisonsColumn;
    @FXML private TableColumn<Client, Integer> nbPrestationsColumn;
    @FXML private TableColumn<Client, Void> actionsColumn;

    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    @FXML private Label totalClientsLabel;

    private ClientAPI clientAPI;
    private int currentPage = 1;
    private int itemsPerPage = 10;
    private int totalPages = 1;

    @FXML
    public void initialize() {
        clientAPI = new ClientAPI();

        setupClientColumn();
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        nomAbonnementColumn.setCellValueFactory(new PropertyValueFactory<>("nomAbonnement"));
        nbLivraisonsColumn.setCellValueFactory(new PropertyValueFactory<>("nbDemandeDeLivraison"));
        nbPrestationsColumn.setCellValueFactory(new PropertyValueFactory<>("nombreDePrestations"));

        setupAbonnementColumn();
        setupNumberColumns();
        setupActionsColumn();

        loadClients();

        setupColumnWidths();

        prevButton.setOnAction(e -> previousPage());
        nextButton.setOnAction(e -> nextPage());
    }

    private void setupClientColumn() {
        clientColumn.setCellFactory(param -> new TableCell<Client, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Client client = getTableView().getItems().get(getIndex());
                    setGraphic(createClientCell(client));
                }
            }
        });
    }

    private HBox createClientCell(Client client) {
        HBox container = new HBox(12);
        container.setAlignment(Pos.CENTER_LEFT);
        container.getStyleClass().add("client-cell");

        ImageView profileImage = new ImageView();
        profileImage.setFitWidth(40);
        profileImage.setFitHeight(40);
        profileImage.getStyleClass().add("profile-image");

        try {
            if (client.getProfilePicture() != null && !client.getProfilePicture().isEmpty()) {
                profileImage.setImage(new Image(client.getProfilePicture(), true));
            } else {
                setDefaultProfileImage(profileImage);
            }
        } catch (Exception e) {
            setDefaultProfileImage(profileImage);
        }

        VBox infoBox = new VBox(2);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(client.getFirstName() + " " + client.getLastName());
        nameLabel.getStyleClass().add("client-name");

        Label titleLabel = new Label("Client");
        titleLabel.getStyleClass().add("client-title");

        infoBox.getChildren().addAll(nameLabel, titleLabel);
        container.getChildren().addAll(profileImage, infoBox);

        return container;
    }

    private void setDefaultProfileImage(ImageView imageView) {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-profile.png"));
            imageView.setImage(defaultImage);
        } catch (Exception e) {
        }
    }

    private void setupAbonnementColumn() {
        nbLivraisonsColumn.setCellFactory(param -> new TableCell<Client, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    getStyleClass().add("number-cell");
                }
            }
        });
    }

    private void setupNumberColumns() {

        nbPrestationsColumn.setCellFactory(param -> new TableCell<Client, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    getStyleClass().add("number-cell");
                }
            }
        });
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Voir détails");

            {
                btn.getStyleClass().add("action-button");
                btn.setOnAction(event -> {
                    Client client = getTableView().getItems().get(getIndex());
                    viewClientDetails(client);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    private void loadClients() {
        try {
            ClientAPI.ClientResponse response = clientAPI.getAllClients(currentPage, itemsPerPage);

            ObservableList<Client> clients = FXCollections.observableArrayList(response.getData());
            clientTable.setItems(clients);

            if (response.getMeta() != null) {
                int total = response.getMeta().getTotal();
                int limit = response.getMeta().getLimit();

                totalPages = (int) Math.ceil((double) total / limit);
                totalClientsLabel.setText(total + " clients au total");

                updatePaginationControls();
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les clients: " + e.getMessage());
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
            loadClients();
        }
    }

    @FXML
    private void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            loadClients();
        }
    }

    private void viewClientDetails(Client client) {
        System.out.println("Affichage des détails pour le client ID: " + client.getId());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupColumnWidths() {
        clientColumn.prefWidthProperty().bind(clientTable.widthProperty().multiply(0.25));
        emailColumn.prefWidthProperty().bind(clientTable.widthProperty().multiply(0.20));
        nomAbonnementColumn.prefWidthProperty().bind(clientTable.widthProperty().multiply(0.15));
        nbLivraisonsColumn.prefWidthProperty().bind(clientTable.widthProperty().multiply(0.10));
        nbPrestationsColumn.prefWidthProperty().bind(clientTable.widthProperty().multiply(0.10));
        actionsColumn.prefWidthProperty().bind(clientTable.widthProperty().multiply(0.20));
    }
}
