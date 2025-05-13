module fr.ecodeli.ecodelidesktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;

    opens fr.ecodeli.ecodelidesktop to javafx.fxml;
    exports fr.ecodeli.ecodelidesktop;
}