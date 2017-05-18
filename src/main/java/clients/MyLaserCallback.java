package clients;

import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.messages.std.Header;
import edu.wpi.rail.jrosbridge.messages.std.Time;
import msgs.LaserScan;
import raytrace.RayTracer;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;

/**
 * Created by arthur on 12.05.17.
 */
public class MyLaserCallback implements TopicCallback {
    RealClient client;

    public MyLaserCallback(RealClient client){
        this.client = client;
    }

    @Override
    public void handleMessage(Message message) {
        //Get laserscan
        LaserScan laserScan = LaserScan.fromMessage(message);
        Topic updatedLaserScan = new Topic(client.ros, "/updatedScan", "sensor_msgs/LaserScan");
        //if more than one external robot
        if(client.robots.size() > 0){
            //Raytrace, modify laserscan
            float[] updatedRanges = RayTracer.rayTrace(client, laserScan, laserScan.getRanges().length);
            //publish updated laserscan
            edu.wpi.rail.jrosbridge.primitives.Time time = edu.wpi.rail.jrosbridge.primitives.Time.now();
            Header h = new Header(laserScan.getHeader().getSeq(),time, new String("laser_2"));

            updatedLaserScan.publish(new LaserScan(h, laserScan.getAngle_min(), laserScan.getAngle_max(), laserScan.getAngle_increment(), laserScan.getTime_increment(), laserScan.getScan_time(), laserScan.getRange_min(), laserScan.getRange_max(), updatedRanges, getJsonArrayBuilder(updatedRanges), laserScan.getIntensities(), getJsonArrayBuilder(laserScan.getIntensities())));
        }else{
            edu.wpi.rail.jrosbridge.primitives.Time time = edu.wpi.rail.jrosbridge.primitives.Time.now();
            Header h = new Header(laserScan.getHeader().getSeq(),time, new String("laser_2"));
            updatedLaserScan.publish(new LaserScan(h, laserScan.getAngle_min(), laserScan.getAngle_max(), laserScan.getAngle_increment(), laserScan.getTime_increment(), laserScan.getScan_time(), laserScan.getRange_min(), laserScan.getRange_max(), laserScan.getRanges(), getJsonArrayBuilder(laserScan.getRanges()), laserScan.getIntensities(), getJsonArrayBuilder(laserScan.getIntensities())));

        }
    }

    private JsonArrayBuilder getJsonArrayBuilder(float[] ranges){
        JsonArrayBuilder jsonRangeBuilder = Json.createArrayBuilder();
        for (float f : ranges){
            jsonRangeBuilder.add(f);
        }
        return jsonRangeBuilder;
    }
}
