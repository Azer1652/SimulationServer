package msgs;

import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.messages.geometry.Pose;
import edu.wpi.rail.jrosbridge.messages.geometry.Twist;

import javax.json.Json;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arthur on 04.05.17.
 */
public class ModelStates{

    public String[] name;
    public List<Pose> pose;
    public List<Twist> twist;

    public ModelStates(){
        pose = new ArrayList<Pose>();
        twist = new ArrayList<Twist>();
    }
    public ModelStates(String[] name, List<Pose> pose, List<Twist> twist){
        this.name = name;
        this.pose = pose;
        this.twist = twist;
    }
}
