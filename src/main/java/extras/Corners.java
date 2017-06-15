package extras;

/**
 * Created by arthu on 15/06/2017.
 */
public class Corners {

    //Four corners
    public double[] corner;
    public double[] corner1;
    public double[] corner2;
    public double[] corner3;
    
    public Corners(double x, double y){
        //Four corners
        corner = new double[]{x-0.125, y-0.25};
        corner1 = new double[]{x-0.125, y+0.25};
        corner2 = new double[]{x+0.125, y-0.25};
        corner3 = new double[]{x+0.125, y+0.25};
    }
}
