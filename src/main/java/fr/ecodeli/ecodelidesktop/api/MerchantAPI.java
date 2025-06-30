package fr.ecodeli.ecodelidesktop.api;

import com.google.gson.Gson;
import fr.ecodeli.ecodelidesktop.client.CustomOkHttpClient;
import fr.ecodeli.ecodelidesktop.merchant.Merchant;
import fr.ecodeli.ecodelidesktop.service.TokenStorage;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
            System.out.println("Response JSON: " + responseBody); // Pour debug
            return gson.fromJson(responseBody, MerchantDetails.class);
        }
    }

    public byte[] downloadDocument(String documentUrl) throws IOException {
        String[] tokens = TokenStorage.loadTokens();
        String accessToken = tokens[0].split("=")[1];

        String encodedUrl = URLEncoder.encode(documentUrl, StandardCharsets.UTF_8);
        String apiUrl = "/client/utils/document?url=" + encodedUrl;

        Request request = new Request.Builder()
                .url(CustomOkHttpClient.BASE_URL + apiUrl)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        try (Response response = CustomOkHttpClient.getInstance().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erreur lors du téléchargement du document: " + response.code());
            }
            assert response.body() != null;
            return response.body().bytes();
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

    // Nouvelle structure pour correspondre au backend
    public static class MerchantDetails {
        public MerchantInfo info;

        public MerchantInfo getInfo() {
            return info;
        }

        // Méthodes de compatibilité pour le contrôleur existant
        public String getId() {
            return info != null ? info.id : null;
        }

        public String getFirstName() {
            return info != null ? info.first_name : null;
        }

        public String getLastName() {
            return info != null ? info.last_name : null;
        }

        public String getCompanyName() {
            return info != null ? info.entreprise : null;
        }

        public String getCity() {
            return info != null ? info.pays : null; // Ou créer un nouveau champ city si nécessaire
        }

        public String getNomAbonnement() {
            return info != null ? info.nomAbonnement : null;
        }

        public int getNbDemandeDeLivraison() {
            return info != null ? info.nbDemandeDeLivraison : 0;
        }

        public String getEmail() {
            return info != null ? info.email : null;
        }

        public String getPhone() {
            return info != null ? info.phone : null;
        }

        public String getAddress() {
            return info != null ? info.description : null; // Ou créer un nouveau champ address
        }

        public String getContractUrl() {
            return info != null ? info.contractUrl : null;
        }
    }

    public static class MerchantInfo {
        public String id;
        public String profile_picture;
        public String first_name;
        public String last_name;
        public String description;
        public String email;
        public String phone;
        public int nbDemandeDeLivraison;
        public String nomAbonnement;
        public int nbSignalements;
        public String entreprise;
        public String siret;
        public String pays;
        public String contractUrl;
    }
}