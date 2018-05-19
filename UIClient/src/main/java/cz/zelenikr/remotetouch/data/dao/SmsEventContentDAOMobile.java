package cz.zelenikr.remotetouch.data.dao;

import cz.zelenikr.remotetouch.Callback;
import cz.zelenikr.remotetouch.data.dto.event.SmsEventContent;

/**
 * Provides access to a mobile sms stored in remote mobile phone.
 *
 * @author Roman Zelenik
 */
public class SmsEventContentDAOMobile extends GenericMobileContentDAO<SmsEventContent> implements SmsEventContentDAO {

    private Callback<SmsEventContent> newItemCallback;

    public SmsEventContentDAOMobile() {
        super();
        connectionManager.registerSmsReceivedListener(this::onNewSms);
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
