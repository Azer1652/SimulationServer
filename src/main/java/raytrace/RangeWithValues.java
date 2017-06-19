package raytrace;

import java.util.ArrayList;

/**
 * Created by arthu on 16/06/2017.
 */
public class RangeWithValues extends Range {

    public ArrayList<Hit> hits = new ArrayList<>();

    public RangeWithValues(int s, int e, ArrayList<Hit> hits) {
        super(s, e);
        this.hits = hits;
    }
}
