package fr.ecodeli.ecodelidesktop.merchant;

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

public class MerchantTableController {

    @FXML private TableView<Merchant> merchantTable;
    @FXML private TableColumn<Merchant, String> idColumn;
    @FXML private TableColumn<Merchant, String> firstNameColumn;
    @FXML private TableColumn<Merchant, String> lastNameColumn;
    @FXML private TableColumn<Merchant, String> companyColumn;
    @FXML private TableColumn<Merchant, String> cityColumn;
    @FXML private TableColumn<Merchant, String> abonnementColumn;
    @FXML private TableColumn<Merchant, Integer> livraisonsColumn;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        companyColumn.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        abonnementColumn.setCellValueFactory(new PropertyValueFactory<>("nomAbonnement"));
        livraisonsColumn.setCellValueFactory(new PropertyValueFactory<>("nbDemandeDeLivraison"));

        ObservableList<Merchant> merchants = FXCollections.observableArrayList(loadMerchantsFromJson());
        merchantTable.setItems(merchants);
    }

    private List<Merchant> loadMerchantsFromJson() {
        InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/data/liste-commercants.json"));
        JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
        Type listType = new TypeToken<List<Merchant>>() {}.getType();
        return new Gson().fromJson(jsonObject.getAsJsonArray("data"), listType);
    }
}
