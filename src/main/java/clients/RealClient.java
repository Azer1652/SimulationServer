package clients;

import SimServer.Robot;
import SimServer.SimServer;
import com.google.gson.Gson;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.messages.geometry.PoseWithCovarianceStamped;
import edu.wpi.rail.jrosbridge.messages.geometry.Twist;
import msgs.ModelStates;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arthur on 10.05.17.
 */
public class RealClient extends Client{

    String robotName;
    boolean created = false;

    public RealClient(String ip, int port, String robotName){
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
    }
}
