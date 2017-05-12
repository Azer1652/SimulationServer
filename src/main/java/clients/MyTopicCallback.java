package clients;

import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import msgs.LaserScan;
import raytrace.RayTracer;

/**
 * Created by arthur on 12.05.17.
 */
public class MyTopicCallback implements TopicCallback {
    RealClient client;

    public MyTopicCallback(RealClient client){
        this.client = client;
    }

    @Override
    public void handleMessage(Message message) {
        //Get laserscan
        LaserScan laserScan = LaserScan.fromMessage(message);
        Topic updatedLaserScan = new Topic(client.ros, "/F1/laser/updatedScan", "sensor_msgs/LaserScan");
        //if more than one external robot
        if(client.externalRobots.size() > 0){
            //Raytrace, modify laserscan
            RayTracer.rayTrace(client, laserScan.getRanges().length);

        }else{
            updatedLaserScan.publish(laserScan);
        }
        //if more than one simulated robot -> raytrace

        //publish changed laserscan
    }
}
