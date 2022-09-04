package com.EarthSound.controllers;

import com.EarthSound.App;
import com.EarthSound.models.DAO.SQL.UserDAO;
import com.EarthSound.models.beans.User;
import com.EarthSound.utils.Dialog;
import com.EarthSound.utils.Tools;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

public class ProfileController {
    @FXML
    private ImageView iview_profile;
    @FXML
    private TextField tfnick;
    @FXML
    private PasswordField tfpasswd;
    @FXML
    private JFXButton btn_save, lock, btn_photo, btnpwd;
    private static final String default_photo = "assets/user_default.png";
    private static String photo = "";
    private static User actual_user;
    private Tooltip toolTip;

    @FXML
    protected void initialize() {
        if (actual_user.getId() == -1) {
            lock.setVisible(false);
            btn_save.setText("Registrarse");
        } else {
            tfnick.setDisable(true);
            tfpasswd.setDisable(true);
            iview_profile.setDisable(true);
            btn_photo.setDisable(true);
            btnpwd.setDisable(true);
        }
        //ToolTip to show password
        toolTip = new Tooltip();
        toolTip.setShowDelay(Duration.ZERO);
        toolTip.setAutoHide(false);
        toolTip.setMinWidth(50);
        //------------------------
        Image i = Tools.getImage(actual_user.getPhotoURL(), false);
        iview_profile.setImage(Objects.requireNonNullElseGet(i, () -> Tools.getImage(default_photo, true)));
        tfnick.setText(actual_user.getName());
        tfpasswd.setText(actual_user.getPassword());
        //Photo Examine Button function declared here\/
        btn_photo.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION")) {
                photo = Tools.selectFile(false);
                if (photo == null) {
                    iview_profile.setImage(Tools.getImage(default_photo, true));
                } else {
                    iview_profile.setImage(Tools.getImage(photo, false));
                }
            }
        });
        //------------------------------------------------------
        //Button save function declared here \/
        btn_save.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION")) {
                if (!tfnick.getText().trim().equals("") && !tfpasswd.getText().trim().equals("")) {
                    actual_user.setName(tfnick.getText());
                    actual_user.setPassword(tfpasswd.getText());
                    if (!photo.equals("")) {
                        actual_user.setPhotoURL(photo);
                        if (Tools.FileCopy(photo, "assets/profile_photo/" + actual_user.getName() + photo.substring(photo.lastIndexOf(".")))) {
                            Dialog.showInformation("", "Exito al copiar la foto", "Hemos guardado una copia de tu foto en otra carpeta!");
                            actual_user.setPhotoURL("assets/profile_photo/" + actual_user.getName() + photo.substring(photo.lastIndexOf(".")));
                        }
                    }
                    ((UserDAO) actual_user).persist();
                    if(btn_save.getText().equals("Registrarse")){
                        Dialog.showInformation("¡Éxito!","Te has registrado correctamente","Te devolvemos a la página de Login, ahora puedes iniciar sesión con tus credenciales!");
                    }else{
                        Dialog.showInformation("¡Éxito!","Tus datos se han actualizado","");
                    }
                    App.closeScene((Stage) tfnick.getScene().getWindow());
                }
            }
        });
        //--------------------------------------------------------
        //Button lock function declared here \/
        lock.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION")) {
                if (Dialog.showConfirmation("¿Desbloquear?", "¿Desea realizar cambios en su perfil?", "")) {
                    User u = new User();
                    u.setName(tfnick.getText());
                    u.setPassword(Dialog.showDialogString("Cuidado", "Para verificar que es ud, le pedimos otra vez la contraseña", "Introduzca su contraseña actual"));
                    if (u.getPassword() != null) {
                        if (UserDAO.checkUser(u)) {
                            tfpasswd.clear();
                            tfnick.setDisable(false);
                            tfpasswd.setDisable(false);
                            iview_profile.setDisable(false);
                            btn_photo.setDisable(false);
                            btnpwd.setDisable(false);
                        } else {
                            Dialog.showError("Error", "Error en la contraseña", "No coinciden las contraseñas");
                        }
                    }
                } else {
                    tfnick.setDisable(true);
                    tfpasswd.setDisable(true);
                    iview_profile.setDisable(true);
                    btn_photo.setDisable(true);
                    btnpwd.setDisable(true);
                }
            }
        });
        //---------------------------------------------------------
    }

    @FXML
    public void viewPwd() {
        Tools.showPassword(toolTip, tfpasswd);
    }

    public static void setActual_user(User u) {
        actual_user = u;
    }
}
