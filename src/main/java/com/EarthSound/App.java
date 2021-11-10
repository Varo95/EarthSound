package com.EarthSound;

import com.EarthSound.utils.Dialog;
import com.EarthSound.utils.Tools;
import com.EarthSound.utils.connections.SQL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class App extends Application {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    @Override
    public void start(Stage stage) {
        SQL.connect();
        loadScene(stage, "login", " EarthSound", false, false);
    }

    /**
     * This method is used to load the view with controlled
     * @param fxml view to load
     * @return null if error on controller or on fxml, otherwhise, the functional nodes of the window
     */
    private static Parent loadFXML(String fxml) {
        Parent result;
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        try {
            result = fxmlLoader.load();
        } catch (IOException e) {
            logger.error("Hubo un error al cargar la vista "+fxml+" con el mensaje:\n"+e.getMessage());
            Dialog.showError("ERROR", "Hubo un error al cargar la vista", "La vista " + fxml + " no pudo cargarse debido a:\n " + e.getMessage());
            result = null;
        }
        return result;
    }

    /**
     * This method is called when we need to show up a new window
     * @param stage a new Stage if we want a new window
     * @param fxml name of the fxml, without ".fxml". It bust be in the same level from resources as App.java or App.class
     *             ie: if App is on java/package.controls.App, fxml must be at resources/package.controls.App
     * @param title window title
     * @param SaW must the background window wait or not?
     * @param isResizable if the window can be resized or not
     */
    public static void loadScene(Stage stage, String fxml, String title, boolean SaW, boolean isResizable) {
        stage.setScene(new Scene(loadFXML(fxml)));
        Tools.addCssAndIcon(stage);
        stage.setTitle(title);
        stage.setResizable(isResizable);
        if (SaW)
            stage.showAndWait();
        else
            stage.show();
    }

    /**
     * This method is used to close a window
     * @param stage stage that we want to close
     */
    public static void closeScene(Stage stage) {
        stage.close();
    }

    public static void main(String[] args) {
        launch();
        SQL.disconnect();
    }
}
