package raytrace;

import SimServer.Robot;
import clients.RealClient;
import edu.wpi.rail.jrosbridge.messages.geometry.Point;
import extras.Quat;
import msgs.LaserScan;

import java.util.List;

/**
 * Created by arthur on 12.05.17.
 */
public class RayTracer {

    //This value is supposed to be divided by -pi/4, but it generates an offset if you don't substract 0.1
    private final static double angleStartRad = Math.toRadians(135); //-0.28;
    //private final static double angleEndRad = Math.PI-angleStartRad;
    private final static double angleDiffRad = Math.toRadians(270)/(1080);

    public static float[] rayTrace(RealClient client, LaserScan laserScan, int length){
        float[] data = new float[length];

        //Get robot pose and direction
        Robot robot = client.robots.get(0);
        //get external robots
        List<Robot> externalRobots = client.externalRobots;

        double current = angleStartRad;
        double currentCarAngleRad = Quat.toEulerianAngle(robot.pose.getOrientation())[2];

        int i = 0;
        while (i < length) {
            //calculate an intersect for each angle
            Hit hit;

            hit = trace(robot.pose.getPosition(), current, currentCarAngleRad, client.externalRobots);

            if(hit != null) {
                if(hit.getTime() < laserScan.getRanges()[i])
                    data[i] = (float) hit.getTime();
            }else{
                data[i] = laserScan.getRanges()[i];
            }
            current -= angleDiffRad;
            i++;
        }

        //return modified array
        return data;
    }

    private static Hit trace(Point carLocation, double angle, double currentCarAngleRad, List<Robot> externalRobots){
        //todo remove cos and sin by something simpler
        double dx = Math.cos(angle+currentCarAngleRad);
        double dy = Math.sin(angle+currentCarAngleRad);

        if(angle < 0.001 && angle > -0.001)
            System.out.println("break");

        //set direction
        Ray ray = new Ray();
        ray.setLocation(carLocation);
        ray.setDirection(dx, dy);

        //find closest intersection
        Hit bestHit = null;
        for(Robot robot: externalRobots){
            for(Segment segment: robot.getSegments()) {
                Hit hit = ray.hit(segment);
                if (hit != null) {
                    if (bestHit == null || (hit.getTime() > 0 && hit.getTime() < bestHit.getTime())) {
                        bestHit = hit;
                    }
                }
            }
        }
        return bestHit;
    }
}
