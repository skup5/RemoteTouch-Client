package cz.zelenikr.remotetouch.data.dao;


import cz.zelenikr.remotetouch.Callback;

/**
 * @author Roman Zelenik
 */
public interface MobileContentDAO<T> {

    /**
     * Initializes items loading. If it's done callback is called.
     *
     * @param callback it's called (only once) when items was loaded
     */
    void loadAllAsync(Callback<T[]> callback);

    /**
     * The specific {@link Callback} is called every time when new item was received.
     *
     * @param callback the given callback function
     */
    void setOnNewItemCallback(Callback<T> callback);
}
