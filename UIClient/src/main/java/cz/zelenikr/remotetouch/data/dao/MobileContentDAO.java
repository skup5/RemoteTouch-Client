package cz.zelenikr.remotetouch.data.dao;


import cz.zelenikr.remotetouch.Callback;

/**
 * Provides methods to access to a remote mobile content.
 *
 * @author Roman Zelenik
 */
public interface MobileContentDAO<T> {

    /**
     * The specific {@link Callback} is called every time when new item was received.
     *
     * @param callback the given callback function
     */
    void setOnNewItemCallback(Callback<T> callback);
}
