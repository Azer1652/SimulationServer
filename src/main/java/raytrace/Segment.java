package raytrace;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arthur on 14.05.17.
 */
public class Segment {

    public double start[];
    public double end[];

    public double position[];
    public double direction[];

    public List<Double[]> vertex;

    public Segment(double[] start, double[] end){
        this.start = start;
        this.end = end;

        this.position = start;
        this.direction = new double[]{end[0]-start[0], end[1]-start[1]};
    }

}
