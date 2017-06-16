package raytrace;

import SimServer.Robot;
import clients.RealClient;
import extras.Corners;
import extras.Quat;
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

    public static long totalTraceTime = 0;
    public static long numTraces = 0;
    public long timeToTrace;
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
        long time = System.currentTimeMillis();
        this.length = length;
        angleDiffRad = Math.toRadians(angleEnd*2)/(length);
        float[] data = new float[length];
        //Allow for one core to be idle
        int cores = Runtime.getRuntime().availableProcessors()-1;
        //int cores = 7;
        Map<RayTraceThread, List<Range>> rayTraceThreadsMap = new HashMap<>();
        List<RayTraceThread> rayTraceThreads = new ArrayList<>();
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

            //get segments to trace against (robot edges) and robot angles (start and end of tracing range)
            ArrayList<Segment[]> segments = new ArrayList<>();
            ArrayList<Range> rangeArrayList = new ArrayList<>();
            for(Robot r : externalRobots)
            {
                segments.add(r.getSegments());
                rangeArrayList.add(robotToAngles(r));
            }

            //Get Final Tracing Ranges
            rangeArrayList = processOverlappingRanges(rangeArrayList);

            //Collections.sort(rangeArrayList);
            int numRangesToTrace = countNumRanges(rangeArrayList);
            int numToTracePerThread = numRangesToTrace/cores;

            //Generate Threads
            //Take from ranges and fill threads untill equally spread
            ListIterator<Range> it = rangeArrayList.listIterator();
            if(it.hasNext()) {
                Range r = it.next();
                for (int m = 0; m < cores; m++) {
                    //thread for core
                    RayTraceThread rayTraceThread = new RayTraceThread(new Point3D(position[0], position[1], position[2]), currentCarAngleRad, segments, numToTracePerThread);

                    while(!rayTraceThread.full()){
                        Range r2 = rayTraceThread.fill(r);
                        if(r2 != null) {
                            it.add(r2);
                            it.previous();
                        }
                        r = it.next();
                    }

                    rayTraceThreads.add(rayTraceThread);
                    //rayTraceThreadsMap.put(rayTraceThread, )

                    //threads.add(new Thread(rayTraceThreads.get()));
                    threads.get(m).start();
                }
            }

            //caclulate remainder
            numToTracePerThread = numRangesToTrace%cores;
            //TODO
            //t.run();
            //hits.addAll(t.hit);

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

        long endTime = System.currentTimeMillis();
        timeToTrace = endTime -time;
        totalTraceTime += timeToTrace;
        numTraces++;

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

    private Range robotToAngles(Robot robot){
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

        int start = values.get(0);
        int end = values.get(3);

        return new Range(start, end);
    }

    public ArrayList<Range> processOverlappingRanges(ArrayList<Range> rangeArrayList){
        ArrayList<Range> returnArray = new ArrayList<>();

        ArrayList<Range> toRemove = new ArrayList<>();

        ListIterator<Range> it = rangeArrayList.listIterator();
        while(it.hasNext()){
            Range r = it.next();
            if(!toRemove.contains(r)){
                int initialStart;
                int start = r.start;
                int initialEnd;
                int end = r.end;
                ArrayList<Range> temp = (ArrayList<Range>) rangeArrayList.clone();

                //Expand
                do {
                    initialStart = start;
                    initialEnd = end;
                    ListIterator<Range> iterator = temp.listIterator();
                    while (iterator.hasNext()) {
                        Range r2 = iterator.next();
                        if (r2.equals(r))
                            if (iterator.hasNext()) {
                                iterator.remove();
                                r2 = iterator.next();
                            }

                        if (r2.start < r.start) {
                            if (r2.end > r.start) {
                                start = r2.start;
                                iterator.remove();
                                toRemove.add(r2);
                                if (r2.end > r.end)
                                    end = r2.end;
                            }
                        } else if (r.start < r2.start) {
                            if (r.end > r2.start) {
                                if (r.end < r2.end) {
                                    iterator.remove();
                                    toRemove.add(r2);
                                    end = r2.end;
                                }
                            }
                        }
                    }
                } while (initialStart != start || initialEnd != end);
                returnArray.add(new Range(start, end));
            }
            it.remove();
        }
        return returnArray;
    }

    private int countNumRanges(List<Range> ranges){
        int num = 0;
        for(Range r : ranges){
            num += r.end-r.start;
        }
        return num;
    }

    public static long getAverageTraceTime(){
        return totalTraceTime/numTraces;
    }

}


