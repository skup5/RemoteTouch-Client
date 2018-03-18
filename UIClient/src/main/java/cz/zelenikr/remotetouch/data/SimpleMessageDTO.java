package cz.zelenikr.remotetouch.data;

import java.io.Serializable;

/**
 * @author Roman Zelenik
 */
public class SimpleMessageDTO implements Serializable{
    String id = "";
    String msg = "";

    public SimpleMessageDTO() {
    }

    public SimpleMessageDTO(String id, String msg) {
        this.msg = msg;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getMsg() {
        return msg;
    }

}
