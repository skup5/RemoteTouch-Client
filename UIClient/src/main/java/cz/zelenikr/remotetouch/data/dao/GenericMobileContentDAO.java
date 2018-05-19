package cz.zelenikr.remotetouch.data.dao;

import cz.zelenikr.remotetouch.data.dto.event.EventContent;
import cz.zelenikr.remotetouch.manager.ConnectionManager;

/**
 * Abstract class that simplifies access to a remote mobile content via {@link ConnectionManager}.
 *
 * @author Roman Zelenik
 */
public abstract class GenericMobileContentDAO<T extends EventContent> implements MobileContentDAO<T> {

    protected final ConnectionManager connectionManager = ConnectionManager.getInstance();


}
