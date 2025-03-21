package com.EarthSound.controllers;

import com.EarthSound.App;
import com.EarthSound.interfaces.IBeans.*;
import com.EarthSound.models.DAO.SQL.*;
import com.EarthSound.models.beans.Song;
import com.EarthSound.utils.Dialog;
import com.EarthSound.utils.Tools;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainController {

    @FXML
    private GridPane searchgrid;
    @FXML
    private TabPane tabpane;
    @FXML
    private JFXButton btn_search, btn_play, btn_sound, btn_prev, btn_next, btn_myPL, btn_sendComment, btn_start;
    @FXML
    private ImageView iview_profile, iview_album_playing, image_actual_artist, image_actual_disc;
    @FXML
    private ComboBox<String> combobox_profile;
    @FXML
    private Label lb_current_song, name_actual_artist, nationality_actual_artist, pub_actual_disc, name_actual_disc, genre_actual_song,
            nrep_actual_song, duration_actual_song;
    @FXML
    private JFXSlider slider_volume, slider_seek;
    @FXML
    private TextField tf_search, tf_comment;
    @FXML
    private ComboBox<String> cb_search, cb_playlists;
    @FXML
    private TableColumn<IArtist, String> column_artist;
    @FXML
    private TableColumn<IDisc, String> column_discs;
    @FXML
    private TableColumn<IPlayList, String> tc_pl_name, tc_pl_desc, tc_pl_creator;
    @FXML
    private TableColumn<ISong, String> column_songs, tc_song_name;
    @FXML
    private TableColumn<IComment, String> tc_comment_user, tc_comment_date, tc_comment_comment;
    @FXML
    private TableView<IPlayList> table_playlist;
    @FXML
    private TableView<IArtist> table_artists;
    @FXML
    private TableView<IDisc> table_discs;
    @FXML
    private TableView<ISong> table_songs;
    @FXML
    private TableView<ISong> table_pl_songs;
    @FXML
    private TableView<IComment> table_comments;
    @FXML
    private TableColumn<IPlayList, String> tc_pl_n_subs;
    //Columna para boton suscribirse y desuscribirse
    private TableColumn<IPlayList, Void> colBtnSub_unSub;

    private FilteredList<IArtist> flArtist;
    private FilteredList<IPlayList> flPlayList;

    private double actual_volume;

    private static UserDAO actual_user;

    //Objects and items to control the Media
    private Duration duration;
    private boolean autonext = false, atEndOfMedia = false;
    //boton repeat(??????
    private final boolean repeat = false;

    private static MediaPlayer mp_actual_song;

    @FXML
    protected void initialize() {
        configureTables();
        configureSearchbar();
        configurePrevAndNext();
        //Search bar, show and hide
        searchgrid.setVisible(false);
        searchgrid.setPrefHeight(0);
        BorderPane.setMargin(searchgrid, new Insets(0, 0, 0, 0));
        btn_search.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION")) {
                searchgrid.setVisible(!searchgrid.isVisible());
                if (!searchgrid.isVisible()) {
                    BorderPane.setMargin(searchgrid, new Insets(0, 0, 0, 0));
                    searchgrid.setPrefHeight(0);
                } else {
                    BorderPane.setMargin(searchgrid, new Insets(10, 10, 10, 10));
                    searchgrid.setPrefHeight(25.6);
                }
            }
        });
        //----------------
        btn_myPL.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION")) {
                UserPlayListController.setActual_user(actual_user);
                App.loadScene(new Stage(), "myplaylists", "Mis PlayLists", false, true);
                configureTables();
            }
        });
        Image i = Tools.getImage(actual_user.getPhoto(), false);
        iview_profile.setImage(Objects.requireNonNullElse(i, Tools.default_photo_user));
        iview_album_playing.setImage(Tools.getImage("assets/disc_default.png", true));
        combobox_profile.setItems(FXCollections.observableArrayList(new ArrayList<>(List.of("Ver perfil", "Cerrar Sesión", "Salir"))));
        cb_search.setItems(FXCollections.observableArrayList(new ArrayList<>(List.of("Artista", "PlayList"))));
        cb_playlists.setItems(FXCollections.observableArrayList(new ArrayList<>(List.of("Todas las Playist", "Mis PlayLists"))));
        cb_playlists.getSelectionModel().select("Todas las PlayList");
        cb_search.getSelectionModel().select("Artista");
        combobox_profile.setPromptText(actual_user.getName());
        //This is used to NOT update the combobox promptText when value changes
        combobox_profile.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(actual_user.getName());
            }
        });
        //-----------------------------Combobox Perfil-----------------------------------
        combobox_profile.getSelectionModel().selectedItemProperty().addListener(onclick -> {
            if (combobox_profile.getSelectionModel().getSelectedItem() != null) {
                if (combobox_profile.getSelectionModel().getSelectedItem().equals("Ver perfil")) {
                    ProfileController.setActual_user(actual_user);
                    App.loadScene(new Stage(), "profile", "Perfil", true, false);
                    combobox_profile.getSelectionModel().select(null);
                } else if (combobox_profile.getSelectionModel().getSelectedItem().equals("Cerrar Sesión")) {
                    stopSong();
                    App.closeScene((Stage) combobox_profile.getScene().getWindow());
                    App.loadScene(new Stage(), "login", "EarthSound", false, false);
                } else if (combobox_profile.getSelectionModel().getSelectedItem().equals("Salir")) {
                    stopSong();
                    App.closeScene((Stage) combobox_profile.getScene().getWindow());
                }
            }
        });
        //-------------------------------------------
        marqueeAnimation();
        configureSubAndUnsubButtonsColumn();
    }

    private void marqueeAnimation() {
        Label label = lb_current_song;

    }

    /**
     * This method is called when doubleclick on a Song on table, loading the media
     *
     * @param s Song to update
     */
    private void updateSongValues(Song s) {
        if (s != null) {
            mp_actual_song = Tools.getSound(s.getSongURL(), false);
            //Slider Volume Control
            slider_volume.setOrientation(Orientation.HORIZONTAL);
            slider_volume.valueProperty().addListener(invalidationListener -> {
                if (slider_volume.isValueChanging())
                    mp_actual_song.setVolume(slider_volume.getValue() / 100.0);
            });
            btn_sound.setOnAction(event -> {
                if (event.getEventType().getName().equals("ACTION")) {
                    if (mp_actual_song != null) {
                        if (mp_actual_song.getStatus().equals(Status.PLAYING)) {
                            if (slider_volume.getValue() != 0) {
                                actual_volume = slider_volume.getValue();
                                slider_volume.setValue(0);
                                mp_actual_song.setVolume(0);
                                btn_sound.setText("🔇");
                            } else {
                                slider_volume.setValue(actual_volume);
                                mp_actual_song.setVolume(actual_volume);
                                btn_sound.setText("🔊");
                            }
                        }
                    }
                }
            });
            //---------------------
            //Slider Seek Control
            slider_seek.setOrientation(Orientation.HORIZONTAL);
            slider_seek.valueProperty().addListener(invalidationListener -> {
                if (slider_seek.isValueChanging())
                    mp_actual_song.seek(duration.multiply(slider_seek.getValue() / 100.0));
            });
            //---------------------
            iview_album_playing.setImage(Tools.getImage(s.getDisc().getPhoto(), false));
            lb_current_song.setText(s.getName());
            btn_play.setOnAction(e -> {
                Status status = mp_actual_song.getStatus();
                if (status == Status.UNKNOWN || status == Status.HALTED)
                    return;
                if (status == Status.PAUSED || status == Status.READY || status == Status.STOPPED) {
                    if (atEndOfMedia) {
                        mp_actual_song.seek(mp_actual_song.getStartTime());
                        slider_seek.setValue(0);
                        atEndOfMedia = false;
                    }
                    mp_actual_song.play();
                } else {
                    mp_actual_song.pause();
                }
            });
            mp_actual_song.currentTimeProperty().addListener(ov -> updateValues());
            mp_actual_song.setOnReady(() -> {
                duration = mp_actual_song.getMedia().getDuration();
                updateValues();
                if (autonext) {
                    btn_play.fire();
                    autonext = false;
                }
            });
            mp_actual_song.setOnPlaying(() -> btn_play.setText("⏸"));
            mp_actual_song.setOnPaused(() -> btn_play.setText("▶"));
            mp_actual_song.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
            mp_actual_song.setOnEndOfMedia(() -> {
                if (!repeat) {
                    btn_play.setText("▶");
                    atEndOfMedia = true;
                }
                SongDAO count = new SongDAO(s.getId());
                count.setNPlays(s.getNPlays() + 1);
                mp_actual_song.seek(mp_actual_song.getStartTime());
                slider_seek.setValue(0);
                autonext = true;
                btn_next.fire();
            });
        }
    }

    /**
     * This method update the values of javafx elements that are assigned to sounds
     */
    private void updateValues() {
        if (lb_current_song != null && slider_seek != null && slider_volume != null) {
            Platform.runLater(() -> {
                slider_seek.setDisable(duration.isUnknown());
                if (!slider_seek.isDisabled() && duration.greaterThan(Duration.ZERO) && !slider_seek.isValueChanging())
                    slider_seek.setValue(mp_actual_song.getCurrentTime().divide(duration).toMillis() * 100.0);
                if (!slider_volume.isValueChanging())
                    slider_volume.setValue((int) Math.round(mp_actual_song.getVolume() * 100));
                slider_seek.setValueFactory(param -> Bindings.createStringBinding(() -> (Tools.formatTime(mp_actual_song.getCurrentTime(), duration)), slider_seek.valueProperty()));
            });
        }
    }

    /**
     * This method is used to set the actual user of this window
     *
     * @param u user to be set
     */
    public static void setUser(UserDAO u) {
        actual_user = u;
    }

    private void configureTables() {
        column_artist.setCellValueFactory(eachArtist -> new SimpleStringProperty(eachArtist.getValue().getName()));
        column_discs.setCellValueFactory(eachDisc -> new SimpleStringProperty(eachDisc.getValue().getName()));
        column_songs.setCellValueFactory(eachSong -> new SimpleStringProperty(eachSong.getValue().getName()));
        tc_song_name.setCellValueFactory(eachSong -> new SimpleStringProperty(eachSong.getValue().getName()));
        tc_pl_name.setCellValueFactory(eachPlayList -> new SimpleStringProperty(eachPlayList.getValue().getName()));
        tc_pl_desc.setCellValueFactory(eachPlayList -> new SimpleStringProperty(eachPlayList.getValue().getDescription()));
        tc_pl_creator.setCellValueFactory(eachPlayList -> new SimpleStringProperty(eachPlayList.getValue().getCreator().getName()));
        tc_comment_user.setCellValueFactory(eachComment -> new SimpleStringProperty(eachComment.getValue().getUser().getName()));
        tc_comment_date.setCellValueFactory(eachComment -> new SimpleStringProperty(eachComment.getValue().getTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));
        tc_comment_comment.setCellValueFactory(eachComment -> new SimpleStringProperty(eachComment.getValue().getComment()));
        tc_pl_n_subs.setCellValueFactory(eachPlayList -> new SimpleStringProperty(eachPlayList.getValue().getNSubs() + ""));
        flArtist = new FilteredList<>(FXCollections.observableArrayList(ArtistDAO.listAll()), p -> true);
        flPlayList = new FilteredList<>(FXCollections.observableArrayList(PlayListDAO.listAll()), p -> true);
        table_artists.setItems(flArtist);
        table_playlist.setItems(flPlayList);
        table_playlist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                PlayListDAO d = new PlayListDAO(table_playlist.getSelectionModel().getSelectedItem().getId());
                table_pl_songs.setItems(null);
                table_pl_songs.setItems(FXCollections.observableArrayList(d.getPlayList()));
                table_comments.setItems(null);
                btn_sendComment.setDisable(true);
                table_comments.setItems(FXCollections.observableArrayList(d.getComments()));
                btn_sendComment.setDisable(false);
            }
        });
        table_pl_songs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                SongDAO s = new SongDAO(table_pl_songs.getSelectionModel().getSelectedItem().getId());
                updateSongLabels(s);
                image_actual_artist.setImage(Tools.getImage(s.getDisc().getArtist().getPhoto(), false));
                name_actual_artist.setText("Nombre: " + s.getDisc().getArtist().getName());
                nationality_actual_artist.setText("Nacionalidad: " + s.getDisc().getArtist().getNationality());
                image_actual_disc.setImage(Tools.getImage(s.getDisc().getPhoto(), false));
                pub_actual_disc.setText("Año: " + s.getDisc().getPublicationDate().getYear());
                name_actual_disc.setText("Nombre: " + s.getDisc().getName());
                updateSongLabels(s);
            }
        });
        table_pl_songs.setRowFactory(tv -> {
            TableRow<ISong> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                SongDAO s = new SongDAO(row.getItem().getId());
                updateSongLabels(s);
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    stopSong();
                    updateSongValues(s);
                    mp_actual_song.play();
                }
            });
            return row;
        });
        column_artist.setCellFactory(param -> {
            TableCell<IArtist, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                }
            };
            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty()) {
                    ArtistDAO a = new ArtistDAO(table_artists.getSelectionModel().getSelectedItem().getId());
                    image_actual_artist.setImage(Tools.getImage(a.getPhoto(), false));
                    name_actual_artist.setText("Nombre: " + a.getName());
                    nationality_actual_artist.setText("Nacionalidad: " + a.getNationality());
                    table_discs.setItems(FXCollections.observableArrayList(a.getDiscs()));
                    table_songs.setItems(null);
                    clearLabelsDisc();
                    clearLabelsSong();
                }
            });
            return cell;
        });
        column_discs.setCellFactory(param -> {
            TableCell<IDisc, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                }
            };
            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty()) {
                    DiscDAO d = new DiscDAO(table_discs.getSelectionModel().getSelectedItem().getId());
                    image_actual_disc.setImage(Tools.getImage(d.getPhoto(), false));
                    pub_actual_disc.setText("Año: " + d.getPublicationDate().getYear());
                    image_actual_artist.setImage(Tools.getImage(d.getArtist().getPhoto(), false));
                    name_actual_artist.setText("Nombre: " + d.getArtist().getName());
                    nationality_actual_artist.setText("Nacionalidad: " + d.getArtist().getNationality());
                    table_songs.setItems(FXCollections.observableArrayList(d.getSongs()));
                    name_actual_disc.setText("Nombre: " + d.getName() + "\nNumero de reproducciones: " + d.getnDiscPlay());
                    clearLabelsSong();
                }
            });
            return cell;
        });
        column_songs.setCellFactory(param -> {
            TableCell<ISong, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                }
            };
            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() > 1) {
                    if (!cell.isEmpty()) {
                        SongDAO s = new SongDAO(table_songs.getSelectionModel().getSelectedItem().getId());
                        stopSong();
                        updateSongValues(s);
                        mp_actual_song.play();
                        updateSongLabels(s);
                    }
                }
            });
            return cell;
        });
        cb_playlists.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equals("Mis PlayLists")) {
                    table_playlist.setItems(null);
                    table_playlist.setItems(FXCollections.observableArrayList(actual_user.getSubPL()));
                    table_comments.setItems(null);
                    table_pl_songs.setItems(null);
                    table_playlist.refresh();
                    Dialog.showInformation("Pestaña PlayList actualizada", "Ahora te mostraré tus playlists", "¡Encontrarás las que estás suscrito y las que has creado!");
                } else if (newValue.equals("Todas las PlayList")) {
                    table_playlist.setItems(null);
                    table_playlist.setItems(FXCollections.observableArrayList(PlayListDAO.listAll()));
                    table_comments.setItems(null);
                    table_pl_songs.setItems(null);
                    table_playlist.refresh();
                    Dialog.showInformation("Pestaña PlayList actualizada", "Ahora te mostraré todas las playlists", "¡Puedes suscribirte a las que te gusten!");
                }
            }
        });
        btn_start.setOnAction(event -> Tools.unKnow());
        table_playlist.refresh();
        table_artists.refresh();
        //Configure Send Button and TextField to put comments while enter or send is pressed
        btn_sendComment.setOnAction(event -> onClickSendComment());
        tf_comment.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) onClickSendComment();
        });
        //---------------------------------------------------------------------------------
    }

    /**
     * This method is used to create subscribe and unsubscribe buttons on the tableview of playlists
     */
    private void configureSubAndUnsubButtonsColumn() {
        colBtnSub_unSub = new TableColumn<>("Sub/UnSub");
        colBtnSub_unSub.setStyle("-fx-alignment: CENTER;");
        Callback<TableColumn<IPlayList, Void>, TableCell<IPlayList, Void>> cellFactoryS = new Callback<>() {
            @Override
            public TableCell<IPlayList, Void> call(TableColumn<IPlayList, Void> param) {
                return new TableCell<>() {
                    private final JFXButton btn = new JFXButton();

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            if (getTableView().getItems().get(getIndex()).getCreator().getId() == actual_user.getId()) {
                                setGraphic(null);
                                setText("¡Eres el creador!");
                            } else {
                                PlayListDAO pl = new PlayListDAO(getTableView().getItems().get(getIndex()).getId());
                                List<IUser> sub_list = pl.getSubs();
                                if (sub_list != null && sub_list.size() != 0) {
                                    if (sub_list.contains(actual_user)) {
                                        btn.setText("❎");
                                        btn.setStyle("-fx-background-color: rgb(241,65,65);");
                                        setGraphic(btn);
                                        btn.setOnAction((ActionEvent event) -> {
                                            if (actual_user.unSubscribe(pl))
                                                Dialog.showInformation("¡Éxito!", "Has cancelado tu suscripcion", "Dejaste de seguir la lista: " + pl.getName());
                                            table_playlist.setItems(FXCollections.observableArrayList(PlayListDAO.listAll()));
                                            table_playlist.refresh();
                                        });
                                    }
                                } else {
                                    btn.setText("✅");
                                    btn.setStyle("-fx-background-color: rgb(132,227,70);");
                                    setGraphic(btn);
                                    btn.setOnAction((ActionEvent event) -> {
                                        if (actual_user.subscribe(pl))
                                            Dialog.showInformation("¡Éxito!", "Te has suscrito", "Ahora sigues la lista: " + pl.getName());
                                        table_playlist.setItems(FXCollections.observableArrayList(PlayListDAO.listAll()));
                                        table_playlist.refresh();
                                    });
                                }
                            }
                        }
                    }
                };
            }
        };
        colBtnSub_unSub.setCellFactory(cellFactoryS);
        table_playlist.getColumns().add(colBtnSub_unSub);
    }

    /**
     * This method is called when we need to put a comment on a PlayList
     */
    private void onClickSendComment() {
        if (!tf_comment.getText().trim().equals("")) {
            if (table_playlist.getSelectionModel().getSelectedItem() != null && table_comments.getItems() != null) {
                CommentDAO c = new CommentDAO();
                c.setUser(actual_user);
                c.setTime(LocalDateTime.now());
                c.setComment(tf_comment.getText().replace("\n", ""));
                c.setPlayList(table_playlist.getSelectionModel().getSelectedItem());
                c.persist();
                tf_comment.clear();
                table_comments.getItems().add(c);
                table_comments.refresh();
            }
        }
    }

    /**
     * With this method is used to configure search with the combobox value
     */
    private void configureSearchbar() {
        tf_search.setPromptText("Busca aquí!");
        tf_search.textProperty().addListener((obs, oldValue, newValue) -> {
            switch (cb_search.getValue()) {
                case "Artista" -> {
                    flArtist.setPredicate(a -> a.getName().toLowerCase().contains(newValue.toLowerCase().trim()));
                    tabpane.getSelectionModel().select(0);
                }
                case "PlayList" -> {
                    flPlayList.setPredicate(pl -> pl.getName().toLowerCase().contains(newValue.toLowerCase().trim()));
                    tabpane.getSelectionModel().select(1);
                }
            }
        });
        cb_search.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) tf_search.setText("");
        });
    }

    /**
     * This method is used to stop song if playing
     */
    private void stopSong() {
        if (mp_actual_song != null) {
            mp_actual_song.stop();
        }
    }

    /**
     * This method is used to configure next and prev buttons
     */
    private void configurePrevAndNext() {
        btn_prev.setOnAction(event -> {
            stopSong();
            if (tabpane.getSelectionModel().isSelected(0)) {
                if (table_songs.getItems() != null && table_songs.getSelectionModel().getSelectedItem() != null) {
                    table_songs.getSelectionModel().selectPrevious();
                    SongDAO s = new SongDAO(table_songs.getSelectionModel().getSelectedItem().getId());
                    updateSongValues(s);
                    mp_actual_song.play();
                    updateSongLabels(s);
                }
            } else if (tabpane.getSelectionModel().isSelected(1)) {
                if (table_pl_songs.getItems() != null && table_pl_songs.getSelectionModel().getSelectedItem() != null) {
                    table_pl_songs.getSelectionModel().selectPrevious();
                    SongDAO s = new SongDAO(table_pl_songs.getSelectionModel().getSelectedItem().getId());
                    updateSongValues(s);
                    mp_actual_song.play();
                    updateSongLabels(s);
                }
            }

        });
        btn_next.setOnAction(event -> {
            stopSong();
            if (tabpane.getSelectionModel().isSelected(0)) {
                if (table_songs.getItems() != null && table_songs.getSelectionModel().getSelectedItem() != null) {
                    table_songs.getSelectionModel().selectNext();
                    SongDAO s = new SongDAO(table_songs.getSelectionModel().getSelectedItem().getId());
                    updateSongValues(s);
                    mp_actual_song.play();
                    updateSongLabels(s);
                }
            } else if (tabpane.getSelectionModel().isSelected(1)) {
                if (table_pl_songs.getItems() != null && table_pl_songs.getSelectionModel().getSelectedItem() != null) {
                    table_pl_songs.getSelectionModel().selectNext();
                    SongDAO s = new SongDAO(table_pl_songs.getSelectionModel().getSelectedItem().getId());
                    updateSongValues(s);
                    mp_actual_song.play();
                    updateSongLabels(s);
                }
            }
        });
    }

    /**
     * This method is used to update Labels of the Song TitledPane
     *
     * @param s song to update the labels
     */
    private void updateSongLabels(SongDAO s) {
        if (s != null) {
            duration_actual_song.setText("Duración : " + Math.round(s.getDuration() / 3600) + "h:" + Math.round((s.getDuration() % 3600) / 60) + "m:" + Math.round(s.getDuration() % 60) + "s");
            nrep_actual_song.setText("Número reproducciones: " + s.getNPlays());
            genre_actual_song.setText("Género: " + s.getGenre().getName());
        }
    }

    /**
     * This method is used to clear Labels on Disc TitledPane
     */
    private void clearLabelsDisc() {
        pub_actual_disc.setText("");
        name_actual_disc.setText("");
        image_actual_disc.setImage(Tools.default_photo_disc);
    }

    /**
     * This method is used to clear Labels on Song TitledPane
     */
    private void clearLabelsSong() {
        duration_actual_song.setText("");
        nrep_actual_song.setText("");
        genre_actual_song.setText("");
    }

}
