package com.EarthSound.controllers;

import com.EarthSound.interfaces.IBeans.IArtist;
import com.EarthSound.interfaces.IBeans.IDisc;
import com.EarthSound.interfaces.IBeans.IPlayList;
import com.EarthSound.interfaces.IBeans.ISong;
import com.EarthSound.models.DAO.SQL.ArtistDAO;
import com.EarthSound.models.DAO.SQL.DiscDAO;
import com.EarthSound.models.DAO.SQL.PlayListDAO;
import com.EarthSound.models.beans.User;
import com.EarthSound.utils.Dialog;
import com.EarthSound.utils.Tools;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class UserPlayListController {
    @FXML
    private JFXButton bnt_insert;
    @FXML
    private ComboBox<IArtist> cb_artist;
    @FXML
    private ComboBox<IDisc> cb_disc;
    @FXML
    private ComboBox<IPlayList> cb_playlist;
    @FXML
    private ComboBox<ISong> cb_song;
    @FXML
    private TableView<IPlayList> table_playlist;
    @FXML
    private TableView<ISong> table_songs;
    @FXML
    private TableColumn<IPlayList, String> tc_pl_name, tc_description;
    @FXML
    private TableColumn<ISong, String> tc_song_artist, tc_song_disc, tc_song_name;
    @FXML
    private MenuItem add_pl;

    private static User actual_user;
    private final List<IPlayList> user_playlist = new ArrayList<>();

    @FXML
    protected void initialize() {
        configureTables();
        updateTables();
        bindingComboboxes();
        addDeleteButtonToTables();
        add_pl.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION")) {
                PlayListDAO pl = new PlayListDAO();
                String name = Dialog.showDialogString("Creando nueva PlayList", "", "Introduzca el nuevo nombre de la PlayList");
                String description = Dialog.showDialogString("Añadir descripcion", "", "Introduzca una breve descripcion de la PlayList");
                if (name != null && description != null) {
                    pl.setName(name);
                    pl.setDescription(description);
                    pl.setCreator(actual_user);
                    pl.persist();
                } else {
                    Dialog.showInformation("Error", "No se creó la PlayList", "Los campos de nombre y descripción son obligatorios!");
                }
                updateTables();
            }
        });
        bnt_insert.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION")) {
                if (cb_playlist.getSelectionModel().getSelectedItem() != null && cb_song.getSelectionModel().getSelectedItem() != null) {
                    PlayListDAO pl = new PlayListDAO(cb_playlist.getSelectionModel().getSelectedItem().getId());
                    if (pl.addSong(cb_song.getSelectionModel().getSelectedItem())) {
                        Dialog.showInformation("¡Éxito!", "La canción " + cb_song.getValue().getName() + " se añadió correctamente a la PlayList", cb_playlist.getValue().getName());
                    } else {
                        Dialog.showWarning("¡!", "La canción " + cb_song.getValue().getName() + " no se pudo añadir correctamente a la PlayList", cb_playlist.getValue().getName()+"\nRevise que no esté dicha canción ya en la playlist");
                    }
                    updateTables();
                }
            }
        });
    }

    private void updateTables() {
        user_playlist.clear();
        for (int i = 0; i < actual_user.getSubPL().size(); i++) {
            if (actual_user.getSubPL().get(i).getCreator().getId() == actual_user.getId()) {
                user_playlist.add(actual_user.getSubPL().get(i));
            }
        }
        table_playlist.setItems(FXCollections.observableArrayList(user_playlist));
        cb_playlist.setItems(FXCollections.observableArrayList(user_playlist));
        table_playlist.refresh();
    }

    private void bindingComboboxes() {

        cb_artist.setItems(FXCollections.observableArrayList(ArtistDAO.listAll()));
        cb_artist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                cb_disc.setDisable(true);
                ArtistDAO a = new ArtistDAO(newValue.getId());
                cb_disc.setItems(FXCollections.observableArrayList(a.getDiscs()));
                cb_disc.setDisable(false);
            }
        });
        cb_disc.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                cb_song.setDisable(true);
                DiscDAO d = new DiscDAO(newValue.getId());
                cb_song.setItems(FXCollections.observableArrayList(d.getSongs()));
                cb_song.setDisable(false);
            }
        });
    }

    private void configureTables() {
        tc_pl_name.setCellValueFactory(eachPlayList -> new SimpleStringProperty(eachPlayList.getValue().getName()));
        tc_description.setCellValueFactory(eachPlayList -> new SimpleStringProperty(eachPlayList.getValue().getDescription()));
        tc_song_artist.setCellValueFactory(eachSong -> new SimpleStringProperty(eachSong.getValue().getDisc().getArtist().getName()));
        tc_song_disc.setCellValueFactory(eachSong -> new SimpleStringProperty(eachSong.getValue().getDisc().getName()));
        tc_song_name.setCellValueFactory(eachSong -> new SimpleStringProperty(eachSong.getValue().getName()));
        table_playlist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                PlayListDAO d = new PlayListDAO(table_playlist.getSelectionModel().getSelectedItem().getId());
                table_songs.setItems(null);
                table_songs.setItems(FXCollections.observableArrayList(d.getPlayList()));
            }
        });
        tc_pl_name.setCellFactory(param -> {
            TableCell<IPlayList, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                }
            };
            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() > 1) {
                    if (!cell.isEmpty()) {
                        PlayListDAO pl = new PlayListDAO(table_playlist.getSelectionModel().getSelectedItem().getId());
                        String newName = Dialog.showDialogString("Actualizando nombre de la lista",pl.getName(),"Introduzca un nuevo nombre");
                        if(newName!=null){
                            pl.setName(newName);
                            pl.persist();
                        }else{
                            Dialog.showInformation("No actualizaste el nombre de la lista",pl.getName(),"Seguirá siendo: "+pl.getName());
                        }
                        updateTables();
                    }
                }
            });
            return cell;
        });
        tc_description.setCellFactory(param -> {
            TableCell<IPlayList, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                }
            };
            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() > 1) {
                    if (!cell.isEmpty()) {
                        PlayListDAO pl = new PlayListDAO(table_playlist.getSelectionModel().getSelectedItem().getId());
                        String newDescription = Dialog.showDialogString("Actualizando descripción de la lista ",pl.getDescription(),"Introduzca un nuevo nombre");
                        if(newDescription!=null){
                            pl.setDescription(newDescription);
                            pl.persist();
                        }else{
                            Dialog.showInformation("No actualizaste la descripción de la lista",pl.getName(),"Seguirá siendo: "+pl.getDescription());
                        }
                        updateTables();
                    }
                }
            });
            return cell;
        });
    }

    /**
     * This method is called at the start of the view. It adds a new Column delete on the 2 tables
     */
    private void addDeleteButtonToTables() {
        //Añadimos el botón eliminar a la tabla PlayList
        TableColumn<IPlayList, Void> colBtnPL = new TableColumn<>("Eliminar");
        colBtnPL.setStyle("-fx-alignment: CENTER;");
        Callback<TableColumn<IPlayList, Void>, TableCell<IPlayList, Void>> cellFactoryPL = new Callback<>() {
            @Override
            public TableCell<IPlayList, Void> call(final TableColumn<IPlayList, Void> param) {
                return new TableCell<>() {
                    private final JFXButton btn = new JFXButton("Eliminar");
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            btn.setOnAction((ActionEvent event) -> {
                                PlayListDAO p = new PlayListDAO(getTableView().getItems().get(getIndex()).getId());
                                p.remove();
                                Dialog.showInformation("¡Éxito!","La PlayList se borró correctamente",p.getName());
                                updateTables();
                            });
                            btn.setStyle("-fx-background-color: rgb(241,65,65);");
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        colBtnPL.setCellFactory(cellFactoryPL);
        table_playlist.getColumns().add(colBtnPL);
        //------------------------------------------------------------------
        //Añadimos el botón eliminar a una nueva columna en la tabla canción
        TableColumn<ISong, Void> colBtnS = new TableColumn<>("Eliminar");
        colBtnS.setStyle("-fx-alignment: CENTER;");
        Callback<TableColumn<ISong, Void>, TableCell<ISong, Void>> cellFactoryS = new Callback<>() {
            @Override
            public TableCell<ISong, Void> call(final TableColumn<ISong, Void> param) {
                return new TableCell<>() {
                    private final JFXButton btn = new JFXButton("Eliminar");

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            btn.setOnAction((ActionEvent event) -> {
                                PlayListDAO pl = new PlayListDAO(table_playlist.getSelectionModel().getSelectedItem().getId());
                                pl.removeSong(getTableView().getItems().get(getIndex()));
                                Dialog.showInformation("¡Éxito!", "La canción se borró correctamente: ", getTableView().getItems().get(getIndex()).getName() + " de la PlayList " + pl.getName());
                                updateTables();
                            });
                            btn.setStyle("-fx-background-color: rgb(241,65,65);");
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        colBtnS.setCellFactory(cellFactoryS);
        table_songs.getColumns().add(colBtnS);
        //-------------------------------------------------------------------------------
    }

    public static void setActual_user(User u) {
        actual_user = u;
    }
}
