package raytrace;

/**
 * Created by arthu on 16/06/2017.
 */
public class Range /* implements Comparable<Range>*/{

    public int start;
    public int end;

    public Range(int s, int e){
        this.start = s;
        this.end = e;
    }

    /*
    @Override
    public int compareTo(Range o) {
        if(this.start == o.start)
            return 0;
        return this.start < o.start ? -1 : 1;
    }
    */
}
