package cz.zelenikr.remotetouch.manager;

import de.jensd.fx.glyphs.GlyphIcon;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 * @author Roman Zelenik
 */
public final class NotificationManager {

    private static final NotificationManager INSTANCE = new NotificationManager();
    private static Stage dummyStage;

    public static NotificationManager getInstance() {
        return INSTANCE;
    }

    /**
     * Shows new notification on screen to the user.
     *
     * @param icon
     * @param title
     * @param text
     * @param onClickAction
     */
    public void notify(GlyphIcon icon, String title, String text, EventHandler<ActionEvent> onClickAction) {
        Notifications notification = Notifications.create()
                .title(title)
                .text(text);

        if (icon != null) {
            icon.setSize("30");
            notification.graphic(icon);
        }

        if (onClickAction != null) {
            notification.onAction(onClickAction);
        }

        notify(notification);
    }

    private void notify(Notifications builder) {
        builder.hideAfter(Duration.INDEFINITE)
                .position(Pos.BOTTOM_RIGHT)
                .owner(getNotificationOwner());

//            notificationBuilder.hideCloseButton();
//         notificationBuilder.darkStyle();

        builder.show();
    }

    private static Stage createDummyStage() {
        Stage dummyPopup = new Stage();
        dummyPopup.initModality(Modality.NONE);
        // set as utility so no iconification occurs
        dummyPopup.initStyle(StageStyle.UTILITY);
        // set opacity so the window cannot be seen
        dummyPopup.setOpacity(0d);
        // not necessary, but this will move the dummy stage off the screen
        final Screen screen = Screen.getPrimary();
        final Rectangle2D bounds = screen.getVisualBounds();
        dummyPopup.setX(bounds.getMaxX() - 25);
        dummyPopup.setY(bounds.getMaxY() - 40);
        // create/add a transparent scene
        final Group root = new Group();
        dummyPopup.setScene(new Scene(root, 1d, 1d, Color.TRANSPARENT));
        // show the dummy stage
        dummyPopup.show();
        return dummyPopup;
    }

    private static Stage getNotificationOwner() {
        if (dummyStage == null) {
            dummyStage = createDummyStage();
        }
        return dummyStage;
    }

    private NotificationManager() {
    }
}
