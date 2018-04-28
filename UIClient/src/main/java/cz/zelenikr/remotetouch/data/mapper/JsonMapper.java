package cz.zelenikr.remotetouch.data.mapper;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import cz.zelenikr.remotetouch.data.dto.event.*;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This helper class serves to serializing and deserializing Json objects.
 *
 * @author Roman Zelenik
 */
public final class JsonMapper {

    private static final Gson GSON = new Gson();

    /**
     * This method deserializes the specified Json into an object of the specified class.
     *
     * @param <T>        the type of the desired object
     * @param json       the string from which the object is to be deserialized
     * @param objectType the class of T
     * @return an object of type T from the string. Returns null if json is null or if json is empty
     * @throws JsonSyntaxException if json is not a valid representation for an object of type classOfT
     */
    public static <T> T fromJsonString(String json, Class<T> objectType) throws JsonSyntaxException {
        return GSON.fromJson(json, objectType);
    }

    /**
     * Construct a JSONObject from an Object using bean getters. It reflects on all of the public methods of the object.
     *
     * @param bean an object that has getter methods that should be used to make a JSONObject
     * @return
     */
    public static JSONObject toJSONObject(Object bean) {
        return new JSONObject(bean);
    }

    /**
     * This method deserializes the specified {@link JSONObject} into the {@link EventDTO}. If {@code type} or {@code content}
     * attribute is missing, method returns null. If {@code type} isn't {@link CallEventContent}, {@link NotificationEventContent}
     * or {@link SmsEventContent}, method returns null too.
     *
     * @param json the given object that will be deserialized
     * @return deserialized object or null on some error
     */
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

    /**
     * This method deserializes the specified Json into the {@link EventDTO}.
     *
     * @param json the string from which the EventDTO is to be deserialized
     * @return deserialized object or null on some error
     * @throws JSONException
     */
    public static EventDTO eventFromJsonString(String json) throws JSONException {
        return eventFromJson(new JSONObject(json));
    }

    private JsonMapper() {
    }
}
