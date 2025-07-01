module fr.ecodeli.ecodelidesktop {
    requires okhttp3;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires javafx.base;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires com.google.gson;
    requires javafx.swing;
    requires org.apache.pdfbox;
    requires org.jfree.jfreechart;
    requires de.rototor.pdfbox.graphics2d;


    opens fr.ecodeli.ecodelidesktop to javafx.fxml;
    opens fr.ecodeli.ecodelidesktop.view.auth to javafx.fxml;
    opens fr.ecodeli.ecodelidesktop.model to com.google.gson;
    opens fr.ecodeli.ecodelidesktop.stats to com.google.gson;
    opens fr.ecodeli.ecodelidesktop.clients to com.google.gson, javafx.fxml, javafx.base;
    opens fr.ecodeli.ecodelidesktop.dashboard to javafx.fxml;
    opens fr.ecodeli.ecodelidesktop.controller to javafx.fxml;
    opens fr.ecodeli.ecodelidesktop.delivery to javafx.fxml, com.google.gson, javafx.base;
    opens fr.ecodeli.ecodelidesktop.merchant to com.google.gson, javafx.fxml, javafx.base;
    opens fr.ecodeli.ecodelidesktop.services to javafx.fxml, com.google.gson, javafx.base;
    opens fr.ecodeli.ecodelidesktop.api to com.google.gson;
    opens fr.ecodeli.ecodelidesktop.view.client to javafx.fxml;
    opens fr.ecodeli.ecodelidesktop.view.merchant to javafx.fxml;
    opens fr.ecodeli.ecodelidesktop.view.delivery to javafx.fxml;
    opens fr.ecodeli.ecodelidesktop.view.service to javafx.fxml;

    exports fr.ecodeli.ecodelidesktop;
    exports fr.ecodeli.ecodelidesktop.view.auth;
    exports fr.ecodeli.ecodelidesktop.dashboard to javafx.fxml;
    exports fr.ecodeli.ecodelidesktop.clients to javafx.fxml;
}
