package fr.ecodeli.ecodelidesktop.dashboard;

import fr.ecodeli.ecodelidesktop.stats.StatsService;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsPrestationsViewController {

    @FXML
    private GridPane statsPrestationsGrid;

    private final StatsService statsService = new StatsService();

    private PieChart prestationsParTypeChart;
    private PieChart prestationsParVilleChart;
    private PieChart prestationsParNoteChart;
    private BarChart<String, Number> topPrestationsChart;

    @FXML
    public void initialize() {
        prestationsParTypeChart = statsService.getPrestationsParTypeChart();
        prestationsParVilleChart = statsService.getPrestationsParVilleChart();
        prestationsParNoteChart = statsService.getPrestationsParNoteChart();
        topPrestationsChart = statsService.getTopServicesChart();

        statsPrestationsGrid.add(prestationsParTypeChart, 0, 0);
        statsPrestationsGrid.add(prestationsParVilleChart, 1, 0);
        statsPrestationsGrid.add(prestationsParNoteChart, 0, 1);
        statsPrestationsGrid.add(topPrestationsChart, 1, 1);
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
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            float margin = 20f;
            float chartWidth = 270;
            float chartHeight = 200;

            // Données pour les graphiques
            DefaultPieDataset datasetType = new DefaultPieDataset();
            statsService.loadServices().stream()
                    .collect(Collectors.groupingBy(s -> s.service_type, Collectors.counting()))
                    .forEach(datasetType::setValue);

            DefaultPieDataset datasetVille = new DefaultPieDataset();
            statsService.loadServices().stream()
                    .collect(Collectors.groupingBy(s -> s.city, Collectors.counting()))
                    .forEach(datasetVille::setValue);

            DefaultPieDataset datasetNote = new DefaultPieDataset();
            Map<String, Long> notes = Map.of(
                    "Note < 4", statsService.loadServices().stream().filter(s -> s.rate < 4).count(),
                    "Note 4 - 4.5", statsService.loadServices().stream().filter(s -> s.rate >= 4 && s.rate <= 4.5).count(),
                    "Note > 4.5", statsService.loadServices().stream().filter(s -> s.rate > 4.5).count()
            );
            notes.forEach(datasetNote::setValue);

            DefaultCategoryDataset datasetBar = new DefaultCategoryDataset();
            statsService.loadServices().stream()
                    .sorted(Comparator.comparingInt(s -> -s.nbDemandeDeLivraison))
                    .limit(5)
                    .forEach(s -> datasetBar.addValue(s.nbDemandeDeLivraison, "Prestations", s.name));

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

            g2.dispose();

            // Ajouter au pdf
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.drawForm(g2.getXFormObject());
            contentStream.close();

            // Sauvegarde
            String filePath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "dashboard-prestations.pdf";
            document.save(filePath);
            System.out.println("✅ PDF vectoriel généré : " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
