package fr.ecodeli.ecodelidesktop.client;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class CustomOkHttpClient {
    public static final String BASE_URL = "https://app.ecodeli.remythibaut.fr";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static OkHttpClient instance;

    public CustomOkHttpClient() {}

    public static OkHttpClient getInstance() {
        if (instance == null) {
            instance = new OkHttpClient.Builder().build();
        }
        return instance;
    }

    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .post(body)
                .build();

        try (Response response = getInstance().newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            assert response.body() != null;
            return response.body().string();
        }
    }
}
