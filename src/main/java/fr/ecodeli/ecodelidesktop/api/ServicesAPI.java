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
