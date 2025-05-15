package fr.ecodeli.ecodelidesktop.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class TokenStorage {
    private static final String AUTH_FILE = "app/data/auth.txt";

    public static void saveTokens(String accessToken, String refreshToken) throws IOException {
        File authFile = new File(AUTH_FILE);
        authFile.getParentFile().mkdirs();

        if (!authFile.exists()) {
            authFile.createNewFile();
        }

        String content = String.format("access_token=%s\nrefresh_token=%s", accessToken, refreshToken);
        Files.write(Paths.get(AUTH_FILE), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static String[] loadTokens() throws IOException {
        File authFile = new File(AUTH_FILE);
        if (!authFile.exists()) {
            throw new IOException("Token file does not exist.");
        }
        return new String(Files.readAllBytes(Paths.get(AUTH_FILE))).split("\n");
    }
}
