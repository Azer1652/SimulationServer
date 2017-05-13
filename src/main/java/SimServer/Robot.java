package SimServer;

import edu.wpi.rail.jrosbridge.messages.geometry.*;

import java.awt.geom.Rectangle2D;

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

    public void updateRobot(Pose pose){
        this.pose=pose;
    }

    public void refreshStrings(){
        pose = new Pose(new Point(pose.getPosition().getX(), pose.getPosition().getY(), pose.getPosition().getZ()), new Quaternion(pose.getOrientation().getX(), pose.getOrientation().getY(), pose.getOrientation().getZ(), pose.getOrientation().getW()));
        twist = new Twist(new Vector3(twist.getLinear().getX(), twist.getLinear().getY(), twist.getLinear().getZ()), new Vector3(twist.getAngular().getX(), twist.getAngular().getY(), twist.getAngular().getZ()));
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

    public Rectangle2D getCorners(){
        return new Rectangle2D.Double(this.pose.getPosition().getX()-0.125, this.pose.getPosition().getY()-0.25, 0.25, 0.5);
    }
}
