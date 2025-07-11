package fr.ecodeli.ecodelidesktop.clients;

import com.github.javafaker.Faker;
import fr.ecodeli.ecodelidesktop.api.ClientAPI;
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
import javafx.util.Callback;

import java.io.IOException;
import java.util.Objects;

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
    @FXML private Button refreshButton;


    @FXML private Label totalClientsCard;
    @FXML private Label clientsTransporteursCard;
    @FXML private Label prestationsMoyennesCard;
    @FXML private Label revenuMoyenCard;
    @FXML private PieChart abonnementChart;
    @FXML private BarChart<String, Number> prestationsAbonnementChart;
    @FXML private LineChart<String, Number> evolutionClientsChart;
    @FXML private BarChart<String, Number> revenusTrimestreChart;

    private ClientAPI clientAPI;
    private int currentPage = 1;
    private int totalPages = 1;
    private final Faker faker = new Faker();

    @FXML
    public void initialize() {
        clientAPI = new ClientAPI();
        setupClientColumn();
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        nomAbonnementColumn.setCellValueFactory(new PropertyValueFactory<>("nomAbonnement"));
        nbLivraisonsColumn.setCellValueFactory(new PropertyValueFactory<>("nbDemandeDeLivraison"));
        nbPrestationsColumn.setCellValueFactory(new PropertyValueFactory<>("nombreDePrestations"));

        centerCellContent(emailColumn);
        centerCellContent(nomAbonnementColumn);
        centerCellContent(nbLivraisonsColumn);
        centerCellContent(nbPrestationsColumn);

        setupActionsColumn();
        loadClients();
        loadStatistics();
        setupCharts();
        prevButton.setOnAction(e -> previousPage());
        nextButton.setOnAction(e -> nextPage());
        refreshButton.setOnAction(e -> refreshData());
    }

    private void setupCharts() {
        abonnementChart.setTitle("Répartition des abonnements");
        abonnementChart.setLegendVisible(true);

        CategoryAxis xAxisPrestations = new CategoryAxis();
        NumberAxis yAxisPrestations = new NumberAxis();
        xAxisPrestations.setLabel("Abonnements");
        yAxisPrestations.setLabel("Prestations moyennes");
        prestationsAbonnementChart.setTitle("Prestations par abonnement");

        CategoryAxis xAxisEvolution = new CategoryAxis();
        NumberAxis yAxisEvolution = new NumberAxis();
        xAxisEvolution.setLabel("Mois");
        yAxisEvolution.setLabel("Nouveaux clients");
        evolutionClientsChart.setTitle("Évolution mensuelle des clients");

        CategoryAxis xAxisRevenu = new CategoryAxis();
        NumberAxis yAxisRevenu = new NumberAxis();
        xAxisRevenu.setLabel("Trimestres");
        yAxisRevenu.setLabel("Revenus (€)");
        revenusTrimestreChart.setTitle("Revenus trimestriels");
    }

    private void loadStatistics() {
        int totalClients = faker.number().numberBetween(800, 1200);
        int clientsTransporteurs = faker.number().numberBetween(45, 85);
        int prestationsMoyennes = faker.number().numberBetween(8, 15);
        double revenuMoyen = faker.number().randomDouble(2, 85, 150);

        totalClientsCard.setText(String.valueOf(totalClients));
        clientsTransporteursCard.setText(String.valueOf(clientsTransporteurs));
        prestationsMoyennesCard.setText(String.valueOf(prestationsMoyennes));
        revenuMoyenCard.setText(String.format("%.2f €", revenuMoyen));

        ObservableList<PieChart.Data> abonnementData = FXCollections.observableArrayList(
                new PieChart.Data("Premium", faker.number().numberBetween(150, 250)),
                new PieChart.Data("Standard", faker.number().numberBetween(300, 450)),
                new PieChart.Data("Basic", faker.number().numberBetween(100, 200)),
                new PieChart.Data("Entreprise", faker.number().numberBetween(50, 120)),
                new PieChart.Data("Famille", faker.number().numberBetween(80, 180))
        );
        abonnementChart.setData(abonnementData);

        XYChart.Series<String, Number> prestationsSeries = new XYChart.Series<>();
        prestationsSeries.setName("Prestations moyennes");
        String[] abonnements = {"Premium", "Standard", "Basic", "Entreprise", "Famille"};
        for (String abonnement : abonnements) {
            int prestations = switch (abonnement) {
                case "Premium" -> faker.number().numberBetween(15, 25);
                case "Standard" -> faker.number().numberBetween(8, 15);
                case "Basic" -> faker.number().numberBetween(3, 8);
                case "Entreprise" -> faker.number().numberBetween(20, 35);
                case "Famille" -> faker.number().numberBetween(10, 18);
                default -> faker.number().numberBetween(5, 15);
            };
            prestationsSeries.getData().add(new XYChart.Data<>(abonnement, prestations));
        }
        prestationsAbonnementChart.getData().clear();
        prestationsAbonnementChart.getData().add(prestationsSeries);

        XYChart.Series<String, Number> evolutionSeries = new XYChart.Series<>();
        evolutionSeries.setName("Nouveaux clients");
        String[] mois = {"Jan", "Fév", "Mar", "Avr", "Mai", "Jun", "Jul", "Aoû", "Sep", "Oct", "Nov", "Déc"};
        int baseValue = 50;
        for (String month : mois) {
            int variation = faker.number().numberBetween(-15, 25);
            baseValue = Math.max(20, baseValue + variation);

            if (month.equals("Jul") || month.equals("Aoû") || month.equals("Déc")) {
                baseValue += faker.number().numberBetween(10, 30);
            }

            evolutionSeries.getData().add(new XYChart.Data<>(month, baseValue));
        }
        evolutionClientsChart.getData().clear();
        evolutionClientsChart.getData().add(evolutionSeries);

        XYChart.Series<String, Number> revenuSeries = new XYChart.Series<>();
        revenuSeries.setName("Revenus");
        String[] trimestres = {"T1 2024", "T2 2024", "T3 2024", "T4 2024", "T1 2025"};
        int baseRevenu = 25000;
        for (String trimestre : trimestres) {
            int variation = faker.number().numberBetween(-3000, 8000);
            baseRevenu = Math.max(15000, baseRevenu + variation);

            if (trimestre.contains("T4")) {
                baseRevenu += faker.number().numberBetween(5000, 12000);
            }

            revenuSeries.getData().add(new XYChart.Data<>(trimestre, baseRevenu));
        }
        revenusTrimestreChart.getData().clear();
        revenusTrimestreChart.getData().add(revenuSeries);
    }

    @FXML
    private void refreshData() {
        loadStatistics();
        loadClients();
    }

    private <T> void centerCellContent(TableColumn<Client, T> column) {
        column.setCellFactory(getCenteredCellFactory());
    }

    private <T> Callback<TableColumn<Client, T>, TableCell<Client, T>> getCenteredCellFactory() {
        return param -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
                setAlignment(Pos.CENTER);
            }
        };
    }

    private void setupClientColumn() {
        clientColumn.setCellFactory(param -> new TableCell<>() {
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
            Image defaultImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/fr/ecodeli/ecodelidesktop/view/global/ecodeli.png")));
            imageView.setImage(defaultImage);
        } catch (Exception ignored) {
        }
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
                setAlignment(Pos.CENTER);
            }
        });
    }

    private void loadClients() {
        try {
            int itemsPerPage = 10;
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
            showAlert("Impossible de charger les clients: " + e.getMessage());
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/client/ClientDetailsView.fxml"));
            Parent detailView = loader.load();

            ClientDetailController detailController = loader.getController();
            detailController.setClientId(client.getId());

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
        alert.showAndWait();
    }
}
