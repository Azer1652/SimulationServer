package msgs;

import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.messages.geometry.Pose;
import edu.wpi.rail.jrosbridge.messages.geometry.Twist;

import javax.json.Json;

/**
 * Created by arthur on 04.05.17.
 */
public class ModelStates extends Message{
    public String[] name;
    public Pose[] pose;
    public Twist[] twist;

    public ModelStates(){}
    public ModelStates(String[] name, Pose[] pose, Twist[] twist){
        this.name = name;
        this.pose = pose;
        this.twist = twist;
    }
}
