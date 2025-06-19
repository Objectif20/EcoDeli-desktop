package fr.ecodeli.ecodelidesktop.stats;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class StatsService {

    public final Gson gson = new Gson();

    public static class Client {
        public String id;
        public String profile_picture;
        public String first_name;
        public String last_name;
        public String email;
        public int nbDemandeDeLivraison;
        public int nbSignalements;
        public String nomAbonnement;
        public int nombreDePrestations;
        public boolean profilTransporteur;
        public String idTransporteur;
        public double chiffreAffaires;

        public String getId() { return id; }
        public String getProfile_picture() { return profile_picture; }
        public String getFirst_name() { return first_name; }
        public String getLast_name() { return last_name; }
        public String getEmail() { return email; }
        public int getNbDemandeDeLivraison() { return nbDemandeDeLivraison; }
        public int getNbSignalements() { return nbSignalements; }
        public String getNomAbonnement() { return nomAbonnement; }
        public int getNombreDePrestations() { return nombreDePrestations; }
        public boolean isProfilTransporteur() { return profilTransporteur; }
        public String getIdTransporteur() { return idTransporteur; }
        public double getChiffreAffaires() { return chiffreAffaires; }
    }

    public static class Merchant {
        public String id;
        public String companyName;
        public String siret;
        public String city;
        public String address;
        public String postalCode;
        public String country;
        public String phone;
        public String description;
        public String profilePicture;
        public String firstName;
        public String lastName;
        public String pays;
        public String entreprise;
        public String nomAbonnement;
        public int nbDemandeDeLivraison;
        public int nbSignalements;

        public String getId() { return id; }
        public String getCompanyName() { return companyName; }
        public String getSiret() { return siret; }
        public String getCity() { return city; }
        public String getAddress() { return address; }
        public String getPostalCode() { return postalCode; }
        public String getCountry() { return country; }
        public String getPhone() { return phone; }
        public String getDescription() { return description; }
        public String getProfilePicture() { return profilePicture; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getPays() { return pays; }
        public String getEntreprise() { return entreprise; }
        public String getNomAbonnement() { return nomAbonnement; }
        public int getNbDemandeDeLivraison() { return nbDemandeDeLivraison; }
        public int getNbSignalements() { return nbSignalements; }
    }

    public static class Delivery {
        public String departure_date;
        public String arrival_date;
        public String status;
        public boolean cart_dropped;
        public boolean isBox;
        public Departure departure;
        public Arrival arrival;
        public List<Package> packages;

        public String getDeparture_date() { return departure_date; }
        public String getArrival_date() { return arrival_date; }
        public String getStatus() { return status; }
        public boolean isCart_dropped() { return cart_dropped; }
        public boolean isBox() { return isBox; }
        public Departure getDeparture() { return departure; }
        public Arrival getArrival() { return arrival; }
        public List<Package> getPackages() { return packages; }

        public static class Departure {
            public String city;
            public String address;
            public String postalCode;
            public double[] coordinates;

            public String getCity() { return city; }
            public String getAddress() { return address; }
            public String getPostalCode() { return postalCode; }
            public double[] getCoordinates() { return coordinates; }
        }

        public static class Arrival {
            public String city;
            public String address;
            public String postalCode;
            public double[] coordinates;

            public String getCity() { return city; }
            public String getAddress() { return address; }
            public String getPostalCode() { return postalCode; }
            public double[] getCoordinates() { return coordinates; }
        }

        public static class Package {
            public String id;
            public String name;
            public boolean fragility;
            public double estimated_price;
            public double weight;
            public double volume;
            public List<String> picture;

            public String getId() { return id; }
            public String getName() { return name; }
            public boolean isFragility() { return fragility; }
            public double getEstimated_price() { return estimated_price; }
            public double getWeight() { return weight; }
            public double getVolume() { return volume; }
            public List<String> getPicture() { return picture; }
        }
    }

    public static class DeliveryOnGoing {
        public String id;
        public String from;
        public String to;
        public String status;
        public String pickupDate;
        public String estimatedDeliveryDate;
        public Coordinates coordinates;
        public double progress;

        public String getId() { return id; }
        public String getFrom() { return from; }
        public String getTo() { return to; }
        public String getStatus() { return status; }
        public String getPickupDate() { return pickupDate; }
        public String getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
        public Coordinates getCoordinates() { return coordinates; }
        public double getProgress() { return progress; }

        public static class Coordinates {
            public double[] origin;
            public double[] destination;

            public double[] getOrigin() { return origin; }
            public double[] getDestination() { return destination; }
        }
    }

    public static class MyService {
        public String service_id;
        public String service_type;
        public String status;
        public String name;
        public String city;
        public double price;
        public double price_admin;
        public double duration_time;
        public boolean available;
        public List<String> keywords;
        public List<String> images;
        public String description;
        public Author author;
        public double rate;
        public List<Comment> comments;

        public String getService_id() { return service_id; }
        public String getService_type() { return service_type; }
        public String getStatus() { return status; }
        public String getName() { return name; }
        public String getCity() { return city; }
        public double getPrice() { return price; }
        public double getPrice_admin() { return price_admin; }
        public double getDuration_time() { return duration_time; }
        public boolean isAvailable() { return available; }
        public List<String> getKeywords() { return keywords; }
        public List<String> getImages() { return images; }
        public String getDescription() { return description; }
        public Author getAuthor() { return author; }
        public double getRate() { return rate; }
        public List<Comment> getComments() { return comments; }

        public static class Author {
            public String id;
            public String name;
            public String email;
            public String photo;

            public String getId() { return id; }
            public String getName() { return name; }
            public String getEmail() { return email; }
            public String getPhoto() { return photo; }
        }

        public static class Comment {
            public String id;
            public Author author;
            public String content;
            public Response response;

            public String getId() { return id; }
            public Author getAuthor() { return author; }
            public String getContent() { return content; }
            public Response getResponse() { return response; }

            public static class Response {
                public String id;
                public Author author;
                public String content;

                public String getId() { return id; }
                public Author getAuthor() { return author; }
                public String getContent() { return content; }
            }
        }

    }
    public static class ClientResponse {
        public List<Client> data;
        public Meta meta;
        public int totalRows;

        public static class Meta {
            public int total;
            public int page;
            public int limit;

            public int getTotal() { return total; }
            public int getPage() { return page; }
            public int getLimit() { return limit; }
        }

        public List<Client> getData() { return data; }
        public Meta getMeta() { return meta; }
        public int getTotalRows() { return totalRows; }
    }

    public static class MerchantResponse {
        public List<Merchant> data;
        public ClientResponse.Meta meta;
        public int totalRows;

        public List<Merchant> getData() { return data; }
        public ClientResponse.Meta getMeta() { return meta; }
        public int getTotalRows() { return totalRows; }
    }

    public static class ServiceResponse {
        public List<MyService> data;
        public int total;
        public int page;
        public int limit;

        public List<MyService> getData() { return data; }
        public int getTotal() { return total; }
        public int getPage() { return page; }
        public int getLimit() { return limit; }
    }



    // 📦 Chargement des clients
    public List<Client> loadClients() {
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream("/data/liste-clients.json"))) {
            Type type = new TypeToken<ClientResponse>(){}.getType();
            ClientResponse response = gson.fromJson(reader, type);
            return response.data != null ? response.data : Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 📦 Chargement des commerçants
    public List<Merchant> loadMerchants() {
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream("/data/liste-commercants.json"))) {
            Type type = new TypeToken<MerchantResponse>(){}.getType();
            MerchantResponse response = gson.fromJson(reader, type);
            return response.data != null ? response.data : Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 📦 Chargement des livraisons
    public List<Delivery> loadDeliveries() {
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream("/data/liste-livraisons.json"))) {
            Type type = new TypeToken<List<Delivery>>(){}.getType();
            return gson.fromJson(reader, type);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 📦 Chargement des prestations
    public List<MyService> loadServices() {
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream("/data/liste-prestations.json"))) {
            Type type = new TypeToken<ServiceResponse>(){}.getType();
            ServiceResponse response = gson.fromJson(reader, type);
            return response.data != null ? response.data : Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 1️⃣ Répartition des abonnements
    public PieChart getAbonnementChart() {
        List<Client> clients = loadClients();
        Map<String, Long> abonnementCounts = clients.stream()
                .collect(Collectors.groupingBy(c -> c.nomAbonnement, Collectors.counting()));

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        abonnementCounts.forEach((abonnement, count) ->
                data.add(new PieChart.Data(abonnement, count)));

        PieChart chart = new PieChart(data);
        chart.setTitle("Répartition des abonnements");
        return chart;
    }

    // 2️⃣ Top 5 des meilleurs clients (fictif)
    public BarChart<String, Number> getTopClientsChart() {
        List<Client> clients = loadClients();
        // 💡 Tu peux calculer chiffreAffaires dynamiquement ici si nécessaire
        clients.forEach(c -> { if (c.chiffreAffaires == 0) c.chiffreAffaires = c.nbDemandeDeLivraison * 20; });

        clients.sort(Comparator.comparingDouble(c -> -c.chiffreAffaires));
        List<Client> top5 = clients.stream().limit(5).collect(Collectors.toList());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Client c : top5) {
            String nom = c.first_name + " " + c.last_name;
            series.getData().add(new XYChart.Data<>(nom, c.chiffreAffaires));
        }

        BarChart<String, Number> chart = new BarChart<>(new javafx.scene.chart.CategoryAxis(), new javafx.scene.chart.NumberAxis());
        chart.setTitle("Top 5 des meilleurs clients");
        chart.getData().add(series);
        return chart;
    }

    // 3️⃣ Répartition des colis expédiés (fictif)
    public PieChart getColisRepartitionChart() {
        List<Delivery> deliveries = loadDeliveries();
        Map<String, Long> repartition = deliveries.stream()
                .collect(Collectors.groupingBy(d -> d.status, Collectors.counting()));

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        repartition.forEach((status, count) ->
                data.add(new PieChart.Data(status, count)));

        PieChart chart = new PieChart(data);
        chart.setTitle("Répartition des colis expédiés (statut)");
        return chart;
    }

    // 4️⃣ Chiffre d'affaires global (fictif) : clients vs commerçants
    public PieChart getChiffreAffairesChart() {
        double totalClients = loadClients().stream()
                .mapToDouble(c -> c.nbDemandeDeLivraison * 20).sum();
        double totalMerchants = loadServices().stream()
                .mapToDouble(s -> s.price).sum();

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
                new PieChart.Data("Clients", totalClients),
                new PieChart.Data("Commerçants", totalMerchants)
        );
        PieChart chart = new PieChart(data);
        chart.setTitle("Chiffre d'affaires : Clients / Commerçants");
        return chart;
    }

    // 5️⃣ Répartition des prestations par durée
    public PieChart getPrestationsParDureeChart() {
        List<MyService> services = loadServices();
        Map<String, Long> durationBuckets = new HashMap<>();
        durationBuckets.put("10-20 min", 0L);
        durationBuckets.put("20-45 min", 0L);
        durationBuckets.put("45+ min", 0L);

        for (MyService s : services) {
            if (s.duration_time <= 20) durationBuckets.put("10-20 min", durationBuckets.get("10-20 min") + 1);
            else if (s.duration_time <= 45) durationBuckets.put("20-45 min", durationBuckets.get("20-45 min") + 1);
            else durationBuckets.put("45+ min", durationBuckets.get("45+ min") + 1);
        }

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        durationBuckets.forEach((bucket, count) ->
                data.add(new PieChart.Data(bucket, count)));

        PieChart chart = new PieChart(data);
        chart.setTitle("Répartition des prestations par durée");
        return chart;
    }
}
