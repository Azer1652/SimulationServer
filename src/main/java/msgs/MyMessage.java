package msgs;

import edu.wpi.rail.jrosbridge.JsonWrapper;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;

/**
 * Created by arthur on 10.05.17.
 */
public class MyMessage extends JsonWrapper {
    /**
     * The String representation of an empty message in JSON.
     */
    public static final String EMPTY_MESSAGE = JsonWrapper.EMPTY_JSON;

    private String messageType;

    /**
     * Create a new, empty message. The type will be set to the empty string.
     */
    public MyMessage() {
        this(MyMessage.EMPTY_MESSAGE, "");
    }

    /**
     * Create a MyMessage based on the given String representation of a JSON
     * object. The type will be set to the empty string.
     *
     * @param jsonString
     *            The JSON String to parse.
     */
    public MyMessage(String jsonString) {
        this(jsonString, "");
    }

    /**
     * Create a MyMessage based on the given String representation of a JSON
     * object.
     *
     * @param jsonString
     *            The JSON String to parse.
     * @param messageType
     *            The type of the message (e.g., "geometry_msgs/Twist").
     */
    public MyMessage(String jsonString, String messageType) {
        // parse and pass it to the JSON constructor
        this(Json.createReader(new StringReader(jsonString)).readObject(),
                messageType);
    }

    /**
     * Create a MyMessage based on the given JSON object. The type will be set to
     * the empty string.
     *
     * @param jsonObject
     *            The JSON object containing the message data.
     */
    public MyMessage(JsonObject jsonObject) {
        // setup the JSON information
        this(jsonObject, "");
    }

    /**
     * Create a MyMessage based on the given JSON object.
     *
     * @param jsonObject
     *            The JSON object containing the message data.
     * @param messageType
     *            The type of the message (e.g., "geometry_msgs/Twist").
     */
    public MyMessage(JsonObject jsonObject, String messageType) {
        // setup the JSON information
        super(jsonObject);
        // set the type
        this.messageType = messageType;
    }

    /**
     * Get the type of the message if one was set.
     *
     * @return The type of the message.
     */
    public String getMyMessageType() {
        return this.messageType;
    }

    /**
     * Set the type of the message.
     *
     * @param messageType
     *            The type of the message (e.g., "geometry_msgs/Twist").
     */
    public void setMyMessageType(String messageType) {
        this.messageType = messageType;
    }

    /**
     * Create a clone of this MyMessage.
     */
    @Override
    public MyMessage clone() {
        return new MyMessage(this.toJsonObject(), this.messageType);
    }
}
