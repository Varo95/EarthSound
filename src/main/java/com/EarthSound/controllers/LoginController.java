package com.EarthSound.controllers;

import com.EarthSound.App;
import com.EarthSound.models.DAO.SQL.UserDAO;
import com.EarthSound.models.beans.DataConnection;
import com.EarthSound.models.beans.User;
import com.EarthSound.utils.Dialog;
import com.EarthSound.utils.Tools;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController {
    @FXML
    private MenuItem menu_admin;
    @FXML
    private ImageView imageview;
    @FXML
    private TextField tfnick;
    @FXML
    private PasswordField tfpasswd;
    @FXML
    private JFXButton btn_registry, btn_login;
    private Tooltip toolTip;
    private static final DataConnection dc = Tools.loadXML();

    @FXML
    protected void initialize() {
        imageview.setImage(Tools.default_photo_user);
        //ToolTip to show password
        toolTip = new Tooltip();
        toolTip.setShowDelay(Duration.ZERO);
        toolTip.setAutoHide(false);
        toolTip.setMinWidth(50);
        //------------------------
        menu_admin.setOnAction(event -> {
            if (tfnick.getText().equals(dc.getUser()) && tfpasswd.getText().equals(dc.getPassword())) {
                tfnick.clear();
                tfpasswd.clear();
                App.loadScene(new Stage(), "admin", "Administración", true, true);
            }
        });
        tfnick.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) onClickLogin();
        });
        tfpasswd.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) onClickLogin();
        });
        btn_login.setOnAction(event -> onClickLogin());
        btn_registry.setOnAction(event -> onClickRegister());
    }

    /**
     * This method is used in view to show password user on GUI while click on the eyebutton
     */
    @FXML
    public void viewPwd() {
        Tools.showPassword(toolTip, tfpasswd);
    }

    private void onClickLogin() {
        if (!tfnick.getText().trim().equals("") && !tfpasswd.getText().trim().equals("")) {
            User u = new User();
            u.setName(tfnick.getText().replace("\n", ""));
            u.setPassword(tfpasswd.getText().replace("\n", ""));
            if (UserDAO.checkUser(u)) {
                UserDAO p = new UserDAO(u.getId());
                MainController.setUser(p);
                App.closeScene((Stage) tfnick.getScene().getWindow());
                App.loadScene(new Stage(), "main", " EarthSound", true, true);
                tfnick.clear();
                tfpasswd.clear();
            } else {
                Dialog.showError("Login incorrecto", "Usuario y contraseña incorrectos", "Si no recuerda su usuario y contraseña, regístrese de nuevo");
            }
        }
    }

    private void onClickRegister() {
        if (!tfnick.getText().trim().equals("") && !tfpasswd.getText().trim().equals("")) {
            UserDAO u = new UserDAO();
            u.setName(tfnick.getText());
            u.setPassword(tfpasswd.getText());
            if (!UserDAO.checkUser(u)) {
                ProfileController.setActual_user(u);
                App.loadScene(new Stage(), "profile", " Crear cuenta", true, false);
                tfnick.clear();
                tfpasswd.clear();
            }
        }
    }

}
