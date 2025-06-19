package fr.ecodeli.ecodelidesktop.services;

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

public class ServiceTableController {

    @FXML private TableView<Service> serviceTable;
    @FXML private TableColumn<Service, String> nameColumn;
    @FXML private TableColumn<Service, String> cityColumn;
    @FXML private TableColumn<Service, Double> priceColumn;
    @FXML private TableColumn<Service, Integer> durationColumn;
    @FXML private TableColumn<Service, Boolean> availableColumn;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationTime"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        ObservableList<Service> services = FXCollections.observableArrayList(loadServicesFromJson());
        serviceTable.setItems(services);
    }

    private List<Service> loadServicesFromJson() {
        InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/data/liste-prestations.json"));
        JsonObject json = new Gson().fromJson(reader, JsonObject.class);
        Type listType = new TypeToken<List<Service>>(){}.getType();
        return new Gson().fromJson(json.getAsJsonArray("data"), listType);
    }
}
