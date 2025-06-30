package fr.ecodeli.ecodelidesktop.api;

import com.google.gson.Gson;
import fr.ecodeli.ecodelidesktop.client.CustomOkHttpClient;
import fr.ecodeli.ecodelidesktop.service.TokenStorage;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public class DeliveryAPI {
    private final Gson gson;

    public DeliveryAPI() {
        this.gson = new Gson();
    }

    public DeliveryResponse getAllOngoingDeliveries(int page, int limit) throws IOException {
        String[] tokens = TokenStorage.loadTokens();
        String accessToken = tokens[0].split("=")[1];
        String url = String.format("/desktop/deliveries/ongoing?page=%d&limit=%d", page, limit);

        Request request = new Request.Builder()
                .url(CustomOkHttpClient.BASE_URL + url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        try (Response response = CustomOkHttpClient.getInstance().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            assert response.body() != null;
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, DeliveryResponse.class);
        }
    }

    public DeliveryDetails getDeliveryById(String id) throws IOException {
        String[] tokens = TokenStorage.loadTokens();
        String accessToken = tokens[0].split("=")[1];
        String url = "/desktop/deliveries/" + id;

        Request request = new Request.Builder()
                .url(CustomOkHttpClient.BASE_URL + url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        try (Response response = CustomOkHttpClient.getInstance().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            assert response.body() != null;
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, DeliveryDetails.class);
        }
    }

    public static class DeliveryResponse {
        public List<DeliveryOngoing> deliveries;
        public int totalRows;

        public List<DeliveryOngoing> getDeliveries() {
            return deliveries;
        }

        public int getTotalRows() {
            return totalRows;
        }
    }

    public static class DeliveryOngoing {
        public String id;
        public String from;
        public String to;
        public String status;
        public String pickupDate;
        public String estimatedDeliveryDate;
        public Coordinates coordinates;
        public double  progress;
        public boolean isBox;
        public double price;
        public int packageCount;

        public static class Coordinates {
            public double[] origin;
            public double[] destination;
        }
    }

    public static class DeliveryDetails {

        public Location departure;
        public Location arrival;
        public String departure_date;
        public String arrival_date;
        public String status;
        public double total_price;
        public boolean cart_dropped;
        public boolean isBox;
        public List<Package> packages;

        public static class Location {
            public String city;
            public String address;
            public String postalCode;
            public double[] coordinates;
        }

        public static class Package {
            public String id;
            public String name;
            public boolean fragility;
            public double estimated_price;
            public double weight;
            public double volume;
            public List<String> picture;
        }
    }
}
