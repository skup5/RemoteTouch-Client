package cz.zelenikr.remotetouch.view.listCell;

import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.controller.Controller;
import cz.zelenikr.remotetouch.data.dto.event.NotificationEventContent;
import cz.zelenikr.remotetouch.data.formatter.EventDateTimeFormat;
import de.jensd.fx.glyphs.GlyphIcon;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * @author Roman Zelenik
 */
public class NotificationListCell extends ListCell<NotificationEventContent> {

    private ViewHolder holder;
    private EventDateTimeFormat dateFormat;
    private EventHandler<MouseEvent> closeEventHandler;

    public NotificationListCell(ListView<NotificationEventContent> listView) {
        this.dateFormat = new EventDateTimeFormat();
        this.holder = new ViewHolder();
        this.holder.getTextControl().wrappingWidthProperty().bind(listView.widthProperty().subtract(30));
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
            holder.setDatetime(dateFormat.format(item.getWhen()));
            holder.setIcon(Resources.Icons.INSTANCE.getIconByApp(item.getApp()));

            if (closeEventHandler != null) holder.setOnCloseClicked(closeEventHandler, item);

            setGraphic(holder.getContent());
        }
    }

    public void setCloseEventHandler(EventHandler<MouseEvent> closeEventHandler) {
        this.closeEventHandler = closeEventHandler;
    }

    /**
     * Represents view of list cell.
     */
    private class ViewHolder implements Controller {

        @FXML
        private GridPane rootPane;
        @FXML
        private Label appName, title, datetime, close;
        @FXML
        private Text text;

        public ViewHolder() {
            try {
                Resources.INSTANCE.loadView("view/notification/notification_cell.fxml", this, null);
                close.setGraphic(Resources.Icons.INSTANCE.getRemoveEventIcon());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Node getContent() {
            return rootPane;
        }

        public Text getTextControl() {
            return text;
        }

        public void setIcon(GlyphIcon glyph) {
            if (glyph == null) {
                appName.setGraphic(null);
            } else {
                appName.setGraphic(glyph);
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

        public void setOnCloseClicked(EventHandler<MouseEvent> handler, Object userData) {
            close.setOnMouseClicked(handler);
            close.setUserData(userData);
        }
    }
}
