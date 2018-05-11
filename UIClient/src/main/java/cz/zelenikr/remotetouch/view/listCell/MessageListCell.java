package cz.zelenikr.remotetouch.view.listCell;

import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.data.dto.event.SmsEventContent;
import de.jensd.fx.glyphs.GlyphIcon;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Roman Zelenik
 */
public class MessageListCell extends ListCell<SmsEventContent> {

    private final GlyphIcon icon = Resources.Icons.getSmsIcon();

    private ViewHolder holder;
    private DateFormat dateFormat;
    private EventHandler<MouseEvent> closeEventHandler;

    public MessageListCell(ListView<SmsEventContent> listView) {
        this.dateFormat = new SimpleDateFormat();
        this.holder = new ViewHolder();
        this.holder.getTextControl().wrappingWidthProperty().bind(listView.widthProperty().subtract(30));
    }

    @Override
    protected void updateItem(SmsEventContent item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            boolean containsName = item.getName() != null && !item.getName().isEmpty();
            String sender = containsName ? item.getName() : item.getNumber();
            String senderTooltip = containsName ? item.getNumber() : "";
            holder.setSender(sender);
            holder.setSenderTooltip(senderTooltip);
            holder.setText(item.getContent());
            holder.setDatetime(formatDatetime(item.getWhen()));
            holder.setIcon(icon);

            if (closeEventHandler != null) holder.setOnCloseClicked(closeEventHandler, item);

            setGraphic(holder.getContent());
        }
    }

    public void setCloseEventHandler(EventHandler<MouseEvent> closeEventHandler) {
        this.closeEventHandler = closeEventHandler;
    }

    private String formatDatetime(long timestamp) {
        return dateFormat.format(new Date(timestamp));
    }

    /**
     * Represents view of list cell.
     */
    private class ViewHolder {

        @FXML
        private GridPane rootPane;
        @FXML
        private Label sender, datetime, close;
        @FXML
        private Text text;

        private Tooltip senderTooltip;

        public ViewHolder() {
            FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("view/message/message_cell.fxml"));
            loader.setController(this);
            try {
                loader.load();
                close.setGraphic(Resources.Icons.getRemoveEventIcon());
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
                sender.setGraphic(null);
            } else {
                sender.setGraphic(glyph);
            }
        }

        public void setSender(String value) {
            if (value == null) value = "";
            sender.setText(value);
        }

        public void setText(String value) {
            if (value == null) value = "";
            text.setText(value);
        }

        public void setDatetime(String value) {
            if (value == null) value = "";
            datetime.setText(value);
        }

        public void setSenderTooltip(String value) {
            Tooltip tooltip = null;
            if (value != null && !value.isEmpty()) {
                tooltip = new Tooltip(value);
                tooltip.setShowDuration(Duration.INDEFINITE);
            }
            sender.setTooltip(tooltip);
        }

        public void setOnCloseClicked(EventHandler<MouseEvent> handler, Object userData) {
            close.setOnMouseClicked(handler);
            close.setUserData(userData);
        }
    }
}
