package raytrace;

import SimServer.Robot;
import edu.wpi.rail.jrosbridge.messages.geometry.Point;

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
        double[] corners = robot.getCorners();

        double t1 = (corners[0] - this.location.getX())*this.inv_direction[0];
        double t2 = (corners[2] - this.location.getX())*this.inv_direction[0];

        double tmin = min(t1, t2);
        double tmax = max(t1, t2);

        t1 = (corners[1] - this.location.getY())*this.inv_direction[1];
        t2 = (corners[3] - this.location.getY())*this.inv_direction[1];

        tmin = max(tmin, min(min(t1, t2), tmax));
        tmax = min(tmax, max(max(t1, t2), tmin));

        if(tmax > max(tmin, 0.0)){
            return new Hit(new double[]{this.location.getX() + tmin*this.direction[0], this.location.getY() + tmin*this.direction[1]}, tmin);
        }
        return null;
    }
}
