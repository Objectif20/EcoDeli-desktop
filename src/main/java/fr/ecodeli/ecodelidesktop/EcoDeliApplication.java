package fr.ecodeli.ecodelidesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class EcoDeliApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/auth/AuthView.fxml"));
        Parent root = loader.load();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

        String css = getClass().getResource("/fr/ecodeli/ecodelidesktop/view/auth/auth-style.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("EcoDeli - Connexion");
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setScene(scene);
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.setMaximized(true);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/fr/ecodeli/ecodelidesktop/view/global/ecodeli.png")));

        stage.show();

        stage.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
