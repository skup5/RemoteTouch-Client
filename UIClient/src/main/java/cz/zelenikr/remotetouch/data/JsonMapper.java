package cz.zelenikr.remotetouch.data;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import cz.zelenikr.remotetouch.data.event.*;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Roman Zelenik
 */
public final class JsonMapper {

    private static final Gson GSON = new Gson();

    /**
     * @param json
     * @param objectType
     * @param <T>
     * @return an object of type T from the string. Returns null if json is null or if json is empty.
     */
    public static <T> T fromJsonString(String json, Class<T> objectType) throws JsonSyntaxException {
        return GSON.fromJson(json, objectType);
    }

    /**
     * @param bean An object that has getter methods that should be used to make a JSONObject.
     * @return
     */
    public static JSONObject toJSONObject(Object bean) {
        return new JSONObject(bean);
    }

    public static EventDTO eventFromJson(JSONObject json) {
        String eventTypeStr = json.optString("type");
        JSONObject contentJson = json.optJSONObject("content");
        if (eventTypeStr == null || contentJson == null) {
            return null;
        }
        EventType type = EventType.valueOf(eventTypeStr);
        EventContent content;
        switch (type) {
            case CALL:
                content = fromJsonString(contentJson.toString(), CallEventContent.class);
                break;
            case NOTIFICATION:
                content = fromJsonString(contentJson.toString(), NotificationEventContent.class);
                break;
            case SMS:
                content = fromJsonString(contentJson.toString(), SmsEventContent.class);
                break;
            default:
                content = null;
        }
        if (content == null) {
            return null;
        }

        return new EventDTO(type, content);
    }

    public static EventDTO eventFromJsonString(String json) throws JSONException {
        return eventFromJson(new JSONObject(json));
    }

    private JsonMapper() {
    }
}
