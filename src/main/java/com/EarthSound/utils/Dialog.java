package com.EarthSound.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * This class is used to show different Dialog popups to the user (GUI)
 */
public class Dialog {
    public static void showError(String title, String header, String description) {
        showDialog(AlertType.ERROR, title, header, description);
    }

    public static void showWarning(String title, String header, String description) {
        showDialog(AlertType.WARNING, title, header, description);
    }

    public static void showInformation(String title, String header, String description) {
        showDialog(AlertType.INFORMATION, title, header, description);
    }

    public static boolean showConfirmation(String title, String header, String description) {
        return showDialogBoolean(title, header, description);
    }

    /**
     * This dialog shows a textfield and return the text that user puts in
     * @param title title of the dialog
     * @param header the content of the dialog
     * @param description the description of the dialog
     * @return null if closed or press Cancel, otherwhise the string that user input
     */
    public static String showDialogString(String title, String header, String description){
        TextInputDialog dialog = new TextInputDialog("");
        Tools.addCssAndIcon((Stage) dialog.getDialogPane().getScene().getWindow());
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(description);
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }



    private static void showDialog(AlertType type, String title, String header, String description) {
        Alert alert = new Alert(type);
        Tools.addCssAndIcon((Stage) alert.getDialogPane().getScene().getWindow());
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(description);
        alert.showAndWait();
    }

    private static boolean showDialogBoolean(String title, String header, String description) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        Tools.addCssAndIcon((Stage) alert.getDialogPane().getScene().getWindow());
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(description);
        alert.showAndWait();
        return alert.getResult().getButtonData().isDefaultButton();
    }
}
