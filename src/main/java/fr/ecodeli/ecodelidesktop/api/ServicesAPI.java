package fr.ecodeli.ecodelidesktop.api;

import com.google.gson.Gson;
import fr.ecodeli.ecodelidesktop.client.CustomOkHttpClient;
import fr.ecodeli.ecodelidesktop.service.TokenStorage;
import fr.ecodeli.ecodelidesktop.services.Service;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public class ServicesAPI {
    private final Gson gson;

    public ServicesAPI() {
        this.gson = new Gson();
    }

    public ServiceResponse getAllServices(int page, int limit) throws IOException {
        String[] tokens = TokenStorage.loadTokens();
        String accessToken = tokens[0].split("=")[1];

        String url = String.format("/desktop/services?page=%d&limit=%d", page, limit);
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
            return gson.fromJson(responseBody, ServiceResponse.class);
        }
    }

    public Service getServiceById(String id) throws IOException {
        String[] tokens = TokenStorage.loadTokens();
        String accessToken = tokens[0].split("=")[1];

        String url = "/desktop/services/" + id;
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
            return gson.fromJson(responseBody, Service.class);
        }
    }

    public List<TopService> getTop5MostRequestedServices() throws IOException {
        String[] tokens = TokenStorage.loadTokens();
        String accessToken = tokens[0].split("=")[1];

        String url = "/desktop/services/top/popular";
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

            TopService[] topServices = gson.fromJson(responseBody, TopService[].class);
            return List.of(topServices);
        }
    }

    public List<DistributionItem> getAppointmentDistributionOverTime() throws IOException {
        String[] tokens = TokenStorage.loadTokens();
        String accessToken = tokens[0].split("=")[1];

        String url = "/desktop/services/stats/distribution";
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

            DistributionItem[] distributionItems = gson.fromJson(responseBody, DistributionItem[].class);
            return List.of(distributionItems);
        }
    }

    public List<RevenueItem> getSalesRevenue() throws IOException {
        String[] tokens = TokenStorage.loadTokens();
        String accessToken = tokens[0].split("=")[1];

        String url = "/desktop/services/stats/revenue";
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

            RevenueItem[] revenueItems = gson.fromJson(responseBody, RevenueItem[].class);
            return List.of(revenueItems);
        }
    }

    public static class RevenueItem {
        private String label;
        private double revenue;

        public String getLabel() {
            return label;
        }

        public double getRevenue() {
            return revenue;
        }
    }

    public static class DistributionItem {
        private String label;
        private int count;

        public String getLabel() {
            return label;
        }

        public int getCount() {
            return count;
        }
    }

    public static class TopService {
        private String id;
        private String name;
        private int count;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getCount() {
            return count;
        }
    }

    public static class ServiceResponse {
        private List<Service> data;
        private int total;

        public List<Service> getData() {
            return data;
        }

        public int getTotal() {
            return total;
        }
    }
}
