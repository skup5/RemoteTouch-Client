package cz.zelenikr.remotetouch.preferences;

import cz.zelenikr.remotetouch.security.AESCipher;
import cz.zelenikr.remotetouch.security.Hash;
import cz.zelenikr.remotetouch.security.SHAHash;
import cz.zelenikr.remotetouch.security.SymmetricCipher;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

/**
 * Provides encrypted preferences api. All of keys and values are stored encrypted by the specific key.
 *
 * @author Roman Zelenik
 */
public final class EncryptedPreferences extends Preferences {

    private final Preferences preferences;
    private final SymmetricCipher<String> cipher;
    private static final Hash HASH = new SHAHash(false);

    /**
     * See {@link Preferences#userRoot()}.
     *
     * @param key a key used to encrypting preferences
     * @return the root preference node for the calling user
     */
    public static EncryptedPreferences userRoot(@NotNull String key) {
        // createUrlValidator sub node and use key hash like a sub node name
        return new EncryptedPreferences(Preferences.userRoot().node(HASH.hash(key)), key);
    }

    /**
     * See {@link Preferences#userNodeForPackage(Class)}.
     *
     * @param c   the class for whose package a user preference node is desired
     * @param key a key used to encrypting preferences
     * @return the user preference node associated with the package of which {@code c} is a member
     * @throws NullPointerException if {@code c} is null
     */
    public static EncryptedPreferences userNodeForPackage(Class<?> c, @NotNull String key) throws NullPointerException {
        // createUrlValidator sub node and use key hash like a sub node name
        return new EncryptedPreferences(Preferences.userNodeForPackage(c).node(HASH.hash(key)), key);
    }

    @Deprecated
    public static Preferences userRoot() {
        throw new IllegalStateException("Not supported method.");
    }

    @Deprecated
    public static Preferences userNodeForPackage(Class<?> c) {
        throw new IllegalStateException("Not supported method.");
    }

    @Deprecated
    public static Preferences systemRoot() {
        throw new IllegalStateException("Not supported method.");
    }

    @Deprecated
    public static Preferences systemNodeForPackage(Class<?> c) {
        throw new IllegalStateException("Not supported method.");
    }

    @Override
    public void put(String key, String value) {
        if (key == null || value == null) throw new NullPointerException();
        preferences.put(encrypt(key), encrypt(value));
    }

    @Override
    public String get(String key, String def) {
        if (key == null) throw new NullPointerException();
        String value = preferences.get(encrypt(key), null);
        return value == null ? def : decrypt(value);
    }

    @Override
    public void remove(String key) {
        if (key == null) throw new NullPointerException();
        preferences.remove(encrypt(key));
    }

    @Override
    public void clear() throws BackingStoreException {
        preferences.clear();
    }

    @Override
    public void putInt(String key, int value) {
        put(key, Integer.toString(value));
    }

    @Override
    public int getInt(String key, int def) {
        return Integer.parseInt(get(key, Integer.toString(def)));
    }

    @Override
    public void putLong(String key, long value) {
        put(key, Long.toString(value));
    }

    @Override
    public long getLong(String key, long def) {
        return Long.parseLong(get(key, Long.toString(def)));
    }

    @Override
    public void putBoolean(String key, boolean value) {
        put(key, Boolean.toString(value));
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return Boolean.parseBoolean(get(key, Boolean.toString(def)));
    }

    @Override
    public void putFloat(String key, float value) {
        put(key, Float.toString(value));
    }

    @Override
    public float getFloat(String key, float def) {
        return Float.parseFloat(get(key, Float.toString(def)));
    }

    @Override
    public void putDouble(String key, double value) {
        put(key, Double.toString(value));
    }

    @Override
    public double getDouble(String key, double def) {
        return Double.parseDouble(get(key, Double.toString(def)));
    }

    @Override
    public void putByteArray(String key, byte[] value) {
        put(key, Base64.getMimeEncoder().encodeToString(value));
    }

    @Override
    public byte[] getByteArray(String key, byte[] def) {
        String value = get(key, Base64.getMimeEncoder().encodeToString(def));
        return Base64.getMimeDecoder().decode(value);
    }

    @Override
    public String[] keys() throws BackingStoreException {
        String[] keys = preferences.keys();
        for (int i = 0; i < keys.length; i++) {
            keys[i] = decrypt(keys[i]);
        }
        return keys;
    }

    @Override
    public String[] childrenNames() throws BackingStoreException {
        return preferences.childrenNames();
    }

    @Override
    public Preferences parent() {
        return preferences.parent();
    }

    @Override
    public Preferences node(String pathName) {
        return preferences.node(pathName);
    }

    @Override
    public boolean nodeExists(String pathName) throws BackingStoreException {
        return preferences.nodeExists(pathName);
    }

    @Override
    public void removeNode() throws BackingStoreException {
        preferences.removeNode();
    }

    @Override
    public String name() {
        return preferences.name();
    }

    @Override
    public String absolutePath() {
        return preferences.absolutePath();
    }

    @Override
    public boolean isUserNode() {
        return preferences.isUserNode();
    }

    @Override
    public String toString() {
        return preferences.toString();
    }

    @Override
    public void flush() throws BackingStoreException {
        preferences.flush();
    }

    @Override
    public void sync() throws BackingStoreException {
        preferences.sync();
    }

    @Override
    public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
        preferences.addPreferenceChangeListener(pcl);
    }

    @Override
    public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
        preferences.removePreferenceChangeListener(pcl);
    }

    @Override
    public void addNodeChangeListener(NodeChangeListener ncl) {
        preferences.addNodeChangeListener(ncl);
    }

    @Override
    public void removeNodeChangeListener(NodeChangeListener ncl) {
        preferences.removeNodeChangeListener(ncl);
    }

    @Override
    public void exportNode(OutputStream os) throws IOException, BackingStoreException {
        preferences.exportNode(os);
    }

    @Override
    public void exportSubtree(OutputStream os) throws IOException, BackingStoreException {
        preferences.exportSubtree(os);
    }

    private EncryptedPreferences(Preferences preferences, String key) {
        this.preferences = preferences;
        this.cipher = new AESCipher(key);
    }

    /**
     * @param plain the specific text to encryption
     * @return the given encrypted text
     */
    private String encrypt(String plain) {
        return cipher.encrypt(plain);
    }

    /**
     * @param encrypted the specific text to decryption
     * @return the given decrypted text or null on some error
     */
    private String decrypt(String encrypted) {
        return cipher.decrypt(encrypted);
    }
}
