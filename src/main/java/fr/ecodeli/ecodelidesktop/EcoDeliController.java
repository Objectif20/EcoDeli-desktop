package fr.ecodeli.ecodelidesktop;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EcoDeliController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Bienvenue dans EcoDeli !");
    }
}
