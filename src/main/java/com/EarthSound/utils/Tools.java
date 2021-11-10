package com.EarthSound.utils;

import com.EarthSound.App;
import com.EarthSound.interfaces.IBeans.IPlayList;
import com.EarthSound.interfaces.IBeans.IDisc;
import com.EarthSound.interfaces.IBeans.ISong;
import com.EarthSound.interfaces.IBeans.IArtist;
import com.EarthSound.interfaces.IBeans.IGenre;
import com.EarthSound.models.DAO.SQL.*;
import com.EarthSound.models.beans.*;
import com.jfoenix.controls.JFXButton;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class Tools {

    private static final Logger logger = LoggerFactory.getLogger(Tools.class);

    private static final String URL_IMG_EXPRESSION = "(http)?s?:?(\\/\\/[^\"']*\\.(?:bmp|gif|jpg|jpeg|png))";
    private static final String URL_SONG_EXPRESSION = "(http)?s?:?(\\/\\/[^\"']*\\.(?:mp3|aif|aiff|wav))";
    private static final String DOUBLE_EXPRESSION_TF = "-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?";
    public static final Image default_photo_user = getImage("assets/user_default.png", true);
    public static final Image default_photo_disc = getImage("assets/disc_default.png", true);

    /**
     * This method is used to validate an url from internet
     *
     * @param url url to check
     * @return true if ends with jpg, gif, or png, otherwhise, false
     */
    public static boolean Validate_img_URL(String url) {
        return Pattern.compile(URL_IMG_EXPRESSION).matcher(url).matches();
    }

    public static boolean Validate_song_URL(String url) {
        return Pattern.compile(URL_SONG_EXPRESSION).matcher(url).matches();
    }

    public static boolean Validate_Double_Value(String value) {
        return Pattern.compile(DOUBLE_EXPRESSION_TF).matcher(value).matches();
    }

    public static boolean ValidateFile_img(String url) {
        boolean result = switch (url.toLowerCase().substring(url.length() - 4, url.length())) {
            case ".bmp", ".gif", ".jpg", ".png" -> true;
            default -> false;
        };
        if (!result) {
            if (url.endsWith(".jpeg")) {
                result = true;
            }
        }
        return result;
    }

    public static boolean ValidateFile_song(String url) {
        boolean result = switch (url.toLowerCase().substring(url.length() - 4, url.length())) {
            case ".mp3", ".aif", ".wav" -> true;
            default -> false;
        };
        if (!result) {
            if (url.endsWith(".aiff")) {
                result = true;
            }
        }
        return result;
    }

    /**
     * This util is used to play a sound
     *
     * @param resPath   relative or absolute path folder, like "foo/bar.mp3" or "/home/user/foo.mp3" or "C:\Users\User\Desktop\bar.mp3"
     * @param isResPath indicate if the source is on resPath(jar) or not
     * @return the MediaPlayer with the sound loaded
     */
    public static MediaPlayer getSound(String resPath, boolean isResPath) {
        if (isResPath)
            return new MediaPlayer(new Media(Objects.requireNonNull(App.class.getResource(resPath)).toExternalForm()));
        else {
            if (!Validate_song_URL(resPath)) {
                File f = new File(resPath);
                if (f.exists() && f.isFile())
                    if (ValidateFile_song(f.getName()))
                        return new MediaPlayer(new Media(f.toURI().toString()));
            } else if (Validate_song_URL((resPath))) {
                return new MediaPlayer(new Media(resPath));
            }
            return null;
        }
    }

    /**
     * This util is used to get an Image
     *
     * @param resPath   relative or absolute path folder, like "foo/bar.png" or "/home/user/foo.png" or "C:\Users\User\Desktop\bar.png"
     * @param isResPath indicate if the source is on resPath(jar) or not
     * @return the Image from the resources loaded
     */
    public static Image getImage(String resPath, boolean isResPath) {
        if (isResPath)
            return new Image(Objects.requireNonNull(App.class.getResourceAsStream(resPath)));
        else {
            if (!Validate_img_URL(resPath)) {
                File f = new File(resPath);
                if (f.exists() && f.isFile())
                    if (ValidateFile_img(f.getName()))
                        return new Image(Objects.requireNonNull(f.getAbsoluteFile().getAbsolutePath()));
            } else if (Validate_img_URL(resPath)) {
                return new Image(resPath);
            }
            return null;
        }
    }

    /**
     * This method is used to load database configuration from an XML at .jar (or compiled classes) level
     *
     * @return a DataConnection based on XML if no error, else, a new DataConnection
     */
    public static DataConnection loadXML() {
        DataConnection result;
        try (BufferedReader r = new BufferedReader(new FileReader("Connection.xml"))) {
            JAXBContext jaxbC = JAXBContext.newInstance(DataConnection.class);
            Unmarshaller um = jaxbC.createUnmarshaller();
            result = (DataConnection) um.unmarshal(r);
        } catch (IOException | JAXBException e) {
            Dialog.showWarning("Error", "Hubo un error al cargar el XML", "Se intentará crear un nuevo fichero en esa ruta");
            result = new DataConnection("", "spotify", "root", "toor", "H2");
            if (e.getClass().equals(IOException.class))
                logger.error("No se ha encontrado la ruta del fichero XML: " + e.getMessage());
            else if (e.getClass().equals(JAXBException.class))
                logger.error("Hubo un error en la lectura del XML: " + e.getMessage());
            else
                logger.error("Hubo un error desconocido: " + e.getMessage());
            saveXML(result);
        }

        return result;
    }

    /**
     * This method is used to save database configuration to an XML
     *
     * @param dc DataConnection to save
     */
    private static void saveXML(DataConnection dc) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter("Connection.xml"))) {
            JAXBContext jaxbC = JAXBContext.newInstance(DataConnection.class);
            Marshaller m = jaxbC.createMarshaller();
            m.setProperty(m.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(dc, w);
        } catch (IOException | JAXBException e) {
            Dialog.showError("Error", "Hubo un error al guardar el XML", e.getMessage());
            if (e.getClass().equals(IOException.class))
                logger.error("No se ha encontrado el fichero XML: " + e.getMessage());
            else if (e.getClass().equals(JAXBException.class))
                logger.error("Hubo un error en la escritura del XML: " + e.getMessage());
            else
                logger.error("Hubo un error desconocido: " + e.getMessage());
        }
    }

    /**
     * This method is used to get a formatted String with values of the MediaPlayer
     *
     * @param elapsed  time on sound
     * @param duration total time of the sound
     * @return formatted string
     */
    public static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0)
            intElapsed -= elapsedHours * 60 * 60;
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;
        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0)
                intDuration -= durationHours * 60 * 60;
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
            if (durationHours > 0)
                return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds, durationHours, durationMinutes, durationSeconds);
            else
                return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds, durationMinutes, durationSeconds);
        } else {
            if (elapsedHours > 0)
                return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
            else
                return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
        }
    }

    /**
     * This method is used to encrypt Strings with MD5 hash
     *
     * @param s String that needs to hash
     * @return hashed MD5 string
     */
    public static String encryptMD5(String s) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : md.digest()) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            result = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("no Provider supports a MessageDigestSpi implementation for the specified algorithm" + e.getMessage());
        }
        return result;
    }

    /**
     * This method is used to copy sources to a .jar path level
     *
     * @param source source file in
     * @param target target file out
     * @return true if success
     */
    public static boolean FileCopy(String source, String target) {
        boolean result;
        try {
            File f = new File(target);
            if (!Files.exists(f.toPath())) {
                Files.createDirectories(f.toPath());
            }
            Files.copy(Paths.get(source), Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
            result = true;
        } catch (IOException e) {
            logger.error("Error al copiar ficheros, con el mensaje:\n" + e.getMessage());
            result = false;
        }
        return result;
    }

    /**
     * This method is used to see a password on a passwordField on javafx
     *
     * @param tt A tooltip to use
     * @param pf the password field
     */
    public static void showPassword(Tooltip tt, PasswordField pf) {
        if (!tt.isShowing()) {
            Point2D p = pf.localToScene(pf.getBoundsInLocal().getMaxX(), pf.getBoundsInLocal().getMaxY());
            tt.setText(pf.getText());
            tt.show(pf,
                    p.getX() + pf.getScene().getX() + pf.getScene().getWindow().getX(),
                    p.getY() + pf.getScene().getY() + pf.getScene().getWindow().getY());
        } else {
            tt.setText("");
            tt.hide();
        }
    }


    /**
     * This method is used to set the icon and css
     *
     * @param window window to need apply styles
     */
    public static void addCssAndIcon(Stage window) {
        window.getScene().getStylesheets().add(String.valueOf(App.class.getResource("dark.css")));
        window.getIcons().add(Tools.getImage("icon.png", true));
    }

    /**
     * This method is used to check that TextFields are not empty
     *
     * @param textFieldList texfields that needs to be checked
     * @return true if all are valid, false if one is not valid
     */
    public static boolean checkTexts(List<TextField> textFieldList) {
        List<Boolean> validOrNot = new ArrayList<>();
        for (TextField tf : textFieldList) {
            if (!tf.getText().trim().equals("")) {
                validOrNot.add(true);
            } else {
                validOrNot.add(false);
            }
        }
        return !validOrNot.contains(false);
    }

    /**
     * This method is used to only write double values on a TextField
     *
     * @param tf TextField to update
     */
    public static void onlyDoubleValue(TextField tf) {
        UnaryOperator<Change> filter = c -> Validate_Double_Value(c.getControlNewText()) ? c : null;
        StringConverter<Double> converter = new StringConverter<>() {
            @Override
            public Double fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return 0.0;
                } else {
                    return Double.valueOf(s);
                }
            }

            @Override
            public String toString(Double d) {
                return d.toString();
            }
        };
        TextFormatter<Double> textFormatter = new TextFormatter<>(converter, 0.0, filter);
        tf.setTextFormatter(textFormatter);
    }

    /**
     * This method is used to set converter on combobox
     *
     * @return artist stringconverter for combobox
     */
    public static StringConverter<IArtist> artistConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(IArtist object) {
                return object == null ? null : object.toCombox();
            }

            @Override
            public Artist fromString(String string) {
                ArtistDAO a = null;
                if (string != null) {
                    long id = Long.parseLong(string.substring(string.lastIndexOf(".")));
                    a = new ArtistDAO(id);
                }
                return a;
            }
        };
    }

    /**
     * This method is used to set converter on combobox
     *
     * @return disc stringconverter for combobox
     */
    public static StringConverter<IDisc> discConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(IDisc object) {
                return object == null ? null : object.toCombox();
            }

            @Override
            public Disc fromString(String string) {
                long id = Long.parseLong(string.substring(string.lastIndexOf(".")));
                return new DiscDAO(id);
            }
        };
    }

    /**
     * This method is used to set converter on combobox
     *
     * @return song stringconverter for combobox
     */
    public static StringConverter<ISong> songConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(ISong object) {
                return object == null ? null : object.toCombox();
            }

            @Override
            public Song fromString(String string) {
                long id = Long.parseLong(string.substring(string.lastIndexOf(".")));
                return new SongDAO(id);
            }
        };
    }

    /**
     * This method is used to set converter on combobox
     *
     * @return genre stringconverter for combobox
     */
    public static StringConverter<IGenre> genreConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(IGenre object) {
                return object == null ? null : object.toCombox();
            }

            @Override
            public Genre fromString(String string) {
                long id = Long.parseLong(string.substring(string.lastIndexOf(".")));
                return new GenreDAO(id);
            }
        };
    }

    /**
     * This method is used to ser converter on combobox
     *
     * @return playlist stringconverter for combobox
     */
    public static StringConverter<IPlayList> playListConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(IPlayList object) {
                return object == null ? null : object.toCombobox();
            }

            @Override
            public IPlayList fromString(String string) {
                long id = Long.parseLong(string.substring(string.lastIndexOf(".")));
                return new PlayListDAO(id);
            }
        };
    }

    /**
     * This method is used to optimize code lines. It iterate a list and set the converters
     *
     * @param cb_list list to iterate
     */
    public static void setComboBoxesforArtist(List<ComboBox<IArtist>> cb_list) {
        for (ComboBox<IArtist> cb : cb_list) {
            cb.setConverter(artistConverter());
            cb.setItems(FXCollections.observableArrayList(ArtistDAO.listAll()));
            cb.setCellFactory(new Callback<>() {
                @Override
                public ListCell<IArtist> call(ListView<IArtist> l) {
                    return new ListCell<>() {
                        @Override
                        protected void updateItem(IArtist item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setGraphic(null);
                            } else {
                                setText(item.getName() + "." + item.getId());
                                ImageView imageView = new ImageView(Tools.getImage(item.getPhotoURL(), false));
                                imageView.setFitWidth(40);
                                imageView.setFitHeight(40);
                                setGraphic(imageView);
                            }
                        }
                    };
                }
            });
            cb.setVisibleRowCount(5);
        }
    }

    /**
     * This method is used to optimize code lines. It iterate a list and set the converters
     *
     * @param cb_list list to iterate
     */
    public static void setComboBoxesforDisc(List<ComboBox<IDisc>> cb_list) {
        for (ComboBox<IDisc> cb : cb_list) {
            cb.setConverter(discConverter());
            cb.setCellFactory(new Callback<>() {
                @Override
                public ListCell<IDisc> call(ListView<IDisc> l) {
                    return new ListCell<>() {
                        @Override
                        protected void updateItem(IDisc item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setGraphic(null);
                            } else {
                                setText(item.getName() + "." + item.getId());
                                ImageView imageView = new ImageView(Tools.getImage(item.getPhotoURL(), false));
                                imageView.setFitWidth(40);
                                imageView.setFitHeight(40);
                                setGraphic(imageView);
                            }
                        }
                    };
                }
            });
            cb.setVisibleRowCount(5);
        }
    }

    /**
     * This method is used to optimize code lines. It iterate a list and set the converters
     *
     * @param cb_list list to iterate
     */
    public static void setComboBoxesforSong(List<ComboBox<ISong>> cb_list) {
        for (ComboBox<ISong> cb : cb_list) {
            cb.setConverter(songConverter());
            cb.setVisibleRowCount(5);
        }
    }

    /**
     * This method is used to optimize code lines. It iterate a list and set the converters
     *
     * @param cb_list list to iterate
     */
    public static void setComboBoxesforGenre(List<ComboBox<IGenre>> cb_list) {
        for (ComboBox<IGenre> cb : cb_list) {
            cb.setConverter(genreConverter());
            cb.setItems(FXCollections.observableArrayList(GenreDAO.listAll()));
            cb.setVisibleRowCount(5);
        }
    }

    /**
     * This method retrieves the URI absolute path of a file selected on a SYSTEM window
     *
     * @param isSound true if files to explore are sound, false if images
     * @return the absolute path of the file
     */
    public static String selectFile(boolean isSound) {
        FileChooser fc = new FileChooser();
        if (isSound)
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Sound Files", "*.mp3", "*.aif", "*.wav", "*.aiff"));
        else
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.bmp", "*.gif", "*.jpg", "*.jpeg", "*.png"));
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        } else {
            Dialog.showWarning("Advertencia", "", "Fichero no seleccionado!");
            return null;
        }
    }

    /**
     * This method is used to get the duration of the sound in seconds(double)
     *
     * @param file     file to get the duration
     * @param tf       textfield of the duration for song
     * @param btn_play button play to set the playsong actions
     */
    public static void refreshValuesOfSong(String file, TextField tf, JFXButton btn_play) {
        MediaPlayer mp = getSound(file, false);
        final boolean[] stopRequested = {false};
        btn_play.setDisable(true);
        btn_play.setText("");
        btn_play.getScene().getWindow().setOnCloseRequest(event -> {
            if (mp != null) {
                MediaPlayer.Status status = mp.getStatus();
                if (status == MediaPlayer.Status.PLAYING) {
                    mp.stop();
                }
            }
        });
        if (mp != null) {
            mp.setOnReady(() -> {
                tf.setText(mp.getMedia().getDuration().toSeconds() + "");
                btn_play.setDisable(false);
                btn_play.setText("▶");
            });
            mp.setOnPlaying(() -> {
                if (stopRequested[0]) {
                    mp.pause();
                    stopRequested[0] = false;
                } else {
                    btn_play.setText("⏸");
                }
            });
            mp.setOnPaused(() -> btn_play.setText("▶"));
            btn_play.setOnAction(e -> {
                MediaPlayer.Status status = mp.getStatus();
                if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED)
                    return;
                if (status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.READY || status == MediaPlayer.Status.STOPPED) {
                    mp.play();
                } else {
                    mp.pause();
                }
            });
            mp.setOnEndOfMedia(mp::stop);
        }
    }

    /**
     * This method is used to get the String sound of the file
     *
     * @param file file to get the name
     * @return string of the file name
     */
    public static String getNameFileOfSound(String file) {
        return new File(file).getName().replaceFirst("[.][^.]+$", "");
    }

    public static boolean FileDelete(String target) {
        boolean result;
        try {
            File f = new File(target);
            result = Files.deleteIfExists(f.toPath());
        } catch (IOException e) {
            logger.error("Error al borrar ficheros" + e.getMessage());
            result = false;
        }
        return result;
    }

    public static void unKnow() {
        logger.error("Exception in thread main java.lang.NullPointerException at com.EarthSound.App.nullpointer.Null.method(Null.java:80000)\n" +
                "at com.EarthSound.App.nullpointer.Null.main(NullExample.java:721298)");
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                URI uri;
                try {
                    uri = new URI("https://youtu.be/dQw4w9WgXcQ");
                    desktop.browse(uri);
                } catch (URISyntaxException | IOException e) {
                    if (e.getClass().equals(URISyntaxException.class)) {
                        logger.error("La cadena introducida viola el código RFC2396" + e.getMessage());
                    } else {
                        logger.error("No se encontró el navegador por defecto " + e.getMessage());
                    }
                }
            }
        }
        Dialog.showError("Pulsaste donde no debías", "Tu programa acaba de reventar", "Tu código es una mierda");

    }
}
