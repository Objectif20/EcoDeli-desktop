package fr.ecodeli.ecodelidesktop.dashboard;

import fr.ecodeli.ecodelidesktop.stats.StatsService;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.layout.GridPane;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

import java.awt.image.BufferedImage;
import java.io.File;

public class StatsViewController {

    @FXML
    private GridPane statsGrid; // C’est ton conteneur des stats

    private final StatsService statsService = new StatsService();

    @FXML
    public void initialize() {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Répartition des utilisateurs");
        pieChart.getData().add(new PieChart.Data("Commerçants", 40));
        pieChart.getData().add(new PieChart.Data("Clients", 35));
        pieChart.getData().add(new PieChart.Data("Livreurs", 25));

        PieChart colisRepartitionChart = statsService.getColisRepartitionChart();
        PieChart chiffreAffairesChart = statsService.getChiffreAffairesChart();
        PieChart abonnementChart = statsService.getAbonnementChart();
        BarChart<String, Number> topClientsChart = statsService.getTopClientsChart();

        // Ajoute les graphiques au GridPane (2 colonnes)
        statsGrid.add(pieChart, 0, 0);
        statsGrid.add(colisRepartitionChart, 1, 0);
        statsGrid.add(chiffreAffairesChart, 0, 1);
        statsGrid.add(abonnementChart, 1, 1);
        statsGrid.add(topClientsChart, 0, 2, 2, 1); // Prend toute la largeur
    }

    @FXML
    private void handleExportPdf() {
        exportDashboardToPdf();
    }

    private void exportDashboardToPdf() {
        try {
            // Capture l’image du GridPane (statsGrid est ton conteneur principal)
            WritableImage snapshot = statsGrid.snapshot(new SnapshotParameters(), null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

            // Crée le document PDF
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(new PDRectangle(bufferedImage.getWidth(), bufferedImage.getHeight()));
            document.addPage(page);

            // Ajoute l’image au PDF
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            var pdImage = LosslessFactory.createFromImage(document, bufferedImage);
            contentStream.drawImage(pdImage, 0, 0);
            contentStream.close();

            // Sauvegarde le fichier PDF
            String userHome = System.getProperty("user.home") + File.separator + "Downloads";
            String filePath = userHome + File.separator + "dashboard.pdf";
            document.save(filePath);
            document.close();

            System.out.println("✅ PDF généré : " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
