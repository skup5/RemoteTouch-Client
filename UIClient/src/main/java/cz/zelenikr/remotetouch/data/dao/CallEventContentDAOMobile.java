package cz.zelenikr.remotetouch.data.dao;

import cz.zelenikr.remotetouch.Callback;
import cz.zelenikr.remotetouch.data.CallType;
import cz.zelenikr.remotetouch.data.event.CallEventContent;

/**
 * @author Roman Zelenik
 */
public class CallEventContentDAOMobile extends GenericMobileContentDAO<CallEventContent> implements CallEventContentDAO {

    private static CallEventContent[] mockData = {
            new CallEventContent("John Doe", "666 666 666", CallType.MISSED),
            new CallEventContent("Jane Doe", "+420 777 666 555", CallType.INCOMING),
            new CallEventContent("Jane Doe", "+420 777 666 555", CallType.ONGOING),
            new CallEventContent("Jane Doe", "+420 777 666 555", CallType.ENDED),
            new CallEventContent("", "963258741", CallType.OUTGOING)
    };

    private Callback<CallEventContent> newItemCallback;

    public CallEventContentDAOMobile() {
        super();
        connectionManager.registerCallReceivedListener(this::onNewCall);
    }

    @Override
    public void loadAllAsync(Callback<CallEventContent[]> callback) {
        callback.call(mockData);
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
