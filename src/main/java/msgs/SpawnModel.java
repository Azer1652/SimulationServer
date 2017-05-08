package msgs;

import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.messages.geometry.Pose;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;
import edu.wpi.rail.jrosbridge.services.std.Empty;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by arthur on 05.05.17.
 */
public class SpawnModel extends ServiceRequest{
    public static final String TYPE = "gazebo_msgs/SpawnModel";

    public String model_name;
    public String model_xml;
    public String robot_namespace;
    public Pose initial_pose;
    public String reference_frame;

    public SpawnModel() {
    }

    public SpawnModel(String model_name, String model_xml, String robot_namespace, Pose initial_pose, String reference_frame) {
        super(Json.createObjectBuilder().add("model_name", model_name).add("model_xml", model_xml).add("robot_namespace", robot_namespace).add("initial_pose", initial_pose.toJsonObject()).add("reference_frame", reference_frame).build(), "gazebo_msgs/SpawnModel");
        this.model_name = model_name;
        this.model_xml = model_xml;
        this.robot_namespace = robot_namespace;
        this.initial_pose = initial_pose;
        this.reference_frame = reference_frame;
    }

    public SpawnModel clone() {
        return new SpawnModel(model_name, model_xml, robot_namespace, initial_pose, reference_frame);
    }

    /*
    public static SpawnModel fromJsonString(String jsonString) {
        // convert to a ServiceRequest
        return SpawnModel.fromServiceRequest(new ServiceRequest(
                jsonString));
    }

    public static SpawnModel fromServiceRequest(ServiceRequest req) {
        // get it from the JSON object
        return SpawnModel.fromJsonObject(req.toJsonObject());
    }

    public static SpawnModel fromJsonObject(JsonObject jsonObject) {
        return new SpawnModel();
    }
    */
}
