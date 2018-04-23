package cz.zelenikr.remotetouch.data.dao;

import cz.zelenikr.remotetouch.data.event.EventContent;
import cz.zelenikr.remotetouch.manager.ConnectionManager;

/**
 * @author Roman Zelenik
 */
public abstract class GenericMobileContentDAO<T extends EventContent> implements MobileContentDAO<T> {

    protected final ConnectionManager connectionManager = ConnectionManager.getInstance();


}
