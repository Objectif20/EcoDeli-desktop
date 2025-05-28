module fr.ecodeli.ecodelidesktop {
    requires okhttp3;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires com.google.gson;

    opens fr.ecodeli.ecodelidesktop to javafx.fxml;
    opens fr.ecodeli.ecodelidesktop.view.auth to javafx.fxml;
    opens fr.ecodeli.ecodelidesktop.model to com.google.gson;
    opens fr.ecodeli.ecodelidesktop.dashboard to javafx.fxml;
    exports fr.ecodeli.ecodelidesktop;
    exports fr.ecodeli.ecodelidesktop.view.auth;
    exports fr.ecodeli.ecodelidesktop.dashboard to javafx.fxml;
}