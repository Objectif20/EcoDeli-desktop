package fr.ecodeli.ecodelidesktop.api;

import com.google.gson.Gson;
import fr.ecodeli.ecodelidesktop.client.CustomOkHttpClient;
import fr.ecodeli.ecodelidesktop.merchant.Merchant;
import fr.ecodeli.ecodelidesktop.service.TokenStorage;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public class MerchantAPI {
    private final Gson gson;

    public MerchantAPI() {
        this.gson = new Gson();
    }

    public MerchantResponse getAllMerchants(int page, int limit) throws IOException {
        String[] tokens = TokenStorage.loadTokens();
        String accessToken = tokens[0].split("=")[1];
        String url = String.format("/desktop/merchants?page=%d&limit=%d", page, limit);
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
            System.out.println(responseBody);
            return gson.fromJson(responseBody, MerchantResponse.class);
        }
    }

    public MerchantDetails getMerchantById(String id) throws IOException {
        String[] tokens = TokenStorage.loadTokens();
        String accessToken = tokens[0].split("=")[1];
        String url = "/desktop/merchants/" + id;
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
            return gson.fromJson(responseBody, MerchantDetails.class);
        }
    }

    public static class MerchantResponse {
        public List<Merchant> data;
        public MetaInfo meta;

        public List<Merchant> getData() {
            return data;
        }

        public MetaInfo getMeta() {
            return meta;
        }
    }

    public static class MetaInfo {
        public int total;
        public int page;
        public int limit;

        public int getTotal() {
            return total;
        }

        public int getPage() {
            return page;
        }

        public int getLimit() {
            return limit;
        }
    }

    public static class MerchantDetails {
        public String id;
        public String firstName;
        public String lastName;
        public String companyName;
        public String city;
        public String nomAbonnement;
        public int nbDemandeDeLivraison;
        public String email;
        public String phone;
        public String address;

        public String getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getCompanyName() {
            return companyName;
        }

        public String getCity() {
            return city;
        }

        public String getNomAbonnement() {
            return nomAbonnement;
        }

        public int getNbDemandeDeLivraison() {
            return nbDemandeDeLivraison;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getAddress() {
            return address;
        }
    }
}