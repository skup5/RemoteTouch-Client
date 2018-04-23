package cz.zelenikr.remotetouch;

/**
 * @author Roman Zelenik
 */
public interface Callback<Param> {
    void call(Param param);
}
