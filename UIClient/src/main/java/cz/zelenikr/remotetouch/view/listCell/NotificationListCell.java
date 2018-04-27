package cz.zelenikr.remotetouch.view.listCell;

import cz.zelenikr.remotetouch.data.dto.event.NotificationEventContent;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Roman Zelenik
 */
public class NotificationListCell extends ListCell<NotificationEventContent> {

    private ViewHolder holder;
    private DateFormat dateFormat;

    public NotificationListCell() {
        this.holder = new ViewHolder();
        this.dateFormat = new SimpleDateFormat();
    }

    @Override
    protected void updateItem(NotificationEventContent item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            String appName = (item.getLabel() == null || item.getLabel().isEmpty()) ? item.getApp() : item.getLabel();
            holder.setAppName(appName);
            holder.setTitle(item.getTitle());
            holder.setText(item.getText());
            holder.setDatetime(formatDatetime(item.getWhen()));
            holder.setIcon(getIconByApp(appName));
            setGraphic(holder.getContent());
        }
    }

    private FontAwesomeIcon getIconByApp(String app) {
        if (app.contains("messenger")) return FontAwesomeIcon.WHATSAPP;
        return FontAwesomeIcon.TH_LARGE;
    }

    private String formatDatetime(long timestamp) {
        return dateFormat.format(new Date(timestamp));
    }

    /**
     * Represents view of list cell.
     */
    private class ViewHolder  {

        @FXML
        private GridPane rootPane;
        @FXML
        private Label appName, title, datetime;
        @FXML
        private Text text;

        public ViewHolder() {
            FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("view/notification/notification_cell.fxml"));
            loader.setController(this);
            try {
                loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        public Node getContent() {
            return rootPane;
        }

        public void setIcon(FontAwesomeIcon glyph) {
            if (glyph == null) {
                appName.setGraphic(null);
            } else {
                appName.setGraphic(new FontAwesomeIconView(glyph));
            }
        }

        public void setAppName(String value) {
            if (value == null) value = "";
            appName.setText(value);
        }

        public void setTitle(String value) {
            if (value == null) value = "";
            title.setText(value);
        }

        public void setDatetime(String value) {
            if (value == null) value = "";
            datetime.setText(value);
        }

        public void setText(String value) {
            if (value == null) value = "";
            text.setText(value);
        }


    }
}
