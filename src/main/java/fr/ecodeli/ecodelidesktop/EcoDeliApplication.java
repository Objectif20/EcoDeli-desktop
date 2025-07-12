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
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        showAuthView();
    }

    public static void showAuthView() {
        try {
            FXMLLoader loader = new FXMLLoader(EcoDeliApplication.class.getResource("/fr/ecodeli/ecodelidesktop/view/auth/AuthView.fxml"));
            Parent root = loader.load();

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            double preferredWidth = screenBounds.getWidth();
            double preferredHeight = screenBounds.getHeight();

            Scene scene = new Scene(root, preferredWidth, preferredHeight);

            scene.getStylesheets().add(
                    Objects.requireNonNull(EcoDeliApplication.class.getResource("/fr/ecodeli/ecodelidesktop/view/auth/auth-style.css")).toExternalForm()
            );

            primaryStage.setTitle("EcoDeli - Connexion");
            primaryStage.setMinWidth(MIN_WIDTH);
            primaryStage.setMinHeight(MIN_HEIGHT);

            primaryStage.setX((screenBounds.getWidth() - preferredWidth) / 2 + screenBounds.getMinX());
            primaryStage.setY((screenBounds.getHeight() - preferredHeight) / 2 + screenBounds.getMinY());

            primaryStage.setScene(scene);
            primaryStage.setWidth(preferredWidth);
            primaryStage.setHeight(preferredHeight);

            if (screenBounds.getWidth() > MIN_WIDTH * 1.5 && screenBounds.getHeight() > MIN_HEIGHT * 1.5) {
                primaryStage.setMaximized(true);
            }

            try {
                primaryStage.getIcons().clear();
                primaryStage.getIcons().add(new Image(Objects.requireNonNull(EcoDeliApplication.class.getResourceAsStream("/fr/ecodeli/ecodelidesktop/view/global/ecodeli.png"))));
            } catch (Exception e) {
                System.out.println("Impossible de charger l'icône de l'application: " + e.getMessage());
            }

            primaryStage.show();
            primaryStage.requestFocus();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(EcoDeliApplication.class.getResource("/fr/ecodeli/ecodelidesktop/view/main/MainView.fxml"));
            Parent root = loader.load();

            double currentWidth = primaryStage.getWidth();
            double currentHeight = primaryStage.getHeight();
            boolean isMaximized = primaryStage.isMaximized();

            Scene scene = new Scene(root, currentWidth, currentHeight);

            scene.getStylesheets().addAll(
                    Objects.requireNonNull(EcoDeliApplication.class.getResource("/fr/ecodeli/ecodelidesktop/view/main/sidebar.css")).toExternalForm(),
                    Objects.requireNonNull(EcoDeliApplication.class.getResource("/fr/ecodeli/ecodelidesktop/view/merchant/merchant-list.css")).toExternalForm(),
                    Objects.requireNonNull(EcoDeliApplication.class.getResource("/fr/ecodeli/ecodelidesktop/view/client/client-list.css")).toExternalForm(),
                    Objects.requireNonNull(EcoDeliApplication.class.getResource("/fr/ecodeli/ecodelidesktop/view/dashboard/dashboard.css")).toExternalForm()
            );

            primaryStage.setTitle("EcoDeli - Dashboard");
            primaryStage.setScene(scene);

            if (isMaximized) {
                primaryStage.setMaximized(true);
            }

            try {
                primaryStage.getIcons().clear();
                primaryStage.getIcons().add(new Image(Objects.requireNonNull(EcoDeliApplication.class.getResourceAsStream("/fr/ecodeli/ecodelidesktop/view/global/ecodeli.png"))));
            } catch (Exception e) {
                System.out.println("Impossible de charger l'icône de l'application: " + e.getMessage());
            }

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}