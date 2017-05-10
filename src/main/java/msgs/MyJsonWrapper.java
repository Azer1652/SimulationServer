package msgs;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;

/**
 * Created by arthur on 10.05.17.
 */
abstract public class MyJsonWrapper {

    /**
     * The String representation of an empty message in JSON.
     */
    public static final String EMPTY_JSON = "{}";

    private final JsonObject jsonObject;
    private final String jsonString;

    /**
     * Create a new, empty JSON object.
     */
    public MyJsonWrapper() {
        this(MyJsonWrapper.EMPTY_JSON);
    }

    /**
     * Create a JSON object based on the given String representation of a JSON
     * object.
     *
     * @param jsonString
     *            The JSON String to parse.
     */
    public MyJsonWrapper(String jsonString) {
        // parse and pass it to the JSON constructor
        this(Json.createReader(new StringReader(jsonString)).readObject());
    }

    /**
     * Create a Message based on the given JSON object.
     *
     * @param jsonObject
     *            The JSON object containing the message data.
     */
    public MyJsonWrapper(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
        // only need to do this once
        this.jsonString = this.jsonObject.toString();
    }

    /**
     * Get the JSON object.
     *
     * @return The JSON object.
     */
    public JsonObject toJsonObject() {
        return this.jsonObject;
    }

    /**
     * Get the String representation of this JSON object in JSON format.
     *
     * @return The String representation of this JSON object in JSON format.
     */
    @Override
    public String toString() {
        return this.jsonString;
    }

    /**
     * Create a clone of this JSON object.
     */
    public abstract MyJsonWrapper clone();

    /**
     * Return the hash code of this JSON object, which is the hash code of the
     * JSON string.
     *
     * @return The hash code of the message.
     */
    @Override
    public int hashCode() {
        return this.jsonString.hashCode();
    }

    /**
     * Test if the given Object is equal to this MyJsonWrapper. Two MyJsonWrappers
     * are equal if and only if their JSON strings match.
     *
     * @param o
     *            The Object to test equality with.
     * @return If the given Object is equal to this MyJsonWrapper.
     */
    @Override
    public boolean equals(Object o) {
        return o == this
                || (o instanceof MyJsonWrapper && this.jsonString.equals(o.toString()));

    }
}
