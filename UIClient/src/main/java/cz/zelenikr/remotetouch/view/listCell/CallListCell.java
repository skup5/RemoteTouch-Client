package cz.zelenikr.remotetouch.view.listCell;

import cz.zelenikr.remotetouch.Resources;
import cz.zelenikr.remotetouch.controller.Controller;
import cz.zelenikr.remotetouch.data.dto.event.CallEventContent;
import cz.zelenikr.remotetouch.data.formatter.EventDateTimeFormat;
import cz.zelenikr.remotetouch.data.mapper.CallTypeToLocalStringMapper;
import de.jensd.fx.glyphs.GlyphIcon;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.io.IOException;

/**
 * @author Roman Zelenik
 */
public class CallListCell extends ListCell<CallEventContent> {

    private ViewHolder holder;
    private EventDateTimeFormat dateFormat;
    private EventHandler<MouseEvent> closeEventHandler;

    public CallListCell() {
        this.holder = new ViewHolder();
        this.dateFormat = new EventDateTimeFormat();
    }

    @Override
    protected void updateItem(CallEventContent item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            boolean containsName = item.getName() != null && !item.getName().isEmpty();
            String caller = containsName ? item.getName() : item.getNumber();
            String callerTooltip = containsName ? item.getNumber() : "";
            holder.setCaller(caller);
            holder.setCallerTooltip(callerTooltip);
            holder.setType(CallTypeToLocalStringMapper.toString(item.getType()));
            holder.setDatetime(dateFormat.format(item.getWhen()));
            holder.setIcon(Resources.Icons.INSTANCE.getIconByCallType(item.getType()));

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
        private Label caller, type, datetime, close;

        private Tooltip callerTooltip;

        public ViewHolder() {
            try {
                Resources.INSTANCE.loadView("view/call/call_cell.fxml", this, null);
                close.setGraphic(Resources.Icons.INSTANCE.getRemoveEventIcon());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Node getContent() {
            return rootPane;
        }

        public void setIcon(GlyphIcon glyph) {
            if (glyph == null) {
                caller.setGraphic(null);
            } else {
                caller.setGraphic(glyph);
            }
        }

        public void setCaller(String value) {
            if (value == null) value = "";
            caller.setText(value);
        }

        public void setType(String value) {
            if (value == null) value = "";
            type.setText(value);
        }

        public void setDatetime(String value) {
            if (value == null) value = "";
            datetime.setText(value);
        }

        public void setCallerTooltip(String value) {
            Tooltip tooltip = null;
            if (value != null && !value.isEmpty()) {
                tooltip = new Tooltip(value);
                tooltip.setShowDuration(Duration.INDEFINITE);
            }
            caller.setTooltip(tooltip);
        }

        public void setOnCloseClicked(EventHandler<MouseEvent> handler, Object userData) {
            close.setOnMouseClicked(handler);
            close.setUserData(userData);
        }
    }
}
