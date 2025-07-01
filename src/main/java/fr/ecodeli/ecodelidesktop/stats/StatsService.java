package fr.ecodeli.ecodelidesktop.stats;

import fr.ecodeli.ecodelidesktop.api.ClientAPI;
import fr.ecodeli.ecodelidesktop.api.DeliveryAPI;
import fr.ecodeli.ecodelidesktop.api.MerchantAPI;
import fr.ecodeli.ecodelidesktop.api.ServicesAPI;
import fr.ecodeli.ecodelidesktop.clients.Client;
import fr.ecodeli.ecodelidesktop.delivery.DeliveryRow;
import fr.ecodeli.ecodelidesktop.merchant.Merchant;
import fr.ecodeli.ecodelidesktop.services.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class StatsService {

    private final ClientAPI clientAPI;
    private final MerchantAPI merchantAPI;
    private final ServicesAPI servicesAPI;
    private final DeliveryAPI deliveryAPI;

    public StatsService() {
        this.clientAPI = new ClientAPI();
        this.merchantAPI = new MerchantAPI();
        this.servicesAPI = new ServicesAPI();
        this.deliveryAPI = new DeliveryAPI();
    }

    public List<Client> loadClients() {
        try {
            ClientAPI.ClientResponse response = clientAPI.getAllClients(1, 100); // Adjust page and limit as needed
            return response.getData() != null ? response.getData() : Collections.emptyList();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Merchant> loadMerchants() {
        try {
            MerchantAPI.MerchantResponse response = merchantAPI.getAllMerchants(1, 100); // Adjust page and limit as needed
            return response.getData() != null ? response.getData() : Collections.emptyList();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<DeliveryRow> loadDeliveries() {
        try {
            DeliveryAPI.DeliveryResponse response = deliveryAPI.getAllOngoingDeliveries(1, 100); // Adjust page and limit as needed
            return response.getDeliveries().stream().map(DeliveryRow::new).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Service> loadServices() {
        try {
            ServicesAPI.ServiceResponse response = servicesAPI.getAllServices(1, 100); // Adjust page and limit as needed
            return response.getData() != null ? response.getData() : Collections.emptyList();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public PieChart getAbonnementChart() {
        List<Client> clients = loadClients();
        Map<String, Long> abonnementCounts = clients.stream()
                .collect(Collectors.groupingBy(Client::getNomAbonnement, Collectors.counting()));

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        abonnementCounts.forEach((abonnement, count) ->
                data.add(new PieChart.Data(abonnement, count)));

        PieChart chart = new PieChart(data);
        chart.setTitle("Répartition des abonnements");
        return chart;
    }

    public BarChart<String, Number> getTopClientsChart() {
        List<Client> clients = loadClients();
        clients.sort(Comparator.comparingInt(Client::getNbDemandeDeLivraison).reversed());
        List<Client> top5 = clients.stream().limit(5).collect(Collectors.toList());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Client c : top5) {
            String nom = c.getFirstName() + " " + c.getLastName();
            series.getData().add(new XYChart.Data<>(nom, c.getNbDemandeDeLivraison()));
        }

        BarChart<String, Number> chart = new BarChart<>(new javafx.scene.chart.CategoryAxis(), new javafx.scene.chart.NumberAxis());
        chart.setTitle("Top 5 clients par nombre de livraisons");
        chart.getData().add(series);
        return chart;
    }

    public PieChart getColisRepartitionChart() {
        List<DeliveryRow> deliveries = loadDeliveries();
        Map<String, Long> repartition = deliveries.stream()
                .collect(Collectors.groupingBy(DeliveryRow::getStatus, Collectors.counting()));

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        repartition.forEach((status, count) ->
                data.add(new PieChart.Data(status, count)));

        PieChart chart = new PieChart(data);
        chart.setTitle("Répartition des colis expédiés (statut)");
        return chart;
    }

    public PieChart getChiffreAffairesChart() {
        ServicesAPI servicesAPI = new ServicesAPI();
        List<ServicesAPI.RevenueItem> revenueItems;

        try {
            revenueItems = servicesAPI.getSalesRevenue();
        } catch (IOException e) {
            e.printStackTrace();
            return new PieChart();
        }

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

        for (ServicesAPI.RevenueItem item : revenueItems) {
            data.add(new PieChart.Data(item.getLabel(), item.getRevenue()));
        }

        PieChart chart = new PieChart(data);
        chart.setTitle("Chiffre d'affaires");
        return chart;
    }

    public PieChart getPrestationsParDureeChart() {
        List<Service> services = loadServices();
        Map<String, Long> durationBuckets = new HashMap<>();
        durationBuckets.put("10-20 min", 0L);
        durationBuckets.put("20-45 min", 0L);
        durationBuckets.put("45+ min", 0L);

        for (Service s : services) {
            if (s.getDurationTime() <= 20) durationBuckets.put("10-20 min", durationBuckets.get("10-20 min") + 1);
            else if (s.getDurationTime() <= 45) durationBuckets.put("20-45 min", durationBuckets.get("20-45 min") + 1);
            else durationBuckets.put("45+ min", durationBuckets.get("45+ min") + 1);
        }

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        durationBuckets.forEach((bucket, count) ->
                data.add(new PieChart.Data(bucket, count)));

        PieChart chart = new PieChart(data);
        chart.setTitle("Répartition des prestations par durée");
        return chart;
    }

    public PieChart getRepartitionUtilisateursChart() {
        List<Client> clients = loadClients();
        List<Merchant> merchants = loadMerchants();

        System.out.println("=== Clients chargés ===");
        for (Client c : clients) {
            System.out.println(c.toString());
        }

        long nbClients = clients.stream().filter(c -> !c.isProfilTransporteur()).count();

        long nbLivreurs = clients.stream().filter(Client::isProfilTransporteur).count();

        long nbCommercants = merchants.size();

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
                new PieChart.Data("Commerçants", nbCommercants),
                new PieChart.Data("Clients", nbClients),
                new PieChart.Data("Clients + Transporteurs", nbLivreurs)
        );

        PieChart chart = new PieChart(data);
        chart.setTitle("Répartition des utilisateurs");
        return chart;
    }

    public PieChart getPrestationsParTypeChart() {
        List<Service> services = loadServices();
        Map<String, Long> typeCounts = services.stream()
                .collect(Collectors.groupingBy(Service::getServiceType, Collectors.counting()));

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        typeCounts.forEach((type, count) ->
                data.add(new PieChart.Data(type, count)));

        PieChart chart = new PieChart(data);
        chart.setTitle("Répartition des prestations par type");
        return chart;
    }

    public PieChart getPrestationsParFrequenceChart() {
        try {
            ServicesAPI servicesAPI = new ServicesAPI();
            List<ServicesAPI.TopService> topServices = servicesAPI.getTop5MostRequestedServices();

            Map<String, Long> frequenceBuckets = new HashMap<>();
            frequenceBuckets.put("Peu demandées", 0L);
            frequenceBuckets.put("Demandées", 0L);
            frequenceBuckets.put("Très demandées", 0L);

            long tresDemandees = topServices.size();

            frequenceBuckets.put("Très demandées", tresDemandees);
            frequenceBuckets.put("Demandées", 10L);
            frequenceBuckets.put("Peu demandées", 20L);

            ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
            frequenceBuckets.forEach((label, count) ->
                    data.add(new PieChart.Data(label, count)));

            PieChart chart = new PieChart(data);
            chart.setTitle("Fréquence d'utilisation des prestations");
            return chart;
        } catch (IOException e) {
            e.printStackTrace();
            return new PieChart(FXCollections.observableArrayList());
        }
    }

    public BarChart<String, Number> getTopServicesChart() {
        try {
            ServicesAPI servicesAPI = new ServicesAPI();
            List<ServicesAPI.TopService> topServices = servicesAPI.getTop5MostRequestedServices();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (ServicesAPI.TopService service : topServices) {
                series.getData().add(new XYChart.Data<>(service.getName(), service.getCount()));
            }

            BarChart<String, Number> chart = new BarChart<>(
                    new javafx.scene.chart.CategoryAxis(),
                    new javafx.scene.chart.NumberAxis()
            );
            chart.setTitle("Top 5 prestations les plus demandées");
            chart.getData().add(series);
            return chart;
        } catch (IOException e) {
            e.printStackTrace();
            return new BarChart<>(new javafx.scene.chart.CategoryAxis(), new javafx.scene.chart.NumberAxis());
        }
    }

    public PieChart getPrestationsParNoteChart() {
        List<Service> services = loadServices();
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

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        parNote.forEach((label, count) -> {
            if (count > 0) {
                data.add(new PieChart.Data(label, count));
            }
        });

        PieChart chart = new PieChart(data);
        chart.setTitle("Répartition des prestations par note");
        return chart;
    }

    public PieChart getPrestationsParVilleChart() {
        List<Service> services = loadServices();
        Map<String, Long> parVille = services.stream()
                .collect(Collectors.groupingBy(Service::getCity, Collectors.counting()));

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        parVille.forEach((ville, count) ->
                data.add(new PieChart.Data(ville, count)));

        PieChart chart = new PieChart(data);
        chart.setTitle("Répartition des prestations par ville");
        return chart;
    }

    public List<ServicesAPI.DistributionItem> loadAppointmentDistribution() {
        try {
            return servicesAPI.getAppointmentDistributionOverTime();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public LineChart<String, Number> getPrestationsParTempsChart() {
        List<ServicesAPI.DistributionItem> distributionItems = loadAppointmentDistribution();

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Fréquence des prestations dans le temps");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre de prestations");

        for (ServicesAPI.DistributionItem item : distributionItems) {
            series.getData().add(new XYChart.Data<>(item.getLabel(), item.getCount()));
        }

        lineChart.getData().add(series);
        return lineChart;
    }
}
