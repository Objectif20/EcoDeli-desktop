package fr.ecodeli.ecodelidesktop.delivery;

import fr.ecodeli.ecodelidesktop.api.DeliveryAPI;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DeliveryRow {
    private final String id;
    private final String departureDate;
    private final String arrivalDate;
    private final String status;
    private final String isBox;
    private final String departureCity;
    private final String arrivalCity;
    private final String price;
    private final String packageCount;

    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.FRANCE);
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE);

    public DeliveryRow(DeliveryAPI.DeliveryOngoing d) {
        this.id = d.id;
        this.departureDate = formatDate(d.pickupDate);
        this.arrivalDate = formatDate(d.estimatedDeliveryDate);
        this.status = translateStatus(d.status);
        this.isBox = d.isBox ? "Oui" : "Non";
        this.departureCity = d.from != null ? d.from : "Inconnu";
        this.arrivalCity = d.to != null ? d.to : "Inconnu";
        this.price = formatPrice(d.price);
        this.packageCount = String.valueOf(d.packageCount);
    }

    private String formatDate(String isoDate) {
        try {
            LocalDate date = LocalDate.parse(isoDate, INPUT_FORMATTER);
            return date.format(OUTPUT_FORMATTER);
        } catch (Exception e) {
            return "Date inconnue";
        }
    }

    private String formatPrice(double price) {
        if (price == (long) price) {
            return String.format("%d €", (long) price);
        } else {
            return String.format(Locale.FRANCE, "%.2f €", price);
        }
    }

    private String translateStatus(String status) {
        return switch (status.toLowerCase()) {
            case "pending" -> "En attente";
            case "taken" -> "En cours";
            case "finished" -> "Terminée";
            case "validated" -> "Validée";
            default -> "Inconnu";
        };
    }

    public String getId() { return id; }
    public String getDepartureDate() { return departureDate; }
    public String getArrivalDate() { return arrivalDate; }
    public String getStatus() { return status; }
    public String getIsBox() { return isBox; }
    public String getDepartureCity() { return departureCity; }
    public String getArrivalCity() { return arrivalCity; }
    public String getPrice() { return price; }
    public String getPackageCount() { return packageCount; }
}
