package raytrace;

import javafx.geometry.Point3D;
import java.util.ArrayList;
import java.util.List;

import static raytrace.RayTracer.angleDiffRad;

/**
 * Created by the following students at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 * Janssens Arthur, De Laet Jan and Verhoeven Peter.
 **/
public class RayTraceThread implements Runnable
{
    Point3D carLocation;
    double angle;
    double currentCarAngleRad;
    int numToTrace;
    int remaining;
    ArrayList<Hit> hit;
    ArrayList<Segment[]> segments;
    ArrayList<Range> ranges;

    /**
     *
     * @param carLocation
     * @param currentCarAngleRad
     * @param segments
     * @param numToTrace
     */
    public RayTraceThread(Point3D carLocation, double currentCarAngleRad, ArrayList<Segment[]> segments, int numToTrace){
        this.carLocation = carLocation;
        this.currentCarAngleRad = currentCarAngleRad;
        this.segments = segments;
        this.numToTrace = numToTrace;
        this.remaining = numToTrace;
        this.hit = new ArrayList<>();
        ranges = new ArrayList<>();
    }

    public boolean full(){
        if(remaining == 0){
            return true;
        }else
            return false;
    }

    public Range fill(Range r){
        if(r.size() == remaining){
            ranges.add(r);
            remaining = 0;
            return null;
        }else if(r.size() < remaining){
            ranges.add(r);
            remaining -= r.size();
            return null;
        }else if(r.size() > remaining){
            ranges.add(new Range(r.start, r.start+remaining));
            Range r2 = new Range(r.start+remaining, r.end);
            remaining =0;
            return r2;
        }
        return null;
    }

    @Override
    //Raytrace given the current values
    public void run()
    {
        for(Range r : ranges){
            for(int i = r.start; i < r.end; i++){
                trace(i);
                angle += angleDiffRad;
            }
        }

    }

    private void trace(int i){
        //todo remove cos and sin by something simpler?
        double angle = RayTracer.angleStartRad+(RayTracer.angleDiffRad*i);
        double dx = Math.cos(angle+currentCarAngleRad);
        double dy = Math.sin(angle+currentCarAngleRad);

        //set direction
        Ray ray = new Ray();
        ray.setLocation(carLocation);
        ray.setDirection(dx, dy);

        //find closest intersection
        Hit bestHit = null;
        for (Segment[] segment1 : segments)
        {
            for (Segment segment : segment1)
            {
                Hit hit = ray.hit(segment);
                if (hit != null)
                {
                    if (bestHit == null || (hit.getTime() > 0 && hit.getTime() < bestHit.getTime()))
                    {
                        bestHit = hit;
                    }
                }
            }
        }
        this.hit.add(bestHit);
    }
}
