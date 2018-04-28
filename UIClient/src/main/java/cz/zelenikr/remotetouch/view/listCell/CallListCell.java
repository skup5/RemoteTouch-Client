package cz.zelenikr.remotetouch.view.listCell;

import cz.zelenikr.remotetouch.data.dto.CallType;
import cz.zelenikr.remotetouch.data.dto.event.CallEventContent;
import cz.zelenikr.remotetouch.data.mapper.CallTypeToLocalStringMapper;
import de.jensd.fx.glyphs.GlyphIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Roman Zelenik
 */
public class CallListCell extends ListCell<CallEventContent> {

    private ViewHolder holder;
    private DateFormat dateFormat;

    public CallListCell() {
        this.holder = new ViewHolder();
        this.dateFormat = new SimpleDateFormat();
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
            holder.setDatetime(formatDatetime(item.getWhen()));
            holder.setIcon(getIconByType(item.getType()));
            setGraphic(holder.getContent());
        }
    }

    private GlyphIcon getIconByType(CallType type) {
        MaterialIcon glyph;
        switch (type) {
            case ENDED:
                glyph = MaterialIcon.CALL_END;
                break;
            case INCOMING:
                glyph = MaterialIcon.RING_VOLUME;
                break;
            case MISSED:
                glyph = MaterialIcon.PHONE_MISSED;
                break;
            case ONGOING:
                glyph = MaterialIcon.PHONE_IN_TALK;
                break;
            case OUTGOING:
                glyph = MaterialIcon.PHONE_FORWARDED;
                break;
            default:
                glyph = MaterialIcon.PHONE;
                break;
        }
        return new MaterialIconView(glyph);
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
        private Label caller, type, datetime;

        private Tooltip callerTooltip;

        public ViewHolder() {
            FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("view/call/call_cell.fxml"));
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
            }
            caller.setTooltip(tooltip);
        }
    }
}
