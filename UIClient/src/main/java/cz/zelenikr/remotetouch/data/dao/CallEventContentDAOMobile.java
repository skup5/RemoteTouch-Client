package cz.zelenikr.remotetouch.data.dao;

import cz.zelenikr.remotetouch.Callback;
import cz.zelenikr.remotetouch.data.dto.CallType;
import cz.zelenikr.remotetouch.data.dto.event.CallEventContent;

/**
 * @author Roman Zelenik
 */
public class CallEventContentDAOMobile extends GenericMobileContentDAO<CallEventContent> implements CallEventContentDAO {

    private static CallEventContent[] mockData = {
            new CallEventContent("John Doe", "666 666 666", CallType.MISSED, System.currentTimeMillis()-60*10000),
            new CallEventContent("Jane Doe", "+420 777 666 555", CallType.INCOMING,System.currentTimeMillis()-60*5000),
            new CallEventContent("Jane Doe", "+420 777 666 555", CallType.ONGOING,System.currentTimeMillis()-60*5000),
            new CallEventContent("Jane Doe", "+420 777 666 555", CallType.ENDED,System.currentTimeMillis()-60*3000),
            new CallEventContent("", "963258741", CallType.OUTGOING, System.currentTimeMillis()-30*1000)
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
