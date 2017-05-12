package raytrace;

import SimServer.Robot;
import clients.Client;
import clients.RealClient;
import edu.wpi.rail.jrosbridge.messages.geometry.Point;
import edu.wpi.rail.jrosbridge.messages.geometry.Pose;
import edu.wpi.rail.jrosbridge.messages.geometry.Quaternion;
import msgs.LaserScan;

import java.util.List;

/**
 * Created by arthur on 12.05.17.
 */
public class RayTracer {

    //This value is supposed to be divided by -pi/4, but it generates an offset if you don't substract 0.1
    private final static double angleStartRad = +Math.toRadians(135); //-0.28;
    //private final static double angleEndRad = Math.PI-angleStartRad;
    private final static double angleDiffRad = Math.toRadians(270)/(1080);

    public static float[] rayTrace(RealClient client, int length){
        float[] data = new float[length];

        //Get robot pose and direction
        Robot robot = client.robots.get(0);
        //get external robots
        List<Robot> externalRobots = client.externalRobots;

        double current = angleStartRad;
        double currentCarAngleRad = calcCurrentCarAngleYaw(robot.pose.getOrientation());

        int i = 0;
        while (i < length) {
            //calculate an intersect for each angle
            Hit hit;

            hit = trace(robot.pose.getPosition(), current, currentCarAngleRad, client.externalRobots);

            data[i]=(float) hit.getTime();
            current -= angleDiffRad;
            i++;
        }

        return null;
    }

    private static Hit trace(Point carLocation, double angle, double currentCarAngleRad, List<Robot> externalRobots){
        //todo remove cos and sin by something simpler
        double dx = Math.cos(angle+currentCarAngleRad);
        double dy = Math.sin(angle+currentCarAngleRad);

        //set direction
        Ray ray = new Ray();
        ray.setLocation(carLocation);
        ray.setDirection(dx, dy);

        //find closest intersection
        Hit bestHit = null;
        for(Robot robot: externalRobots){
            Hit hit = ray.hit(robot);
            if(hit != null) {
                if (bestHit == null || (hit.getTime() > 0 && hit.getTime() < bestHit.getTime())) {
                    bestHit = hit;
                }
            }
        }
        return bestHit;
    }

    private static double calcCurrentCarAngleYaw(Quaternion q){
        double euler;

        double ysqr = q.getY() * q.getY();

        // yaw (z-axis rotation)
        double t3 = +2.0 * (q.getW() * q.getZ() + q.getX() * q.getY());
        double t4 = +1.0 - 2.0 * (ysqr + q.getZ() * q.getZ());
        //yaw
        euler = Math.atan2(t3, t4);

        return euler;
    }

    private static double[] calcCurrentCarAngle(Quaternion q){
        double[] euler = new double[3];

        double ysqr = q.getY() * q.getY();

        // roll (x-axis rotation)
        double t0 = +2.0 * (q.getW() * q.getX() + q.getY() * q.getZ());
        double t1 = +1.0 - 2.0 * (q.getX() * q.getX() + ysqr);
        //roll
        euler[0] = Math.atan2(t0, t1);

        // pitch (y-axis rotation)
        double t2 = +2.0 * (q.getW() * q.getY() - q.getZ() * q.getX());
        t2 = t2 > 1.0 ? 1.0 : t2;
        t2 = t2 < -1.0 ? -1.0 : t2;
        //pitch
        euler[1] = Math.asin(t2);

        // yaw (z-axis rotation)
        double t3 = +2.0 * (q.getW() * q.getZ() + q.getX() * q.getY());
        double t4 = +1.0 - 2.0 * (ysqr + q.getZ() * q.getZ());
        //yaw
        euler[2] = Math.atan2(t3, t4);

        return euler;
    }
}
