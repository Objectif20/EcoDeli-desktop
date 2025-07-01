package fr.ecodeli.ecodelidesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.util.Objects;

public class EcoDeliApplication extends Application {

    private static final double MIN_WIDTH = 800;
    private static final double MIN_HEIGHT = 600;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/auth/AuthView.fxml"));
        Parent root = loader.load();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        double preferredWidth = Math.max(screenBounds.getWidth() * 0.9, MIN_WIDTH);
        double preferredHeight = Math.max(screenBounds.getHeight() * 0.9, MIN_HEIGHT);

        Scene scene = new Scene(root, preferredWidth, preferredHeight);

        scene.getStylesheets().addAll(
                Objects.requireNonNull(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/auth/auth-style.css")).toExternalForm(),
                Objects.requireNonNull(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/merchant/merchant-list.css")).toExternalForm(),
                Objects.requireNonNull(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/client/client-list.css")).toExternalForm(),
                Objects.requireNonNull(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/dashboard/dashboard.css")).toExternalForm()
        );

        stage.setTitle("EcoDeli - Connexion");

        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);

        stage.setX((screenBounds.getWidth() - preferredWidth) / 2 + screenBounds.getMinX());
        stage.setY((screenBounds.getHeight() - preferredHeight) / 2 + screenBounds.getMinY());

        stage.setScene(scene);
        stage.setWidth(preferredWidth);
        stage.setHeight(preferredHeight);

        if (screenBounds.getWidth() > MIN_WIDTH * 1.5 && screenBounds.getHeight() > MIN_HEIGHT * 1.5) {
            stage.setMaximized(true);
        }

        try {
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/fr/ecodeli/ecodelidesktop/view/global/ecodeli.png"))));
        } catch (Exception e) {
            System.out.println("Impossible de charger l'icône de l'application: " + e.getMessage());
        }

        stage.show();
        stage.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}