package msgs;

import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.messages.geometry.Pose;
import edu.wpi.rail.jrosbridge.messages.geometry.Twist;
import javax.json.Json;

/**
 * Created by the following students at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 * Janssens Arthur, De Laet Jan & Verhoeven Peter.
 **/
public class ModelState extends Message {
    public static final String FIELD_MODEL_NAME = "model_name";
    public static final String FIELD_POSE = "pose";
    public static final String FIELD_TWIST = "twist";
    public static final String FIELD_REFERENCE_FRAME = "reference_frame";
    public static final String TYPE = "gazebo_msgs/ModelState";

    private /*final*/ String model_name;
    private /*final*/ Pose pose;
    private /*final*/ Twist twist;
    private /*final*/ String reference_frame;

    public ModelState(){
        this("foo", new Pose(), new Twist(), "foo");
    }
    public ModelState(String model_name, Pose pose, Twist twist, String reference_frame){
        super(Json.createObjectBuilder()
                .add("model_name", model_name)
                .add("pose", pose.toJsonObject())
                .add("twist", twist.toJsonObject())
                .add("reference_frame", reference_frame)
                .build(), "gazebo_msgs/ModelState");
        this.model_name=model_name;
        this.pose=pose;
        this.twist=twist;
        this.reference_frame=reference_frame;
    }

    public ModelState clone() {
        return new ModelState(this.model_name, this.pose, this.twist, this.reference_frame);
    }
}

