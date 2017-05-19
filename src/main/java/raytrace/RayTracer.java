package raytrace;

import SimServer.Robot;
import clients.RealClient;
import edu.wpi.rail.jrosbridge.messages.geometry.Point;
import extras.Quat;
import msgs.LaserScan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arthur on 12.05.17.
 */
public class RayTracer{

    //This value is supposed to be divided by -pi/4, but it generates an offset if you don't substract 0.1
    private final static double angleStartRad = Math.toRadians(-135); //-0.28;
    //private final static double angleEndRad = Math.PI-angleStartRad;
    private final static double angleDiffRad = Math.toRadians(270)/(1080);

    public static float[] rayTrace(RealClient client, LaserScan laserScan, int length){
        float[] data = new float[length];
        //System.out.print("tracing");
        int cores = Runtime.getRuntime().availableProcessors();
        ArrayList<RayTraceThread> rayTraceThreads = new ArrayList<>();
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<Hit> hits = new ArrayList<>();


        //Get robot pose and direction
        if(client.ownedRobots.size() != 0)
        {
            Robot robot = client.ownedRobots.get(0);
            //get external robots
            List<Robot> externalRobots;
            synchronized (client.externalRobots)
            {
                externalRobots = client.externalRobots;

                double current = angleStartRad;
                double currentCarAngleRad = Quat.toEulerianAngle(robot.pose.getOrientation())[2];

                int i = 0;
                while (i < length)
                {
                    /*if(i == 1080){
                        System.out.print(i);
                    }*/

                    //System.out.print(i);
                    //calculate an intersect for each angle

                    for(int m = 0; m<cores; m++){
                        rayTraceThreads.add(new RayTraceThread(robot.pose.getPosition(), current, currentCarAngleRad+angleDiffRad*m, externalRobots));
                        threads.add(new Thread(rayTraceThreads.get(m)));
                        threads.get(m).start();
                    }

                    try
                    {
                        for(int j = 0; j< cores; j++){
                            threads.get(j).join();
                            hits.add(rayTraceThreads.get(j).hit);
                        }

                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    for(int k = 0; k<cores; k++){
                        Hit hit = rayTraceThreads.get(k).hit;
                        if (hit != null)
                        {
                            if (hit.getTime() < laserScan.getRanges()[i+k])
                                data[i+k] = (float) hit.getTime();
                        }
                        else
                        {
                            if(i+k+1 <= length)
                                data[i+k] = laserScan.getRanges()[i+k];
                        }
                    }

                    current += angleDiffRad*cores;
                    i += cores;
                }
            }
        }

        //return modified array
        return data;
    }

    private static Hit trace(Point carLocation, double angle, double currentCarAngleRad, List<Robot> externalRobots){
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
        return bestHit;
    }

    // Conversion Angles to X-th ray
    private static int mappingAngle(double angle)
    {
        angle += 3/4*Math.PI;
        angle = angle*180/Math.PI;
        return (int) Math.ceil(angle * 4);
    }
}


