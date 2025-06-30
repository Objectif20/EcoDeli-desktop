package fr.ecodeli.ecodelidesktop.api;

import com.google.gson.Gson;
import fr.ecodeli.ecodelidesktop.client.CustomOkHttpClient;
import fr.ecodeli.ecodelidesktop.clients.Client;
import fr.ecodeli.ecodelidesktop.service.TokenStorage;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public class ClientAPI {
    private final Gson gson;

    public ClientAPI() {
        this.gson = new Gson();
    }

    public ClientResponse getAllClients(int page, int limit) throws IOException {
        String[] tokens = TokenStorage.loadTokens();
        String accessToken = tokens[0].split("=")[1];
        String url = String.format("/desktop/clients?page=%d&limit=%d", page, limit);

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
            return gson.fromJson(responseBody, ClientResponse.class);
        }
    }

    public ClientDetails getClientById(String id) throws IOException {
        String[] tokens = TokenStorage.loadTokens();
        String accessToken = tokens[0].split("=")[1];
        String url = "/desktop/clients/" + id;

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
            return gson.fromJson(responseBody, ClientDetails.class);
        }
    }

    public static class ClientResponse {
        public List<Client> data;
        public Meta meta;
        public int totalRows;

        public List<Client> getData() {
            return data;
        }

        public Meta getMeta() {
            return meta;
        }

        public int getTotalRows() {
            return totalRows;
        }
    }

    public static class Meta {
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

    public static class ClientDetails {
        public Info info;

        public Info getInfo() {
            return info;
        }

        public static class Info {
            public String profile_picture;
            public String first_name;
            public String last_name;
            public String email;
            public int nbDemandeDeLivraison;
            public String nomAbonnement;
            public int nbSignalements;
            public int nombreDePrestations;
            public boolean profilTransporteur;
            public String idTransporteur;

            public String getProfilePicture() { return profile_picture; }
            public String getFirstName() { return first_name; }
            public String getLastName() { return last_name; }
            public String getEmail() { return email; }
            public int getNbDemandeDeLivraison() { return nbDemandeDeLivraison; }
            public String getNomAbonnement() { return nomAbonnement; }
            public int getNbSignalements() { return nbSignalements; }
            public int getNombreDePrestations() { return nombreDePrestations; }
            public boolean isProfilTransporteur() { return profilTransporteur; }
            public String getIdTransporteur() { return idTransporteur; }
        }
    }
}
