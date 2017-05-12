package raytrace;

/**
 * Created by arthur on 12.05.17.
 */
public class Hit {

    private double[] position;
    private double time;

    public Hit(double[] position, double time){
        this.position = position;
        this.time = time;
    }

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
