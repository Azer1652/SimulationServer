package clients;

import Windows.Plot;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.messages.std.Header;
import msgs.LaserScan;
import raytrace.RayTracer;
import javax.json.Json;
import javax.json.JsonArrayBuilder;

/**
 * Created by the following students at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 * Janssens Arthur, De Laet Jan and Verhoeven Peter.
 *
 * Used by the Real Clients to update the laserScan with the external robots
 **/
public class MyLaserCallback implements TopicCallback {
    RealClient client;

    public MyLaserCallback(RealClient client){
        this.client = client;
    }

    /**
     *
     * @param message
     */
    @Override
    public void handleMessage(Message message) {
        //Get laserscan
        LaserScan laserScan = LaserScan.fromMessage(message);
        Topic updatedLaserScan = new Topic(client.ros, "/updatedScan", "sensor_msgs/LaserScan");
        edu.wpi.rail.jrosbridge.primitives.Time time;
        Header h;

        RayTracer rayTracer = new RayTracer();
        //if more than one external robot && at least one Owned Robot
        synchronized (client.externalRobots) {
            if (client.externalRobots.size() > 0 && client.ownedRobots.size() > 0) {
                //Raytrace, modify laserscan
                float[] updatedRanges = rayTracer.rayTrace(client, laserScan, laserScan.getRanges().length);
                Plot.update(rayTracer.time);
                time = edu.wpi.rail.jrosbridge.primitives.Time.now();
                h = new Header(laserScan.getHeader().getSeq(), time, new String("laser_2"));

                //publish updated laserscan
                updatedLaserScan.publish(new LaserScan(h, laserScan.getAngle_min(), laserScan.getAngle_max(), laserScan.getAngle_increment(), laserScan.getTime_increment(), laserScan.getScan_time(), laserScan.getRange_min(), laserScan.getRange_max(), updatedRanges, getJsonArrayBuilder(updatedRanges), laserScan.getIntensities(), getJsonArrayBuilder(laserScan.getIntensities())));
            } else {
                time = edu.wpi.rail.jrosbridge.primitives.Time.now();
                h = new Header(laserScan.getHeader().getSeq(), time, new String("laser_2"));

                //publish unmodified laserscan
                updatedLaserScan.publish(new LaserScan(h, laserScan.getAngle_min(), laserScan.getAngle_max(), laserScan.getAngle_increment(), laserScan.getTime_increment(), laserScan.getScan_time(), laserScan.getRange_min(), laserScan.getRange_max(), laserScan.getRanges(), getJsonArrayBuilder(laserScan.getRanges()), laserScan.getIntensities(), getJsonArrayBuilder(laserScan.getIntensities())));

            }
        }
    }

    /**
     *
     * @param ranges
     * @return
     */
    private JsonArrayBuilder getJsonArrayBuilder(float[] ranges){
        JsonArrayBuilder jsonRangeBuilder = Json.createArrayBuilder();
        for (float f : ranges){
            jsonRangeBuilder.add(f);
        }
        return jsonRangeBuilder;
    }
}
