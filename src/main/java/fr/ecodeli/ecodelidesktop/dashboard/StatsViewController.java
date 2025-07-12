package fr.ecodeli.ecodelidesktop.dashboard;

import fr.ecodeli.ecodelidesktop.api.ServicesAPI;
import fr.ecodeli.ecodelidesktop.stats.StatsService;
import fr.ecodeli.ecodelidesktop.clients.Client;
import fr.ecodeli.ecodelidesktop.delivery.DeliveryRow;
import fr.ecodeli.ecodelidesktop.services.Service;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import de.rototor.pdfbox.graphics2d.PdfBoxGraphics2D;
import de.rototor.pdfbox.graphics2d.PdfBoxGraphics2DFontTextDrawer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatsViewController {

    @FXML
    private GridPane statsGrid;

    private final StatsService statsService = new StatsService();

    @FXML
    public void initialize() {
        PieChart repartitionUtilisateursChart = statsService.getRepartitionUtilisateursChart();
        PieChart colisRepartitionChart = statsService.getColisRepartitionChart();
        PieChart chiffreAffairesChart = statsService.getChiffreAffairesChart();
        PieChart abonnementChart = statsService.getAbonnementChart();
        BarChart<String, Number> topClientsChart = statsService.getTopClientsChart();

        statsGrid.add(repartitionUtilisateursChart, 0, 0);
        statsGrid.add(colisRepartitionChart, 1, 0);
        statsGrid.add(chiffreAffairesChart, 0, 1);
        statsGrid.add(abonnementChart, 1, 1);
        statsGrid.add(topClientsChart, 0, 2, 2, 1);
    }

    @FXML
    private void handleExportPdf() {
        exportChartsToPdf();
    }

    private void exportChartsToPdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport PDF");
        fileChooser.setInitialFileName("dashboard.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));

        File selectedFile = fileChooser.showSaveDialog(null); // Remplace `null` par `stage` si tu l’as

        if (selectedFile == null) {
            System.out.println("❌ Export annulé par l'utilisateur.");
            return;
        }

        String finalPath = selectedFile.getAbsolutePath();
        exportChartsToPdf(finalPath);
    }

    public void exportChartsToPdf(String customPath) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            float margin = 20f;
            float pageWidth = PDRectangle.A4.getWidth();
            float pageHeight = PDRectangle.A4.getHeight();
            float chartWidth = (pageWidth - 3 * margin) / 2;
            float chartHeight = (pageHeight - 4 * margin) / 3;

            // Recréer les datasets à partir des données
            DefaultPieDataset datasetUsers = new DefaultPieDataset();
            long nbClients = statsService.loadClients().stream().filter(c -> !c.isProfilTransporteur()).count();
            long nbLivreurs = statsService.loadClients().stream().filter(Client::isProfilTransporteur).count();
            long nbCommercants = statsService.loadMerchants().size();
            datasetUsers.setValue("Commerçants", nbCommercants);
            datasetUsers.setValue("Clients", nbClients);
            datasetUsers.setValue("Livreurs", nbLivreurs);

            DefaultPieDataset datasetColis = new DefaultPieDataset();
            statsService.loadDeliveries().stream()
                    .collect(Collectors.groupingBy(DeliveryRow::getStatus, Collectors.counting()))
                    .forEach(datasetColis::setValue);

            DefaultPieDataset datasetCA = new DefaultPieDataset();
            ServicesAPI servicesAPI = new ServicesAPI();
            try {
                List<ServicesAPI.RevenueItem> revenueItems = servicesAPI.getSalesRevenue();
                for (ServicesAPI.RevenueItem item : revenueItems) {
                    datasetCA.setValue(item.getLabel(), item.getRevenue());
                }
            } catch (IOException e) {
                e.printStackTrace();
                datasetCA.setValue("Erreur", 0);
            }

            DefaultPieDataset datasetAbo = new DefaultPieDataset();
            statsService.loadClients().stream()
                    .collect(Collectors.groupingBy(Client::getNomAbonnement, Collectors.counting()))
                    .forEach(datasetAbo::setValue);

            DefaultCategoryDataset datasetTopClients = new DefaultCategoryDataset();
            statsService.loadClients().stream()
                    .sorted(Comparator.comparingInt(Client::getNbDemandeDeLivraison).reversed())
                    .limit(5)
                    .forEach(c -> {
                        String name = c.getFirstName() + " " + c.getLastName();
                        datasetTopClients.addValue(c.getNbDemandeDeLivraison(), "Livraisons", name);
                    });

            // Créer les charts
            JFreeChart chartUsers = ChartFactory.createPieChart("Répartition des utilisateurs", datasetUsers, true, true, false);
            JFreeChart chartColis = ChartFactory.createPieChart("Répartition des colis (statuts)", datasetColis, true, true, false);
            JFreeChart chartCA = ChartFactory.createPieChart("Chiffre d'affaires : Livreurs / Commerçants", datasetCA, true, true, false);
            JFreeChart chartAbo = ChartFactory.createPieChart("Répartition des abonnements", datasetAbo, true, true, false);
            JFreeChart chartTop = ChartFactory.createBarChart("Top 5 clients par livraisons", "Client", "Nb livraisons", datasetTopClients);

            // Dessiner les charts en vectoriel
            PdfBoxGraphics2D g2 = new PdfBoxGraphics2D(document, (int) pageWidth, (int) pageHeight);
            g2.setFontTextDrawer(new PdfBoxGraphics2DFontTextDrawer());
            chartUsers.draw(g2, new Rectangle2D.Double(margin, pageHeight - chartHeight - margin, chartWidth, chartHeight));
            chartColis.draw(g2, new Rectangle2D.Double(margin * 2 + chartWidth, pageHeight - chartHeight - margin, chartWidth, chartHeight));
            chartCA.draw(g2, new Rectangle2D.Double(margin, pageHeight - 2 * chartHeight - 2 * margin, chartWidth, chartHeight));
            chartAbo.draw(g2, new Rectangle2D.Double(margin * 2 + chartWidth, pageHeight - 2 * chartHeight - 2 * margin, chartWidth, chartHeight));
            chartTop.draw(g2, new Rectangle2D.Double(margin, margin, pageWidth - 2 * margin, chartHeight));
            g2.dispose();

            // Ajouter au PDF
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.drawForm(g2.getXFormObject());
            contentStream.close();

            // Sauvegarde
            document.save(customPath);
            System.out.println("✅ PDF vectoriel généré : " + customPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
