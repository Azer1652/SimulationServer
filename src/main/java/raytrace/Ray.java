package raytrace;

import SimServer.Robot;
import edu.wpi.rail.jrosbridge.messages.geometry.Point;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by arthur on 12.05.17.
 */
public class Ray {

    private double direction[];
    private double inv_direction[];
    private Point location;
    private double angle;

    public Ray(){
        this.location = new Point();
        this.direction = new double[]{0,0};
        this.inv_direction = new double[]{0,0};
    }

    public void setLocation(Point location){
        this.location = location;
    }

    public void setDirection(double[] direction){
        this.direction[0] = direction[0];
        this.direction[1] = direction[1];
        updateInv();
    }

    public void setDirection(double x, double y){
        this.direction[0] = x;
        this.direction[1] = y;
        updateInv();
    }

    private void updateInv(){
        this.inv_direction = new double[]{-direction[0],-direction[1]};
    }

    public Hit hit(Robot robot){
        Rectangle2D corners = robot.getCorners();

        double x1 = this.location.getX();
        double y1 = this.location.getY();
        double x2 = this.location.getX()+this.direction[0]*999999;
        double y2 = this.location.getY()+this.direction[1]*999999;

        double x3 = corners.getX();
        double y3 = corners.getY();
        double x4 = corners.getMaxX();
        double y4 = corners.getMaxY();

        double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if (denom == 0.0) { // Lines are parallel.
            return null;
        }
        double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3))/denom;
        double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3))/denom;
        if (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f) {
            // Get the intersection point.
            double[] intersection = new double[]{(x1 + ua*(x2 - x1)),(y1 + ua*(y2 - y1))};
            return new Hit(intersection, Point2D.Double.distance(location.getX(), location.getY(), intersection[0], intersection[1]));
        }

        return null;
    }
}
