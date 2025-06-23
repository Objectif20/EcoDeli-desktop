package fr.ecodeli.ecodelidesktop.clients;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class ClientTableController {

    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, String> idColumn;
    @FXML private TableColumn<Client, String> firstNameColumn;
    @FXML private TableColumn<Client, String> lastNameColumn;
    @FXML private TableColumn<Client, String> emailColumn;
    @FXML private TableColumn<Client, String> nomAbonnementColumn;
    @FXML private TableColumn<Client, Integer> nbLivraisonsColumn;
    @FXML private TableColumn<Client, Integer> nbPrestationsColumn;

    @FXML
    public void initialize() {
        // Association des colonnes aux propriétés de l'objet Client
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        nomAbonnementColumn.setCellValueFactory(new PropertyValueFactory<>("nomAbonnement"));
        nbLivraisonsColumn.setCellValueFactory(new PropertyValueFactory<>("nbDemandeDeLivraison"));
        nbPrestationsColumn.setCellValueFactory(new PropertyValueFactory<>("nombreDePrestations"));

        ObservableList<Client> clients = FXCollections.observableArrayList(loadClientsFromJson());
        System.out.println("Clients chargés : " + clients.size());
        for (Client client : clients) {
            System.out.println(client.getFirstName() + " " + client.getLastName());
        }
        clientTable.setItems(clients);
    }



    private List<Client> loadClientsFromJson() {
        InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/data/liste-clients.json")); // mets ton chemin ici
        JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
        Type clientListType = new TypeToken<List<Client>>() {}.getType();
        return new Gson().fromJson(jsonObject.getAsJsonArray("data"), clientListType);
    }
}
