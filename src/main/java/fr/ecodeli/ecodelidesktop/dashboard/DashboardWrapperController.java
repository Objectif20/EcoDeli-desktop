package fr.ecodeli.ecodelidesktop.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.Parent;

import java.io.IOException;

public class DashboardWrapperController {

    @FXML
    private TabPane dashboardTabPane;

    @FXML
    public void initialize() {
        try {
            FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/dashboard/StatsView.fxml"));
            Parent statsView = loader1.load();

            Tab statsTab = new Tab("Statistiques Générales");
            statsTab.setContent(statsView);
            statsTab.setClosable(false);

            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/dashboard/StatsPrestationsView.fxml"));
            Parent prestationsView = loader2.load();

            Tab prestationsTab = new Tab("Statistiques des Prestations");
            prestationsTab.setContent(prestationsView);
            prestationsTab.setClosable(false);

            dashboardTabPane.getTabs().addAll(statsTab, prestationsTab);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}