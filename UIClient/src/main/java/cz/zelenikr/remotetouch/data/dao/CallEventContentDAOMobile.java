package cz.zelenikr.remotetouch.data.dao;

import cz.zelenikr.remotetouch.Callback;
import cz.zelenikr.remotetouch.data.dto.event.CallEventContent;

/**
 * Provides access to a mobile calls stored in remote mobile phone.
 *
 * @author Roman Zelenik
 */
public class CallEventContentDAOMobile extends GenericMobileContentDAO<CallEventContent> implements CallEventContentDAO {

    private Callback<CallEventContent> newItemCallback;

    public CallEventContentDAOMobile() {
        super();
        connectionManager.registerCallReceivedListener(this::onNewCall);
    }

    @Override
    public void setOnNewItemCallback(Callback<CallEventContent> callback) {
        newItemCallback = callback;
    }

    private void onNewCall(CallEventContent... callEventContents) {
        if (newItemCallback != null) {
            for (CallEventContent content : callEventContents) {
                newItemCallback.call(content);
            }
        }
    }
}
