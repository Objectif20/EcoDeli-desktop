package fr.ecodeli.ecodelidesktop.dashboard;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;

public class StatsViewController {

    @FXML
    private VBox statsContainer;

    @FXML
    public void initialize() {
        // Exemple : PieChart fictif sur les utilisateurs
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Répartition des utilisateurs");

        pieChart.getData().add(new PieChart.Data("Commerçants", 40));
        pieChart.getData().add(new PieChart.Data("Clients", 35));
        pieChart.getData().add(new PieChart.Data("Livreurs", 25));

        statsContainer.getChildren().add(pieChart);
    }
}
