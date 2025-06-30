package fr.ecodeli.ecodelidesktop.dashboard;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PdfMergerService {

    private final StatsViewController statsViewController;
    private final StatsPrestationsViewController statsPrestationsViewController;

    public PdfMergerService(StatsViewController statsViewController,
                            StatsPrestationsViewController statsPrestationsViewController) {
        this.statsViewController = statsViewController;
        this.statsPrestationsViewController = statsPrestationsViewController;
    }

    public void generateCompletePdf() {
        String tempDir = System.getProperty("java.io.tmpdir");
        String coverPath = tempDir + File.separator + "temp_cover.pdf";
        String statsPath = tempDir + File.separator + "temp_stats.pdf";
        String prestationsPath = tempDir + File.separator + "temp_prestations.pdf";
        String finalPath = System.getProperty("user.home") + File.separator + "Downloads"
                + File.separator + "rapport-complet-ecodeli.pdf";

        try {
            // 1. Créer la page de couverture dans un PDF séparé
            createCoverPdf(coverPath);

            // 2. Générer les PDFs de stats
            generateTempStatsPdf(statsPath);
            generateTempPrestationsPdf(prestationsPath);

            // 3. Merger tous les PDFs
            mergePdfFilesWithUtility(finalPath, coverPath, statsPath, prestationsPath);

            // 4. Nettoyer les fichiers temporaires
            cleanupTempFiles(coverPath, statsPath, prestationsPath);

            System.out.println("✅ Rapport complet généré : " + finalPath);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de la génération du rapport complet");
        }
    }

    private void createCoverPdf(String filePath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage coverPage = new PDPage(PDRectangle.A4);
            document.addPage(coverPage);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, coverPage)) {
                float pageWidth = PDRectangle.A4.getWidth();
                float pageHeight = PDRectangle.A4.getHeight();

                contentStream.setNonStrokingColor(28, 42, 31);
                contentStream.addRect(0, pageHeight - 200, pageWidth, 200);
                contentStream.fill();

                try {
                    PDImageXObject logo = PDImageXObject.createFromFile(
                            "src/main/resources/fr/ecodeli/ecodelidesktop/view/navigation/ecodeli.png",
                            document);
                    float logoSize = 80;
                    contentStream.drawImage(logo, (pageWidth - logoSize) / 2, pageHeight - 100, logoSize, logoSize);
                } catch (Exception e) {
                    System.out.println("Logo non trouvé, génération sans logo");
                }

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 36);
                contentStream.setNonStrokingColor(255, 255, 255); // Blanc

                String title = "EcoDeli";
                float titleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(title) / 1000 * 36;
                contentStream.newLineAtOffset((pageWidth - titleWidth) / 2, pageHeight - 130);
                contentStream.showText(title);
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 16);
                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);
                String dateText = today.format(formatter);
                float dateWidth = PDType1Font.HELVETICA.getStringWidth(dateText) / 1000 * 16;
                contentStream.newLineAtOffset((pageWidth - dateWidth) / 2, pageHeight - 150);
                contentStream.showText(dateText);
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
                contentStream.setNonStrokingColor(0, 0, 0); // Noir
                String subtitle = "Rapport Graphique de l'Activité";
                float subtitleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(subtitle) / 1000 * 24;
                contentStream.newLineAtOffset((pageWidth - subtitleWidth) / 2, pageHeight - 300);
                contentStream.showText(subtitle);
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 14);
                contentStream.setNonStrokingColor(100, 100, 100); // Gris
                String[] descriptionLines = {
                        "Ce rapport présente une analyse complète",
                        "de l'activité d'EcoDeli incluant :",
                        "",
                        "• Statistiques générales des utilisateurs",
                        "• Répartition des livraisons et revenus",
                        "• Analyse détaillée des prestations",
                        "• Top des services les plus demandés"
                };

                float currentY = pageHeight - 380;
                for (String line : descriptionLines) {
                    float lineWidth = PDType1Font.HELVETICA.getStringWidth(line) / 1000 * 14;
                    contentStream.newLineAtOffset((pageWidth - lineWidth) / 2, currentY);
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(-(pageWidth - lineWidth) / 2, -currentY);
                    currentY -= 25;
                }
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
                contentStream.setNonStrokingColor(150, 150, 150);
                String footer = "Généré automatiquement par EcoDeli Desktop";
                float footerWidth = PDType1Font.HELVETICA_OBLIQUE.getStringWidth(footer) / 1000 * 10;
                contentStream.newLineAtOffset((pageWidth - footerWidth) / 2, 50);
                contentStream.showText(footer);
                contentStream.endText();
            }

            document.save(filePath);
        }
    }

    private void generateTempStatsPdf(String filePath) {
        statsViewController.exportChartsToPdf(filePath);
    }

    private void generateTempPrestationsPdf(String filePath) {
        statsPrestationsViewController.exportChartsToPdf(filePath);
    }

    private void mergePdfFilesWithUtility(String outputPath, String... inputPaths) throws IOException {
        PDFMergerUtility mergerUtility = new PDFMergerUtility();

        for (String inputPath : inputPaths) {
            File inputFile = new File(inputPath);
            if (inputFile.exists()) {
                mergerUtility.addSource(inputFile);
                System.out.println("Ajout du fichier : " + inputPath);
            } else {
                System.err.println("⚠️ Fichier non trouvé : " + inputPath);
            }
        }

        mergerUtility.setDestinationFileName(outputPath);

        mergerUtility.mergeDocuments(null);
    }

    private void cleanupTempFiles(String... filePaths) {
        for (String filePath : filePaths) {
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    System.out.println("🗑️ Fichier temporaire supprimé : " + filePath);
                } else {
                    System.err.println("⚠️ Impossible de supprimer : " + filePath);
                }
            }
        }
    }
}