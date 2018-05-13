package cz.zelenikr.remotetouch.data.dto;

/**
 * DTO to authentication purpose.
 *
 * @author Roman Zelenik
 */
public class UserInfo {

    private final String password;

    public UserInfo(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
