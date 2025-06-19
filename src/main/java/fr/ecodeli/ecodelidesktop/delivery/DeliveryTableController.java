package fr.ecodeli.ecodelidesktop.delivery;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class DeliveryTableController {

    @FXML private TableView<Delivery> livraisonTable;
    @FXML private TableColumn<Delivery, String> departureDateColumn;
    @FXML private TableColumn<Delivery, String> arrivalDateColumn;
    @FXML private TableColumn<Delivery, String> statusColumn;
    @FXML private TableColumn<Delivery, Boolean> isBoxColumn;
    @FXML private TableColumn<Delivery, String> departureCityColumn;
    @FXML private TableColumn<Delivery, String> arrivalCityColumn;

    @FXML
    public void initialize() {
        departureDateColumn.setCellValueFactory(new PropertyValueFactory<>("departureDate"));
        arrivalDateColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        isBoxColumn.setCellValueFactory(new PropertyValueFactory<>("box"));

        departureCityColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getDeparture().getCity())
        );
        arrivalCityColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getArrival().getCity())
        );

        ObservableList<Delivery> livraisons = FXCollections.observableArrayList(loadLivraisonsFromJson());
        livraisonTable.setItems(livraisons);
    }

    private List<Delivery> loadLivraisonsFromJson() {
        InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/data/liste-livraisons.json"));
        Type listType = new TypeToken<List<Delivery>>(){}.getType();
        return new Gson().fromJson(reader, listType);
    }
}
