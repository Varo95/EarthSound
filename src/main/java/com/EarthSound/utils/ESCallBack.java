package com.EarthSound.utils;
import com.EarthSound.interfaces.IBeans.IPhotoName;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

/**
 * This class is used to create a callback for the EarthSound application.
 */

public class ESCallBack<T extends IPhotoName> implements Callback<ListView<T>, ListCell<T>> {

    @Override
    public ListCell<T> call(final ListView<T> o) {
        return new ListCell<>() {
            @Override
            protected void updateItem(final T item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    final ImageView imageView = new ImageView(Tools.getImage(item.getPhoto(), false));
                    imageView.setFitWidth(40);
                    imageView.setFitHeight(40);
                    setGraphic(imageView);
                    setText(item.getName());
                }
            }
        };
    }
}
