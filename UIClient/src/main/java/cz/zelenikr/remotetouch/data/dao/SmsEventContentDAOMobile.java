package cz.zelenikr.remotetouch.data.dao;

import cz.zelenikr.remotetouch.Callback;
import cz.zelenikr.remotetouch.data.dto.event.SmsEventContent;

/**
 * @author Roman Zelenik
 */
public class SmsEventContentDAOMobile extends GenericMobileContentDAO<SmsEventContent> implements SmsEventContentDAO {

    private static SmsEventContent[] mockData = {
            new SmsEventContent("John Doe", "666 666 666", "Sorry, i'm late.", System.currentTimeMillis() - 60 * 5000),
            new SmsEventContent("", "+420 666 888 333", "Let kombinézy 2002 metropole obavy floridě specialistkou současné. Městě však pozdější najisto cestujete. I nakrásně cítit na současném nebezpečná tábory i počátku v splní.", System.currentTimeMillis() - 60 * 1000)
    };

    private Callback<SmsEventContent> newItemCallback;

    public SmsEventContentDAOMobile() {
        super();
        connectionManager.registerSmsReceivedListener(this::onNewSms);
    }

    @Override
    public void loadAllAsync(Callback<SmsEventContent[]> callback) {
        callback.call(mockData);
    }

    @Override
    public void setOnNewItemCallback(Callback<SmsEventContent> callback) {
        newItemCallback = callback;
    }

    private void onNewSms(SmsEventContent... smsEventContents) {
        if (newItemCallback != null) {
            for (SmsEventContent content : smsEventContents) {
                newItemCallback.call(content);
            }
        }
    }
}
