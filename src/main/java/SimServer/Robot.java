package SimServer;

import edu.wpi.rail.jrosbridge.messages.geometry.*;
import extras.Quat;
import raytrace.Segment;
import sun.security.provider.certpath.Vertex;

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

    public Segment[] getSegments(){
        double[] corner = new double[]{this.pose.getPosition().getX()-0.125, this.pose.getPosition().getY()-0.25, this.pose.getPosition().getZ()};
        double[] corner1 = new double[]{this.pose.getPosition().getX()-0.125, this.pose.getPosition().getY()+0.25, this.pose.getPosition().getZ()};
        double[] corner2 = new double[]{this.pose.getPosition().getX()+0.125, this.pose.getPosition().getY()-0.25, this.pose.getPosition().getZ()};
        double[] corner3 = new double[]{this.pose.getPosition().getX()+0.125, this.pose.getPosition().getY()+0.25, this.pose.getPosition().getZ()};

        double[] newcorner = rotationAround3DEuler(pose.getPosition(), corner, Quat.toEulerianAngle(this.pose.getOrientation()));
        double[] newcorner1 = rotationAround3DEuler(pose.getPosition(), corner1, Quat.toEulerianAngle(this.pose.getOrientation()));
        double[] newcorner2 = rotationAround3DEuler(pose.getPosition(), corner2, Quat.toEulerianAngle(this.pose.getOrientation()));
        double[] newcorner3 = rotationAround3DEuler(pose.getPosition(), corner3, Quat.toEulerianAngle(this.pose.getOrientation()));

        /*
        double[] newcorner = rotation3D(corner, this.pose.getOrientation());
        double[] newcorner1 = rotation3D(corner1, this.pose.getOrientation());
        double[] newcorner2 = rotation3D(corner2, this.pose.getOrientation());
        double[] newcorner3 = rotation3D(corner3, this.pose.getOrientation());
        */

        Segment s = new Segment(new double[]{newcorner[0], newcorner[1]}, new double[]{newcorner1[0], newcorner1[1]});
        Segment s1 = new Segment(new double[]{newcorner1[0], newcorner1[1]}, new double[]{newcorner3[0], newcorner3[1]});
        Segment s2 = new Segment(new double[]{newcorner3[0], newcorner3[1]}, new double[]{newcorner2[0], newcorner2[1]});
        Segment s3 = new Segment(new double[]{newcorner2[0], newcorner2[1]}, new double[]{newcorner[0], newcorner[1]});

        return new Segment[]{s,s1,s2,s3};
    }

    //doesnt work
    private double[] rotation3D(double[] points, Quaternion orientation){
        double[] newPoints = new double[3];

        double x_old = points[0];
        double y_old = points[1];
        double z_old = points[2];

        double w = Math.cos(orientation.getW()/2.0);
        double x = orientation.getX()*Math.sin(orientation.getW()/2.0);
        double y = orientation.getY()*Math.sin(orientation.getW()/2.0);
        double z = orientation.getZ()*Math.sin(orientation.getW()/2.0);

        double x_new = (1 - 2*y*y -2*z*z)*x_old + (2*x*y + 2*w*z)*y_old + (2*x*z-2*w*y)*z_old;
        double y_new = (2*x*y - 2*w*z)*x_old + (1 - 2*x*x - 2*z*z)*y_old + (2*y*z + 2*w*x)*z_old;
        double z_new = (2*x*z + 2*w*y)*x_old + (2*y*z - 2*w*x)*y_old + (1 - 2*x*x - 2*y*y)*z_old;

        newPoints[0] = x_new;
        newPoints[1] = y_new;
        newPoints[2] = z_new;

        return newPoints;
    }

    private double[] rotationAround3DEuler(Point center, double[] point, double[] angles){
        double[] newPoints = new double[3];

        newPoints[0] = center.getX() + (point[0]-center.getX())*Math.cos(angles[2]) - (point[1]-center.getY())*Math.sin(angles[2]);

        newPoints[1] = center.getY() + (point[0]-center.getX())*Math.sin(angles[2]) + (point[1]-center.getY())*Math.cos(angles[2]);
        newPoints[2] = point[2];

        return newPoints;
    }


}
