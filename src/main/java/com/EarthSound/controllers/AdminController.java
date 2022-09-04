package com.EarthSound.controllers;

import com.EarthSound.interfaces.IBeans.IArtist;
import com.EarthSound.interfaces.IBeans.IDisc;
import com.EarthSound.interfaces.IBeans.IGenre;
import com.EarthSound.interfaces.IBeans.ISong;
import com.EarthSound.models.DAO.SQL.ArtistDAO;
import com.EarthSound.models.DAO.SQL.DiscDAO;
import com.EarthSound.models.DAO.SQL.GenreDAO;
import com.EarthSound.models.DAO.SQL.SongDAO;
import com.EarthSound.utils.Dialog;
import com.EarthSound.utils.Tools;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


public class AdminController {
    //C------------------Pestaña CREAR----------------------C
    @FXML
    private ImageView image_c_artist, image_c_disc;
    @FXML
    private TextField tf_c_artist_name, tf_c_artist_nationality, tf_c_artist_photoURL, tf_c_disc_name, tf_c_disc_photoURL,
            tf_c_song_duration, tf_c_song_name, tf_c_song_songURL, tf_c_genre_name;
    @FXML
    private JFXButton btn_c_artist_examine, btn_c_artist_save, btn_c_disc_examine, btn_c_disc_save, btn_c_song_examine,
            btn_c_song_save, btn_c_song_play, btn_c_genre_save;

    @FXML
    private DatePicker dp_c_disc_pubdate;
    @FXML
    private ComboBox<IArtist> cb_c_disc_sl_artist, cb_c_song_sl_artist;
    @FXML
    private ComboBox<IDisc> cb_c_song_sl_disc;
    @FXML
    private ComboBox<IGenre> cb_c_song_sl_genre;


    //U------------------Pestaña ACTUALIZAR-----------------U
    @FXML
    private ComboBox<IArtist> cb_u_artist_sl_artist, cb_u_disc_sl_artist, cb_u_disc_sl_artist_in, cb_u_song_sl_artist, cb_u_song_sl_artist_in;
    @FXML
    private ImageView image_u_artist, image_u_disc;
    @FXML
    private TextField tf_u_artist_name, tf_u_artist_nationality, tf_u_artist_photoURL, tf_u_disc_name, tf_u_disc_photoURL,
            tf_u_song_duration, tf_u_song_name, tf_u_song_songURL, tf_u_genre_name;
    @FXML
    private JFXButton btn_u_artist_examine, btn_u_artist_update, btn_u_disc_examine, btn_u_disc_update, btn_u_song_examine,
            btn_u_song_update, btn_u_song_play, btn_u_genre_update;
    @FXML
    private ComboBox<IDisc> cb_u_disc_sl_disc, cb_u_song_sl_disc, cb_u_song_sl_disc_in;
    @FXML
    private DatePicker dp_u_disc_pubdate;
    @FXML
    private ComboBox<IGenre> cb_u_song_sl_genre, cb_u_genre_sl_genre;
    @FXML
    private ComboBox<ISong> cb_u_song_sl_song;

    //D------------------Pestaña ELIMINAR-------------------D
    @FXML
    private ComboBox<IArtist> cb_r_artist_sl_artist, cb_r_disc_sl_artist, cb_r_song_sl_artist;
    @FXML
    private TextField tf_r_artist_name, tf_r_artist_nationality, tf_r_artist_photoURL, tf_r_disc_name, tf_r_disc_photoURL,
            tf_r_song_duration, tf_r_song_name, tf_r_song_songURL, tf_r_genre_name;
    @FXML
    private ImageView image_r_artist, image_r_disc;
    @FXML
    private JFXButton btn_r_artist_examine, btn_r_artist_remove, btn_r_disc_examine, btn_r_disc_remove, btn_r_song_examine,
            btn_r_song_remove, btn_r_song_play, btn_r_genre_remove;
    @FXML
    private ComboBox<IDisc> cb_r_disc_sl_disc, cb_r_song_sl_disc, cb_r_song_sl_disc_in;
    @FXML
    private DatePicker dp_r_disc_pubdate;
    @FXML
    private ComboBox<ISong> cb_r_song_sl_song;
    @FXML
    private ComboBox<IGenre> cb_r_song_sl_genre, cb_r_genre_sl_genre;

    //------------------------------------------------------------------------
    private static ArtistDAO selected_artist_update, selected_artist_remove;
    private static DiscDAO selected_disc_update, selected_disc_remove;
    private static SongDAO selected_song_update, selected_song_remove;
    private static GenreDAO selected_genre_update, selected_genre_remove;
    //------------------------------ComboBoxLists------------------------------
    private List<ComboBox<IArtist>> artist_comboboxes;
    private List<ComboBox<IDisc>> disc_comboboxes;
    private List<ComboBox<ISong>> song_comboboxes;
    private List<ComboBox<IGenre>> genre_comboboxes;

    @FXML
    protected void initialize() {
        //Seteamos los valores iniciales
        image_c_artist.setImage(Tools.default_photo_user);
        image_u_artist.setImage(Tools.default_photo_user);
        image_r_artist.setImage(Tools.default_photo_user);
        image_c_disc.setImage(Tools.default_photo_disc);
        image_u_disc.setImage(Tools.default_photo_disc);
        image_r_disc.setImage(Tools.default_photo_disc);
        dp_c_disc_pubdate.setValue(LocalDate.now());
        //Ponemos los convertidores de los combobox de los artistas
        artist_comboboxes = new ArrayList<>(List.of(cb_c_disc_sl_artist, cb_c_song_sl_artist, cb_u_artist_sl_artist
                , cb_u_disc_sl_artist, cb_u_disc_sl_artist_in, cb_u_song_sl_artist, cb_u_song_sl_artist_in, cb_r_artist_sl_artist, cb_r_disc_sl_artist, cb_r_song_sl_artist));
        Tools.setComboBoxesforArtist(artist_comboboxes);
        //---------------------------------------------------------
        //Ponemos los convertidores de los combobox de los discos
        disc_comboboxes = new ArrayList<>(List.of(cb_c_song_sl_disc, cb_u_disc_sl_disc, cb_u_song_sl_disc,
                cb_u_song_sl_disc_in, cb_r_disc_sl_disc, cb_r_song_sl_disc, cb_r_song_sl_disc_in));
        Tools.setComboBoxesforDisc(disc_comboboxes);
        //---------------------------------------------------------
        //Ponemos los convertidores de los combobox de las canciones
        song_comboboxes = new ArrayList<>(List.of(cb_u_song_sl_song, cb_r_song_sl_song));
        Tools.setComboBoxesforSong(song_comboboxes);
        //-----------------------------------------------------------
        //Ponemos los convertidores de los combobox de los generos
        genre_comboboxes = new ArrayList<>(List.of(cb_c_song_sl_genre, cb_u_song_sl_genre, cb_u_genre_sl_genre,
                cb_r_song_sl_genre, cb_r_genre_sl_genre));
        Tools.setComboBoxesforGenre(genre_comboboxes);
        //-----------------------------------------------------------
        Tools.onlyDoubleValue(tf_c_song_duration);
        Tools.onlyDoubleValue(tf_u_song_duration);
        Tools.onlyDoubleValue(tf_r_song_duration);
        //Reactive tecnology, this is listening on text changes and try to set an image on imageview
        addListenersForImageViews(tf_c_artist_photoURL, image_c_artist, Tools.default_photo_user, tf_u_artist_photoURL, image_u_artist);
        addListenersForImageViews(tf_c_disc_photoURL, image_c_disc, Tools.default_photo_disc, tf_u_disc_photoURL, image_u_disc);
        //-------------------------------------------------------------------------------------------
        List<TextField> c_artist_fields = new ArrayList<>(List.of(tf_c_artist_name, tf_c_artist_nationality, tf_c_artist_photoURL));
        List<TextField> c_disc_fields = new ArrayList<>(List.of(tf_c_disc_name, tf_c_disc_photoURL, dp_c_disc_pubdate.getEditor()));
        List<TextField> c_song_fields = new ArrayList<>(List.of(tf_c_song_name, tf_c_song_duration, tf_c_song_songURL));
        List<TextField> c_genre_fields = new ArrayList<>(List.of(tf_c_genre_name));
        List<TextField> u_artist_fields = new ArrayList<>(List.of(tf_u_artist_name, tf_u_artist_nationality, tf_u_artist_photoURL));
        List<TextField> u_disc_fields = new ArrayList<>(List.of(tf_u_disc_name, tf_u_disc_photoURL, dp_u_disc_pubdate.getEditor()));
        List<TextField> u_song_fields = new ArrayList<>(List.of(tf_u_song_name, tf_u_song_duration, tf_u_song_songURL));
        List<TextField> u_genre_fields = new ArrayList<>(List.of(tf_u_genre_name));
        setExamineButtons(btn_c_artist_examine, tf_c_artist_photoURL, btn_c_disc_examine, tf_c_disc_photoURL);
        btn_c_song_examine.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION")) {
                String file = Tools.selectFile(true);
                tf_c_song_songURL.setText((file == null) ? "" : file);
                tf_c_song_name.setText(Tools.getNameFileOfSound(file));
                Tools.refreshValuesOfSong(file, tf_c_song_duration, btn_c_song_play);
                tf_c_song_songURL.textProperty().addListener((observable, oldValue, newValue) -> tf_c_song_songURL.setText(newValue));
            }
        });
        setExamineButtons(btn_u_artist_examine, tf_u_artist_photoURL, btn_u_disc_examine, tf_u_disc_photoURL);
        btn_u_song_examine.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION")) {
                String file = Tools.selectFile(true);
                tf_u_song_songURL.setText((file == null) ? "" : file);
                tf_u_song_name.setText(Tools.getNameFileOfSound(file));
                Tools.refreshValuesOfSong(file, tf_u_song_duration, btn_u_song_play);
                tf_u_song_songURL.textProperty().addListener((observable, oldValue, newValue) -> tf_u_song_songURL.setText(newValue));
            }
        });
        btn_c_artist_save.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION")) {
                if (Tools.checkTexts(c_artist_fields)) {
                    persistArtist(new ArtistDAO(), tf_c_artist_name, tf_c_artist_nationality, tf_c_artist_photoURL);
                } else {
                    Dialog.showWarning("Error", "Te has dejado un campo sin rellenar!", "Revisa que todos los campos están al menos rellenados");
                }
            }
        });
        btn_u_artist_update.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION")) {
                if (Tools.checkTexts(u_artist_fields)) {
                    persistArtist(selected_artist_update, tf_u_artist_name, tf_u_artist_nationality, tf_u_artist_photoURL);
                } else {
                    Dialog.showWarning("Error", "Te has dejado un campo sin rellenar!", "Revisa que todos los campos están al menos rellenados");
                }
            }
        });
        btn_c_disc_save.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION")) {
                if (Tools.checkTexts(c_disc_fields)) {
                    persistDisc(new DiscDAO(), tf_c_disc_name, tf_c_disc_photoURL, dp_c_disc_pubdate, cb_c_disc_sl_artist);
                } else {
                    Dialog.showWarning("Error", "Te has dejado un campo sin rellenar!", "Revisa que todos los campos están al menos rellenados");
                }
            }
        });
        btn_u_disc_update.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION")) {
                if (Tools.checkTexts(u_disc_fields)) {
                    persistDisc(selected_disc_update, tf_u_disc_name, tf_u_disc_photoURL, dp_u_disc_pubdate, cb_u_disc_sl_artist);
                } else {
                    Dialog.showWarning("Error", "Te has dejado un campo sin rellenar!", "Revisa que todos los campos están al menos rellenados");
                }
            }
        });
        btn_c_song_save.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION")) {
                if (Tools.checkTexts(c_song_fields)) {
                    persistSong(new SongDAO(), tf_c_song_name, tf_c_song_duration, tf_c_song_songURL, cb_c_song_sl_artist, cb_c_song_sl_disc, cb_c_song_sl_genre);
                } else {
                    Dialog.showWarning("Error", "Te has dejado un campo sin rellenar!", "Revisa que todos los campos están al menos rellenados");
                }
            }
        });
        btn_u_song_update.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION")) {
                if (Tools.checkTexts(u_song_fields)) {
                    persistSong(selected_song_update, tf_u_song_name, tf_u_song_duration, tf_u_song_songURL, cb_u_song_sl_artist, cb_u_song_sl_disc, cb_u_song_sl_genre);
                } else {
                    Dialog.showWarning("Error", "Te has dejado un campo sin rellenar!", "Revisa que todos los campos están al menos rellenados");
                }
            }
        });
        btn_c_genre_save.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION")) {
                if (Tools.checkTexts(c_genre_fields)) {
                    persistGenre(new GenreDAO(), tf_c_genre_name);
                } else {
                    Dialog.showWarning("Error", "Te has dejado un campo sin rellenar!", "Revisa que todos los campos están al menos rellenados");
                }
            }
        });
        btn_u_genre_update.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION")) {
                if (Tools.checkTexts(u_genre_fields)) {
                    persistGenre(selected_genre_update, tf_u_genre_name);
                } else {
                    Dialog.showWarning("Error", "Te has dejado un campo sin rellenar!", "Revisa que todos los campos están al menos rellenados");
                }
            }
        });
        btn_r_artist_remove.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION"))
                if (selected_artist_remove != null) {
                    selected_artist_remove.remove();
                    if (!selected_artist_remove.getPhotoURL().startsWith("http"))
                        if (Tools.FileDelete(selected_artist_remove.getPhotoURL())) {
                            Dialog.showInformation("Exito!", "Hemos eliminado el archivo asociado", "Se consiguió eliminar la foto asociada al artista de la ruta relativa");
                        } else {
                            Dialog.showError("Error", "No se pudo eliminar", "Hubo un problema al eliminar la foto de la ruta relativa");
                        }
                    Tools.setComboBoxesforArtist(artist_comboboxes);
                    Dialog.showInformation("Éxito!", "Se borró correctamente", "El artista se pudo borrar correctamente");
                    clear_r_artist();
                }
        });
        btn_r_disc_remove.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION"))
                if (selected_disc_remove != null) {
                    selected_disc_remove.remove();
                    if (!selected_disc_remove.getPhotoURL().startsWith("http"))
                        if (Tools.FileDelete(selected_disc_remove.getPhotoURL())) {
                            Dialog.showInformation("Exito!", "Hemos eliminado el archivo asociado", "Se consiguió eliminar la foto asociada al disco de la ruta relativa");
                        } else {
                            Dialog.showError("Error", "No se pudo eliminar", "Hubo un problema al eliminar la foto de la ruta relativa");
                        }
                    Dialog.showInformation("Éxito!", "Se borró correctamente", "El disco se pudo borrar correctamente");
                    clear_r_disc();
                }
        });
        btn_r_song_remove.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION"))
                if (selected_song_remove != null) {
                    selected_song_remove.remove();
                    if (!selected_song_remove.getSongURL().startsWith("http"))
                        if (Tools.FileDelete(selected_song_remove.getSongURL())) {
                            Dialog.showInformation("Exito!", "Hemos eliminado el archivo asociado", "Se consiguió eliminar la canción asociada a la musica de la ruta relativa");
                        } else {
                            Dialog.showError("Error", "No se pudo eliminar", "Hubo un problema al eliminar la foto de la ruta relativa");
                        }
                    Dialog.showInformation("Éxito!", "Se borró correctamente", "La canción se pudo borrar correctamente");
                    clear_r_song();
                }
        });
        btn_r_genre_remove.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION"))
                if (selected_genre_remove != null) {
                    selected_genre_remove.remove();
                    Dialog.showInformation("Éxito!", "Se borró correctamente", "El género se pudo borrar correctamente");
                    clear_r_genre();
                }
        });
        addListenersOnComboBoxes();

    }

    private void clear_r_artist() {
        cb_r_artist_sl_artist.getSelectionModel().select(null);
        image_r_artist.setImage(Tools.default_photo_user);
        tf_r_artist_name.clear();
        tf_r_artist_nationality.clear();
        tf_r_artist_photoURL.clear();
    }

    private void clear_r_disc() {
        cb_r_disc_sl_artist.getSelectionModel().select(null);
        image_r_disc.setImage(Tools.default_photo_disc);
        cb_r_disc_sl_disc.getSelectionModel().select(null);
        tf_r_disc_name.clear();
        tf_r_disc_photoURL.clear();
        dp_r_disc_pubdate.setValue(null);
    }

    private void clear_r_genre() {
        cb_r_genre_sl_genre.getSelectionModel().select(null);
        tf_r_genre_name.clear();
    }

    private void clear_r_song() {
        cb_r_song_sl_artist.getSelectionModel().select(null);
        cb_r_song_sl_disc.getSelectionModel().select(null);
        cb_r_song_sl_song.getSelectionModel().select(null);
        tf_r_song_songURL.clear();
        tf_r_song_duration.clear();
        tf_r_song_name.clear();
    }

    /**
     * This method is to configure examine buttons for both tabs (create and update)
     *
     * @param btn_artist_examine examine button of artist tab
     * @param tf_artist_photoURL textfield photoURL of artist tab
     * @param btn_disc_examine   examine button of disc tab
     * @param tf_disc_photoURL   textfield photoURL of disc tab
     */
    private void setExamineButtons(JFXButton btn_artist_examine, TextField tf_artist_photoURL, JFXButton btn_disc_examine, TextField tf_disc_photoURL) {
        btn_artist_examine.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION")) {
                String file = Tools.selectFile(false);
                tf_artist_photoURL.setText((file == null) ? "" : file);
            }
        });
        btn_disc_examine.setOnAction(event -> {
            if (event.getEventType().getName().equals("ACTION")) {
                String file = Tools.selectFile(false);
                tf_disc_photoURL.setText((file == null) ? "" : file);
            }
        });
    }

    /**
     * This method is used to persist an artist with the params below
     *
     * @param artist                artist to persist
     * @param tf_artist_name        artist name textfield of the tab
     * @param tf_artist_nationality artist nationality textfield of the tab
     * @param tf_artist_photoURL    artist photoURL textfield of the tab
     */
    private void persistArtist(ArtistDAO artist, TextField tf_artist_name, TextField tf_artist_nationality, TextField tf_artist_photoURL) {
        if (!tf_artist_name.getText().trim().equals("") && !tf_artist_nationality.getText().trim().equals("") && !tf_artist_photoURL.getText().trim().equals("")) {
            artist.setName(tf_artist_name.getText());
            artist.setNationality(tf_artist_nationality.getText());
            artist.setPhoto(tf_artist_photoURL.getText());
            if (!tf_artist_photoURL.getText().startsWith("http")) {
                if (Tools.FileCopy(tf_artist_photoURL.getText(), "assets/artists/" + artist.getName() + tf_artist_photoURL.getText().substring(tf_artist_photoURL.getText().lastIndexOf(".")))) {
                    Dialog.showInformation("", "Exito al copiar la foto", "Hemos guardado una copia de tu foto en otra carpeta!");
                    artist.setPhoto("assets/artists/" + artist.getName() + tf_artist_photoURL.getText().substring(tf_artist_photoURL.getText().lastIndexOf(".")));
                }
            } else {
                Dialog.showInformation("¡Cuidado!", "Has metido una URL de internet", "No se hará una copia en local de dicha imagen, ¡Cuidado no te la borren del servidor!");
            }
            artist.persist();
            Tools.setComboBoxesforArtist(artist_comboboxes);
            Dialog.showInformation("", "Se creó correctamente", "El artista se ha guardado correctamente en la base de datos");
            tf_artist_name.clear();
            tf_artist_nationality.clear();
            tf_artist_photoURL.clear();
        } else {
            Dialog.showWarning("Error", "Los campos de texto son incorrectos", "Asegurese de haber metido bien el texto en los campos de nombre y/o foto y/o nacionalidad");
        }
    }

    /**
     * This method is used to persis a disc with the params below
     *
     * @param disc              disc to persist
     * @param tf_disc_name      disc name textfield of the tab
     * @param tf_disc_photoURL  disc photoURL textfield of the tab
     * @param dp_disc_pubdate   disc datepicker of the tab
     * @param cb_disc_sl_artist disc combobox(selected artist) of the tab
     */
    private void persistDisc(DiscDAO disc, TextField tf_disc_name, TextField tf_disc_photoURL, DatePicker dp_disc_pubdate, ComboBox<IArtist> cb_disc_sl_artist) {
        if (!tf_disc_name.getText().trim().equals("") && !tf_disc_photoURL.getText().trim().equals("")) {
            disc.setName(tf_disc_name.getText());
            disc.setPhotoURL(tf_disc_photoURL.getText());
            if (!tf_disc_photoURL.getText().startsWith("http")) {
                if (Tools.FileCopy(tf_disc_photoURL.getText(), "assets/discs/" + disc.getName() + tf_disc_photoURL.getText().substring(tf_disc_photoURL.getText().lastIndexOf(".")))) {
                    Dialog.showInformation("", "Exito al copiar la foto", "Hemos guardado una copia de tu foto en otra carpeta!");
                    disc.setPhotoURL("assets/discs/" + disc.getName() + tf_disc_photoURL.getText().substring(tf_disc_photoURL.getText().lastIndexOf(".")));
                } else {
                    Dialog.showInformation("¡Cuidado!", "Has metido una URL de internet", "No se hará una copia en local de dicha imagen, ¡Cuidado no te la borren del servidor!");
                }
            }
            disc.setPublicationDate(dp_disc_pubdate.getValue());
            if (cb_disc_sl_artist.getValue() != null) {
                disc.setArtist(cb_disc_sl_artist.getValue());
                disc.persist();
                Tools.setComboBoxesforDisc(disc_comboboxes);
                Dialog.showInformation("", "Se creó/actualizó correctamente", "El disco se ha guardado correctamente en la base de datos");
                tf_disc_name.clear();
                tf_disc_photoURL.clear();
                dp_disc_pubdate.setValue(LocalDate.now());
            } else {
                Dialog.showInformation("Error", "No has seleccionado ningún artista", "Asegurate de seleccionarlo antes de guardar el disco");
            }
        } else {
            Dialog.showWarning("Error", "Los campos de texto son incorrectos", "Asegurese de haber metido bien el texto en los campos de nombre y/o foto");
        }

    }

    /**
     * This method is used to persis a disc with the params below
     *
     * @param song              song to persist
     * @param tf_song_name      song name textfield of the tab
     * @param tf_song_duration  song duration textfield of the tab
     * @param tf_song_songURL   song url textfield of the tab
     * @param cb_song_sl_artist song combobox artist of the tab
     * @param cb_song_sl_disc   song combobox disc of the tab
     * @param cb_song_sl_genre  song combobox genre of the tab
     */
    private void persistSong(SongDAO song, TextField tf_song_name, TextField tf_song_duration, TextField tf_song_songURL, ComboBox<IArtist> cb_song_sl_artist, ComboBox<IDisc> cb_song_sl_disc, ComboBox<IGenre> cb_song_sl_genre) {
        if (!tf_song_name.getText().trim().equals("") && !tf_song_duration.getText().trim().equals("") && !tf_song_songURL.getText().trim().equals("")) {
            song.setName(tf_song_name.getText());
            song.setSongURL(tf_song_songURL.getText());
            if (!tf_song_songURL.getText().startsWith("http")) {
                if (Tools.FileCopy(tf_song_songURL.getText(), "assets/songs/" + song.getName() + tf_song_songURL.getText().substring(tf_song_songURL.getText().lastIndexOf(".")))) {
                    Dialog.showInformation("", "Exito al copiar la canción", "Hemos guardado una copia de tu foto en otra carpeta!");
                    song.setSongURL("assets/songs/" + song.getName() + tf_song_songURL.getText().substring(tf_song_songURL.getText().lastIndexOf(".")));
                } else {
                    Dialog.showInformation("¡Cuidado!", "Has metido una URL de internet", "No se hará una copia en local de dicha imagen, ¡Cuidado no te la borren del servidor!");
                }
            }
            song.setDuration(((Double) tf_song_duration.getTextFormatter().getValue()));
            if (cb_song_sl_artist.getValue() != null && cb_song_sl_disc.getValue() != null && cb_song_sl_genre.getValue() != null) {
                song.setDisc(cb_song_sl_disc.getValue());
                song.getDisc().setArtist(cb_song_sl_artist.getValue());
                song.setGenre(cb_song_sl_genre.getValue());
                song.persist();
                Tools.setComboBoxesforSong(song_comboboxes);
                tf_song_name.clear();
                tf_song_duration.clear();
                tf_song_songURL.clear();
                Dialog.showInformation("", "Se creó/actualizó correctamente", "La canción se ha guardado correctamente en la base de datos");
            } else {
                Dialog.showWarning("Cuidado", "Imposible actualizar/crear canción", "No has seleccionado ningún valor en algún combobox");
            }
        } else {
            Dialog.showWarning("Error", "No se pudo guardar la canción", "Falta por rellenar algún campo de texto, por favor revíselo");
        }

    }

    /**
     * This method is used to persist a genre with the params below
     *
     * @param genre         genre to persist
     * @param tf_genre_name genre name combobox of the tab
     */

    private void persistGenre(GenreDAO genre, TextField tf_genre_name) {
        if (!tf_genre_name.getText().trim().equals("")) {
            genre.setName(tf_genre_name.getText());
            genre.persist();
            Tools.setComboBoxesforGenre(genre_comboboxes);
            Dialog.showInformation("", "Se creó/actualizó correctamente", "El género se ha guardado correctamente en la base de datos");
            tf_genre_name.clear();
        }
    }

    /**
     * This method is used to add listeners on textFields to update the image on ImageViews
     *
     * @param createTextField TextField on create tab, that needs a listener
     * @param createImageView ImageView on create tab, that needs to update
     * @param default_photo   default photo to put into in case the file is not found
     * @param updateTextField TextField on update tab, that needs a listener
     * @param updateImageView ImageView on update tab, that needs to update
     */
    private void addListenersForImageViews(TextField createTextField, ImageView createImageView, Image default_photo, TextField updateTextField, ImageView updateImageView) {
        createTextField.textProperty().addListener(onChange -> {
            Image i = Tools.getImage(createTextField.getText(), false);
            createImageView.setImage(Objects.requireNonNullElse(i, default_photo));
        });
        updateTextField.textProperty().addListener(onChange -> {
            Image i = Tools.getImage(updateTextField.getText(), false);
            updateImageView.setImage(Objects.requireNonNullElse(i, default_photo));
        });
    }

    /**
     * This method is used to inject all listener to comboboxes, to makes changes on another comboboxes when changing a value of one of them
     */
    private void addListenersOnComboBoxes() {
        AtomicReference<ArtistDAO> a_updateDisc = new AtomicReference<>();
        AtomicReference<ArtistDAO> a_updateSong = new AtomicReference<>();
        AtomicReference<DiscDAO> d_updateSong = new AtomicReference<>();
        AtomicReference<DiscDAO> d_deleteSong = new AtomicReference<>();
        addArtistComboBoxListeners(cb_c_song_sl_artist, cb_c_song_sl_disc);
        addArtistComboBoxListeners(cb_u_song_sl_artist, cb_u_song_sl_disc);
        addArtistComboBoxListeners(cb_u_song_sl_artist_in, cb_u_song_sl_disc_in);
        addArtistComboBoxListeners(cb_r_disc_sl_artist, cb_r_disc_sl_disc);
        addArtistComboBoxListeners(cb_r_song_sl_artist, cb_r_song_sl_disc);
        addArtistComboBoxListenersForUpdate(a_updateDisc, cb_u_disc_sl_artist, cb_u_disc_sl_disc);
        addArtistComboBoxListenersForUpdate(a_updateSong, cb_u_song_sl_artist, cb_u_song_sl_disc);
        cb_u_artist_sl_artist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selected_artist_update = new ArtistDAO(newValue.getId());
                tf_u_artist_name.setText(newValue.getName());
                tf_u_artist_nationality.setText(newValue.getNationality());
                tf_u_artist_photoURL.setText(newValue.getPhotoURL());
            }
        });
        cb_u_disc_sl_disc.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selected_disc_update = new DiscDAO(newValue.getId());
                tf_u_disc_name.setText(newValue.getName());
                tf_u_disc_photoURL.setText(newValue.getPhotoURL());
                dp_u_disc_pubdate.setValue(newValue.getPublicationDate());
                cb_u_disc_sl_artist_in.setDisable(true);
                cb_u_disc_sl_artist_in.getSelectionModel().select(a_updateDisc.get());
                cb_u_disc_sl_artist_in.setDisable(false);
            }
        });
        cb_u_song_sl_disc.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                d_updateSong.set(new DiscDAO(newValue.getId()));
                cb_u_song_sl_song.setDisable(true);
                cb_u_song_sl_song.setItems(FXCollections.observableArrayList(d_updateSong.get().getSongs()));
                cb_u_song_sl_song.setDisable(false);
            }
        });
        cb_r_song_sl_disc.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                d_deleteSong.set(new DiscDAO(newValue.getId()));
                cb_r_song_sl_song.setDisable(true);
                cb_r_song_sl_song.setItems(FXCollections.observableArrayList(d_deleteSong.get().getSongs()));
                cb_r_song_sl_song.setDisable(false);
            }
        });
        cb_u_song_sl_song.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selected_song_update = new SongDAO(newValue.getId());
                tf_u_song_name.setText(newValue.getName());
                tf_u_song_duration.setText(newValue.getDuration() + "");
                tf_u_song_songURL.setText(newValue.getSongURL());
                Tools.refreshValuesOfSong(newValue.getSongURL(), tf_u_song_duration, btn_u_song_play);
                cb_u_song_sl_genre.setDisable(true);
                cb_u_song_sl_genre.getSelectionModel().select(newValue.getGenre());
                cb_u_song_sl_genre.setDisable(false);
                cb_u_song_sl_artist_in.setDisable(true);
                cb_u_song_sl_artist_in.getSelectionModel().select(newValue.getDisc().getArtist());
                cb_u_song_sl_artist_in.setDisable(false);
                cb_u_song_sl_disc_in.setDisable(true);
                cb_u_song_sl_disc_in.getSelectionModel().select(newValue.getDisc());
                cb_u_song_sl_disc_in.setDisable(false);
            }
        });
        cb_u_genre_sl_genre.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selected_genre_update = new GenreDAO(newValue.getId());
                tf_u_genre_name.setText(newValue.getName());
            }
        });
        cb_r_artist_sl_artist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selected_artist_remove = new ArtistDAO(newValue.getId());
                tf_r_artist_name.setText(newValue.getName());
                tf_r_artist_nationality.setText(newValue.getNationality());
                tf_r_artist_photoURL.setText(newValue.getPhotoURL());
                image_r_artist.setImage(Tools.getImage(newValue.getPhotoURL(), false));
            }
        });
        cb_r_disc_sl_disc.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selected_disc_remove = new DiscDAO(newValue.getId());
                tf_r_disc_name.setText(newValue.getName());
                tf_r_disc_photoURL.setText(newValue.getPhotoURL());
                dp_r_disc_pubdate.setValue(newValue.getPublicationDate());
                image_r_disc.setImage(Tools.getImage(newValue.getPhotoURL(), false));
            }
        });
        cb_r_song_sl_song.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selected_song_remove = new SongDAO(newValue.getId());
                tf_r_song_name.setText(newValue.getName());
                tf_r_song_duration.setText(newValue.getDuration() + "");
                tf_r_song_songURL.setText(newValue.getSongURL());
                Tools.refreshValuesOfSong(newValue.getSongURL(), tf_r_song_duration, btn_r_song_play);
            }
        });
        cb_r_genre_sl_genre.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null){
                selected_genre_remove = new GenreDAO(newValue.getId());
                tf_r_genre_name.setText(newValue.getName());
            }
        });

    }

    /**
     * This method is used to update Disc Comboboxes when Artist on another combobox has changed
     *
     * @param a_updateDisc      atomic reference from artist to update the discs
     * @param cb_disc_sl_artist combobox to add the listener
     * @param cb_disc_sl_disc   combobox to refresh
     */
    private void addArtistComboBoxListenersForUpdate(AtomicReference<ArtistDAO> a_updateDisc, ComboBox<IArtist> cb_disc_sl_artist, ComboBox<IDisc> cb_disc_sl_disc) {
        cb_disc_sl_artist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                a_updateDisc.set(new ArtistDAO(newValue.getId()));
                cb_disc_sl_disc.setDisable(true);
                cb_disc_sl_disc.setItems(FXCollections.observableArrayList(a_updateDisc.get().getDiscs()));
                cb_disc_sl_disc.setDisable(false);
            }
        });
    }

    /**
     * This method update Disc Comboboxes when Artist on another Combobox has changed
     *
     * @param cb_sl_artist combobox artist to add the listener
     * @param cb_sl_disc   combobox disc to update discs
     */
    private void addArtistComboBoxListeners(ComboBox<IArtist> cb_sl_artist, ComboBox<IDisc> cb_sl_disc) {
        cb_sl_artist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                cb_sl_disc.setDisable(true);
                ArtistDAO a = new ArtistDAO(newValue.getId());
                cb_sl_disc.setItems(FXCollections.observableArrayList(a.getDiscs()));
                cb_sl_disc.setDisable(false);
            }
        });
    }

}
