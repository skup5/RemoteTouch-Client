package cz.zelenikr.remotetouch.manager;

import cz.zelenikr.remotetouch.data.dto.UserInfo;
import cz.zelenikr.remotetouch.security.Hash;
import cz.zelenikr.remotetouch.security.SHAHash;
import org.jetbrains.annotations.NotNull;

import java.util.prefs.Preferences;

/**
 * This manager is providing authentication, etc.
 *
 * @author Roman Zelenik
 */
public final class SecurityManager {

    private static final Hash HASH = new SHAHash(false);
    private static final String PREF_PASSWD = HASH.hash("user_passwd");
    private static final SecurityManager ourInstance = new SecurityManager();

    public static SecurityManager getInstance() {
        return ourInstance;
    }

    private final Preferences preferences;

    /**
     * The application owner, who is granted access.
     */
    private UserInfo owner;

    /**
     * Checks if the specific {@code user} is the owner.
     *
     * @param user the given user to authentication
     * @return if this {@code user} is owner
     */
    public boolean authenticateOwner(@NotNull UserInfo user) {
        return existOwner() && owner.getPassword().equals(HASH.hash(user.getPassword()));
    }

    /**
     * Stores the specific {@code user} like the new owner.
     *
     * @param user the given new owner
     * @throws NullPointerException if a user password is {@code null}
     */
    public void createOwner(@NotNull UserInfo user) throws NullPointerException {
        if (user.getPassword() == null) throw new NullPointerException("Password cannot be null");

        storeOwner(user);
        owner = loadOwner();
    }

    public boolean existOwner() {
        return owner != null;
    }

    /**
     * Deletes current owner.
     */
    public void resetOwner() {
        owner = null;
        storeOwner(owner);
    }

    /**
     * @return stored owner or {@code null} if any is not stored
     */
    private UserInfo loadOwner() {
        String passwd = preferences.get(PREF_PASSWD, null);
        return passwd == null ? null : new UserInfo(passwd);
    }

    /**
     * Stores the specific {@code user} like the owner. If {@code user} is null, deletes current owner.
     *
     * @param user the given new owner
     */
    private void storeOwner(UserInfo user) {
        if (user == null) {
            preferences.remove(PREF_PASSWD);
        } else {
            if (user.getPassword() != null) {
                preferences.put(PREF_PASSWD, HASH.hash(user.getPassword()));
            }
        }
    }

    private SecurityManager() {
        preferences = Preferences.userNodeForPackage(this.getClass());
        owner = loadOwner();
    }

}
