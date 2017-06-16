package raytrace;

import SimServer.Robot;
import clients.RealClient;
import extras.Corners;
import extras.Quat;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Point3D;
import msgs.LaserScan;

import java.util.*;

/**
 * Created by the following students at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 * Janssens Arthur, De Laet Jan and Verhoeven Peter.
 *
 * The Raytracer raytraces a scene by creating and managing threads performing the raytracing
 *
 * Only works with real clients
 **/
public class RayTracer{

    public final static double angleStart = -135;
    public final static double angleEnd = 135;
    public final static double angleStartRad = Math.toRadians(angleStart); //-0.28;
    //private final static double angleEndRad = Math.PI-angleStartRad;
    public static double angleDiffRad;

    private int length;

    /**
     * Start a new Raytrace manager
     * @param client
     * @param laserScan
     * @param length
     * @return
     */
    public float[] rayTrace(RealClient client, LaserScan laserScan, int length){
        this.length = length;
        angleDiffRad = Math.toRadians(angleEnd*2)/(length);
        float[] data = new float[length];
        //Allow for one core to be idle
        int cores = Runtime.getRuntime().availableProcessors()-1;
        //int cores = 7;
        ArrayList<RayTraceThread> rayTraceThreads = new ArrayList<>();
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<Hit> hits = new ArrayList<>();

        //Get the robot to be traced from
        Robot robot = client.ownedRobots.get(0);
        double position[] =  new double[]{robot.pose.getPosition().getX(),robot.pose.getPosition().getY(),robot.pose.getPosition().getZ()};
        float[] ranges = laserScan.getRanges();

        //get external robots
        List<Robot> externalRobots;
        synchronized (client.externalRobots)
        {
            externalRobots = client.externalRobots;

            double current = angleStartRad;
            double currentCarAngleRad = Quat.toEulerianAngle(robot.pose.getOrientation())[2];

            //segments to trace against (robot edges)
            ArrayList<Segment[]> segments = new ArrayList<>();
            for(Robot r : externalRobots)
            {
                robotToAngles(r, position);
                segments.add(r.getSegments());
            }

            int numToTrace = length/cores;

            //Generate Threads
            for(int m = 0; m<cores; m++)
            {
                rayTraceThreads.add(new RayTraceThread(new Point3D(position[0],position[1],position[2]), current+(angleDiffRad*numToTrace)*(m), currentCarAngleRad, segments, numToTrace));
                threads.add(new Thread(rayTraceThreads.get(m)));
                threads.get(m).start();
            }

            //Wait for threads
            try
            {
                for(int j = 0; j< cores; j++){
                    threads.get(j).join();
                    hits.addAll(rayTraceThreads.get(j).hit);
                }

            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            //caclulate remainder
            numToTrace = length%cores;
            RayTraceThread t = new RayTraceThread(new Point3D(position[0],position[1],position[2]), current+(angleDiffRad*numToTrace)*(cores), currentCarAngleRad, segments, numToTrace);
            t.run();
            hits.addAll(t.hit);

            //Update data
            Iterator<Hit> hitIterator = hits.iterator();
            for(int i = 0; i < length; i++) // length = amount of rays (1080)
            {
                //Update hits
                Hit hit = hitIterator.next();
                if (hit != null)
                {
                    if (hit.getTime() < ranges[i])
                        data[i] = (float) hit.getTime();
                }
                else
                {
                    data[i] = ranges[i];
                }
            }
        }

        //return modified array
        return data;
    }

    // Conversion Angles to X-th ray

    /**
     *
     * @param angle in radians
     * @return value between
     */
    private int mappingAngle(double angle)
    {
        //get in degrees and make positive
        angle += 3.0/4.0*Math.PI;
        angle = angle*180/Math.PI;

        return (int) Math.floor(angle/(angleEnd*2)*(this.length-1));
    }

    private double[] robotToAngles(Robot robot, double[] position){
        Corners c = robot.getCorners();

        List<Integer> values = new ArrayList<>();

        //testing
        //values.add(mappingAngle(angleStartRad)); //0
        //values.add(mappingAngle(0)); //n/2
        //values.add(mappingAngle(-angleStartRad)); //n

        //four corner values
        values.add(mappingAngle(Math.atan2(c.corner[1], c.corner[0])));
        values.add(mappingAngle(Math.atan2(c.corner1[1], c.corner1[0])));
        values.add(mappingAngle(Math.atan2(c.corner2[1], c.corner2[0])));
        values.add(mappingAngle(Math.atan2(c.corner3[1], c.corner3[0])));

        Collections.sort(values);

        double start = values.get(0);
        double end = values.get(3);

        return new double[]{start, end};
    }

}


