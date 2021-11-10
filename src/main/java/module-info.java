module EarthSound {
    requires com.sun.xml.bind;
    requires jakarta.xml.bind;
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.base;
    //requires mjson;
    requires org.slf4j;
    requires java.sql;
    requires com.h2database;
    requires com.jfoenix;

    opens com.EarthSound.controllers to javafx.fxml, javafx.controls, javafx.graphics, javafx.media, javafx.base, com.jfoenix;
    opens com.EarthSound.models.beans to jakarta.xml.bind;
    opens com.EarthSound.utils to jakarta.xml.bind, com.sun.xml.bind, com.sun.xml.bind.core, com.jfoenix;
    exports com.EarthSound;
}
