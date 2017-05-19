package raytrace;

import SimServer.Robot;
import edu.wpi.rail.jrosbridge.messages.geometry.Point;

import java.util.List;

/**
 * SimulationServer created by Jan De Laet on 19/05/2017.
 */
public class RayTraceThread implements Runnable
{
    Point carLocation;
    double angle;
    double currentCarAngleRad;
    List<Robot> externalRobots;
    Hit hit = null;

    public RayTraceThread(Point carLocation, double angle, double currentCarAngleRad, List<Robot> externalRobots){
        this.carLocation = carLocation;
        this.angle = angle;
        this.currentCarAngleRad = currentCarAngleRad;
        this.externalRobots = externalRobots;
    }

    @Override
    public void run()
    {
        //todo remove cos and sin by something simpler
        //System.out.print("Ray");
        double dx = Math.cos(angle+currentCarAngleRad);
        double dy = Math.sin(angle+currentCarAngleRad);

        /*if(angle < 0.001 && angle > -0.001)
            System.out.println("break");
*/
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
        this.hit = bestHit;
    }
}
