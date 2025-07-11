package fr.ecodeli.ecodelidesktop.api;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import fr.ecodeli.ecodelidesktop.client.CustomOkHttpClient;
import fr.ecodeli.ecodelidesktop.service.TokenStorage;
import fr.ecodeli.ecodelidesktop.warehouse.Warehouse;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WarehouseAPI {
    private final Gson gson;

    public WarehouseAPI() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Warehouse.class, new WarehouseDeserializer())
                .create();
    }

    private static class WarehouseDeserializer implements JsonDeserializer<Warehouse> {
        @Override
        public Warehouse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) return null;

            JsonObject obj = json.getAsJsonObject();
            Warehouse warehouse = new Warehouse();

            // Identifiant
            warehouse.setWarehouseId(obj.has("warehouse_id") ? obj.get("warehouse_id").getAsString() : null);

            // Champs simples
            warehouse.setCity(obj.has("city") ? obj.get("city").getAsString() : null);
            warehouse.setCapacity(obj.has("capacity") ? obj.get("capacity").getAsInt() : 0);
            warehouse.setDescription(obj.has("description") ? obj.get("description").getAsString() : null);
            warehouse.setAddress(obj.has("address") ? obj.get("address").getAsString() : null);
            warehouse.setPostalCode(obj.has("postal_code") ? obj.get("postal_code").getAsString() : null);
            warehouse.setPhoto(obj.has("photo") ? obj.get("photo").getAsString() : null);

            // Coordonnées (array de chaînes)
            if (obj.has("coordinates") && obj.get("coordinates").isJsonArray()) {
                JsonArray coordsArray = obj.getAsJsonArray("coordinates");
                List<String> coords = new ArrayList<>();
                for (JsonElement element : coordsArray) {
                    coords.add(element.getAsString());
                }
                warehouse.setCoordinates(coords);
            }

            return warehouse;
        }
    }

    public WarehouseResponse getAllWarehouses() throws IOException {
        String[] tokens = TokenStorage.loadTokens();
        String accessToken = tokens[0].split("=")[1];
        String url = "/desktop/warehouses";

        Request request = new Request.Builder()
                .url(CustomOkHttpClient.BASE_URL + url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        try (Response response = CustomOkHttpClient.getInstance().newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            assert response.body() != null;
            String responseBody = response.body().string();

            JsonElement jsonElement = JsonParser.parseString(responseBody);
            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                List<Warehouse> warehouses = new ArrayList<>();
                for (JsonElement element : jsonArray) {
                    Warehouse warehouse = gson.fromJson(element, Warehouse.class);
                    warehouses.add(warehouse);
                }
                WarehouseResponse warehouseResponse = new WarehouseResponse();
                warehouseResponse.data = warehouses;
                warehouseResponse.totalRows = warehouses.size();
                warehouseResponse.meta = new Meta();
                warehouseResponse.meta.total = warehouses.size();
                warehouseResponse.meta.page = 1;
                warehouseResponse.meta.limit = warehouses.size();
                return warehouseResponse;
            } else {
                return gson.fromJson(responseBody, WarehouseResponse.class);
            }
        }
    }

    public void createWarehouse(WarehouseCreateRequest warehouse, File file) throws IOException {
        String[] tokens = TokenStorage.loadTokens();
        String accessToken = tokens[0].split("=")[1];
        String url = "/desktop/warehouses";

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        multipartBuilder.addFormDataPart("city", warehouse.city);
        multipartBuilder.addFormDataPart("capacity", String.valueOf(warehouse.capacity));
        multipartBuilder.addFormDataPart("coordinates", warehouse.coordinates); // [longitude, latitude] stringifiée
        multipartBuilder.addFormDataPart("address", warehouse.address);
        multipartBuilder.addFormDataPart("postal_code", warehouse.postalCode);
        if (warehouse.description != null) {
            multipartBuilder.addFormDataPart("description", warehouse.description);
        }
        if (file != null) {
            multipartBuilder.addFormDataPart("file", file.getName(),
                    RequestBody.create(file, MediaType.parse("application/octet-stream")));
        }

        RequestBody requestBody = multipartBuilder.build();

        Request request = new Request.Builder()
                .url(CustomOkHttpClient.BASE_URL + url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .post(requestBody)
                .build();

        try (Response response = CustomOkHttpClient.getInstance().newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            assert response.body() != null;
            response.body().string(); // Ignoré ici
        }
    }

    public void updateWarehouse(String id, WarehouseUpdateRequest warehouse, File file) throws IOException {
        String[] tokens = TokenStorage.loadTokens();
        String accessToken = tokens[0].split("=")[1];
        String url = "/desktop/warehouses/" + id;

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (warehouse.city != null) multipartBuilder.addFormDataPart("city", warehouse.city);
        if (warehouse.capacity != null) multipartBuilder.addFormDataPart("capacity", String.valueOf(warehouse.capacity));
        if (warehouse.coordinates != null) multipartBuilder.addFormDataPart("coordinates", warehouse.coordinates);
        if (warehouse.address != null) multipartBuilder.addFormDataPart("address", warehouse.address);
        if (warehouse.postalCode != null) multipartBuilder.addFormDataPart("postal_code", warehouse.postalCode);
        if (warehouse.description != null) multipartBuilder.addFormDataPart("description", warehouse.description);
        if (file != null) {
            multipartBuilder.addFormDataPart("file", file.getName(),
                    RequestBody.create(file, MediaType.parse("application/octet-stream")));
        }

        RequestBody requestBody = multipartBuilder.build();

        Request request = new Request.Builder()
                .url(CustomOkHttpClient.BASE_URL + url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .patch(requestBody)
                .build();

        try (Response response = CustomOkHttpClient.getInstance().newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            assert response.body() != null;
            response.body().string(); // Ignoré ici
        }
    }

    public static String coordinatesToPointJson(double latitude, double longitude) {
        return String.format("[\"%.6f\",\"%.6f\"]", longitude, latitude);
    }

    public static class WarehouseResponse {
        public List<Warehouse> data;
        public Meta meta;
        public int totalRows;

        public List<Warehouse> getData() { return data; }
        public Meta getMeta() { return meta; }
        public int getTotalRows() { return totalRows; }
    }

    public static class Meta {
        public int total;
        public int page;
        public int limit;

        public int getTotal() { return total; }
        public int getPage() { return page; }
        public int getLimit() { return limit; }
    }

    public static class WarehouseCreateRequest {
        public String city;
        public int capacity;
        public String coordinates; // JSON array sous forme de string : ["longitude", "latitude"]
        public String description;
        public String address;
        public String postalCode;
    }

    public static class WarehouseUpdateRequest {
        public String city;
        public Integer capacity;
        public String coordinates;
        public String description;
        public String address;
        public String postalCode;
    }
}