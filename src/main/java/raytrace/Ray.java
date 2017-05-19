package raytrace;

import SimServer.Robot;
import edu.wpi.rail.jrosbridge.messages.geometry.Point;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by arthur on 12.05.17.
 */
public class Ray
{

    private double direction[];
    private Point location;
    private double angle;
    public Segment segment;
    public Hit hit;

    public Ray(){
        this.location = new Point();
        this.direction = new double[]{0,0};
    }

    public void setLocation(Point location){
        this.location = location;
    }

    public void setDirection(double[] direction){
        this.direction[0] = direction[0];
        this.direction[1] = direction[1];
    }

    public void setDirection(double x, double y){
        this.direction[0] = x;
        this.direction[1] = y;
    }

    public Hit hit(Segment segment){
        if(this.direction == segment.direction)
            return null;

        double T2 = (direction[0]*(segment.start[1]-location.getY()) + direction[1]*(location.getX()-segment.start[0]))/(segment.direction[0]*direction[1] - segment.direction[1]*direction[0]);
        double T1 = (segment.start[0]+segment.direction[0]*T2-location.getX())/direction[0];

        // Must be within parametic whatevers for RAY/SEGMENT
        if(T1<0)
            return null;
        if(T2<0 || T2>1.0000000001)
            return null;

        // Return the POINT OF INTERSECTION
        return new Hit(new double[]{location.getX()+direction[0]*T1, location.getY()+direction[1]*T1}, T1);
    }
}
