package raytrace;

import javafx.geometry.Point3D;
import java.util.ArrayList;

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
    ArrayList<Hit> hit;
    ArrayList<Segment[]> segments;

    /**
     *
     * @param carLocation
     * @param angle
     * @param currentCarAngleRad
     * @param segments
     * @param numToTrace
     */
    public RayTraceThread(Point3D carLocation, double angle, double currentCarAngleRad, ArrayList<Segment[]> segments, int numToTrace){
        this.carLocation = carLocation;
        this.angle = angle;
        this.currentCarAngleRad = currentCarAngleRad;
        this.segments = segments;
        this.numToTrace = numToTrace;
        this.hit = new ArrayList<>();
    }

    @Override
    //Raytrace given the current values
    public void run()
    {
        for(int i = 0; i < numToTrace; i++){
            trace();
            angle += angleDiffRad;
        }
    }

    private void trace(){
        //todo remove cos and sin by something simpler?
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
