package clients;

import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
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
        if(client.externalRobots.size() > 0){
            //Raytrace, modify laserscan
            float[] updatedRanges = RayTracer.rayTrace(client, laserScan, laserScan.getRanges().length);
            //publish updated laserscan
            updatedLaserScan.publish(new LaserScan(laserScan.getHeader(), laserScan.getAngle_min(), laserScan.getAngle_max(), laserScan.getAngle_increment(), laserScan.getTime_increment(), laserScan.getScan_time(), laserScan.getRange_min(), laserScan.getRange_max(), updatedRanges, getJsonArrayBuilder(updatedRanges), laserScan.getIntensities(), getJsonArrayBuilder(laserScan.getIntensities())));
        }else{
            updatedLaserScan.publish(laserScan);
        }
    }

    public void trace(){

    }

    private JsonArrayBuilder getJsonArrayBuilder(float[] ranges){
        JsonArrayBuilder jsonRangeBuilder = Json.createArrayBuilder();
        for (float f : ranges){
            jsonRangeBuilder.add(f);
        }
        return jsonRangeBuilder;
    }
}
