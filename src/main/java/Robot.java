import edu.wpi.rail.jrosbridge.messages.geometry.Pose;
import edu.wpi.rail.jrosbridge.messages.geometry.Twist;

/**
 * Created by arthur on 06.05.17.
 */
public class Robot {

    public long id;
    public String model_name;
    public Pose pose;
    public Twist twist;

    public boolean created = false;

    public Robot(long id, String model_name, Pose pose, Twist twist){
        this.id=id;
        this.model_name=model_name;
        this.pose=pose;
        this.twist=twist;
    }

    public Robot(String str){
        this.model_name = str;
    }

    public void updateRobot(Pose pose, Twist twist){
        this.pose=pose;
        this.twist=twist;
    }

    @Override
    public boolean equals(Object o){
        if(o.getClass() == this.getClass()){
            if(((Robot) o).model_name.equals(this.model_name))
                return true;
            else
                return false;
        }else if(o.getClass() == String.class){
            if(((String) o) == this.model_name)
                return true;
            else
                return false;
        }
        return false;
    }
}
