package clients;

import SimServer.Robot;
import SimServer.SimServer;
import com.google.gson.Gson;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.messages.geometry.PoseWithCovarianceStamped;
import edu.wpi.rail.jrosbridge.messages.geometry.Twist;
import msgs.LaserScan;
import msgs.ModelStates;
import raytrace.RayTracer;

import javax.json.JsonObject;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arthur on 10.05.17.
 */
public class RealClient extends Client{

    String robotName;
    public List<Robot> externalRobots = new ArrayList<Robot>();
    boolean created = false;

    public RealClient(InetAddress ip, int port, String robotName){
        super(ip, port);
        this.robotName = robotName;
        init();
    }

    public void updateRobots() {
        Topic echoBack = new Topic(ros, "/amcl_pose", "geometry_msgs/PoseWithCovarianceStamped", 100);
        echoBack.subscribe(new TopicCallback() {
            //@Override
            public void handleMessage(Message message) {
                PoseWithCovarianceStamped pose = PoseWithCovarianceStamped.fromMessage(message);

                if (!created) {
                    robots.add(SimServer.robotHandler.newRobot(robotName, pose.getPose().getPose(), new Twist()));
                    created = true;
                } else {
                    //Update robots already tracked
                    robots.get(0).updateRobot(pose.getPose().getPose());
                }
            }
        });

        Topic laserScan = new Topic(ros, "/F1/laser/scan", "sensor_msgs/LaserScan", 100);
        laserScan.subscribe(new MyTopicCallback(this));
    }

    public void updateRobot(Robot robot) {

    }

    public void deleteRobot(Robot robot) {

    }

    public void createRobot(Robot robot) {

    }
}
