package fr.ecodeli.ecodelidesktop.dashboard;

import fr.ecodeli.ecodelidesktop.api.ServicesAPI;
import fr.ecodeli.ecodelidesktop.services.Service; // Assurez-vous que cette ligne est correcte
import fr.ecodeli.ecodelidesktop.stats.StatsService;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import de.rototor.pdfbox.graphics2d.PdfBoxGraphics2D;
import de.rototor.pdfbox.graphics2d.PdfBoxGraphics2DFontTextDrawer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsPrestationsViewController {

    @FXML
    private GridPane statsPrestationsGrid;

    private final StatsService statsService = new StatsService();
    private final ServicesAPI servicesAPI = new ServicesAPI();

    private PieChart prestationsParTypeChart;
    private PieChart prestationsParVilleChart;
    private PieChart prestationsParNoteChart;
    private BarChart<String, Number> topPrestationsChart;
    private LineChart<String, Number> prestationsParTempsChart;

    @FXML
    public void initialize() {
        prestationsParTypeChart = statsService.getPrestationsParTypeChart();
        prestationsParVilleChart = statsService.getPrestationsParVilleChart();
        prestationsParNoteChart = statsService.getPrestationsParNoteChart();
        topPrestationsChart = statsService.getTopServicesChart();
        prestationsParTempsChart = statsService.getPrestationsParTempsChart();

        statsPrestationsGrid.add(prestationsParTypeChart, 0, 0);
        statsPrestationsGrid.add(prestationsParVilleChart, 1, 0);
        statsPrestationsGrid.add(prestationsParNoteChart, 0, 1);
        statsPrestationsGrid.add(topPrestationsChart, 1, 1);
        statsPrestationsGrid.add(prestationsParTempsChart, 0, 2, 2, 1);
    }

    @FXML
    private void handleExportPdf() {
        exportChartsToPdf();
    }

    private BufferedImage captureChartImage(Node chart) {
        WritableImage fxImage = chart.snapshot(new SnapshotParameters(), null);
        return SwingFXUtils.fromFXImage(fxImage, null);
    }

    private void exportChartsToPdf() {
        String filePath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "dashboard-prestations.pdf";
        exportChartsToPdf(filePath);
    }

    public void exportChartsToPdf(String customPath) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            float margin = 20f;
            float chartWidth = 270;
            float chartHeight = 200;

            // Données pour les graphiques
            DefaultPieDataset datasetType = new DefaultPieDataset();
            statsService.loadServices().stream()
                    .collect(Collectors.groupingBy(Service::getServiceType, Collectors.counting()))
                    .forEach(datasetType::setValue);

            DefaultPieDataset datasetVille = new DefaultPieDataset();
            statsService.loadServices().stream()
                    .collect(Collectors.groupingBy(Service::getCity, Collectors.counting()))
                    .forEach(datasetVille::setValue);

            DefaultPieDataset datasetNote = new DefaultPieDataset();
            List<Service> services = statsService.loadServices();
            Map<String, Long> parNote = new LinkedHashMap<>();
            parNote.put("Note < 2.5", 0L);
            parNote.put("Note 2.5 - 3.5", 0L);
            parNote.put("Note 3.5 - 4.5", 0L);
            parNote.put("Note > 4.5", 0L);
            long ignored = 0;

            for (Service s : services) {
                double rate = s.getRate();

                if (rate == 0.0) {
                    ignored++;
                    continue;
                }

                if (rate < 2.5) {
                    parNote.put("Note < 2.5", parNote.get("Note < 2.5") + 1);
                } else if (rate <= 3.5) {
                    parNote.put("Note 2.5 - 3.5", parNote.get("Note 2.5 - 3.5") + 1);
                } else if (rate <= 4.5) {
                    parNote.put("Note 3.5 - 4.5", parNote.get("Note 3.5 - 4.5") + 1);
                } else {
                    parNote.put("Note > 4.5", parNote.get("Note > 4.5") + 1);
                }
            }

            parNote.forEach((label, count) -> {
                if (count > 0) {
                    datasetNote.setValue(label, count);
                }
            });

            DefaultCategoryDataset datasetBar = new DefaultCategoryDataset();
            servicesAPI.getTop5MostRequestedServices().forEach(s ->
                    datasetBar.addValue(s.getCount(), "Prestations", s.getName())
            );

            // Création des graphiques en vectoriel
            PdfBoxGraphics2D g2 = new PdfBoxGraphics2D(document,
                    (int) PDRectangle.A4.getWidth(), (int) PDRectangle.A4.getHeight());
            g2.setFontTextDrawer(new PdfBoxGraphics2DFontTextDrawer());

            JFreeChart chart1 = ChartFactory.createPieChart("Par Type", datasetType, true, true, false);
            chart1.draw(g2, new Rectangle2D.Double(margin, 500, chartWidth, chartHeight));

            JFreeChart chart2 = ChartFactory.createPieChart("Par Ville", datasetVille, true, true, false);
            chart2.draw(g2, new Rectangle2D.Double(margin + chartWidth + margin, 500, chartWidth, chartHeight));

            JFreeChart chart3 = ChartFactory.createPieChart("Par Note", datasetNote, true, true, false);
            chart3.draw(g2, new Rectangle2D.Double(margin, 270, chartWidth, chartHeight));

            JFreeChart chart4 = ChartFactory.createBarChart("Top Prestations", "Prestation", "Demandes", datasetBar);
            chart4.draw(g2, new Rectangle2D.Double(margin + chartWidth + margin, 270, chartWidth, chartHeight));

            // Ajouter le nouveau graphique LineChart
            DefaultCategoryDataset datasetLine = new DefaultCategoryDataset();
            statsService.loadAppointmentDistribution().forEach(item ->
                    datasetLine.addValue(item.getCount(), "Fréquence", item.getLabel())
            );

            JFreeChart chart5 = ChartFactory.createLineChart("Fréquence des prestations dans le temps", "Temps", "Nombre", datasetLine);
            chart5.draw(g2, new Rectangle2D.Double(margin, 50, PDRectangle.A4.getWidth() - 2 * margin, chartHeight));

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
