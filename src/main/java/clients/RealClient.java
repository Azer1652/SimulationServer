package clients;

import SimServer.Robot;
import SimServer.SimServer;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.messages.geometry.PoseWithCovarianceStamped;
import edu.wpi.rail.jrosbridge.messages.geometry.Twist;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by arthur on 10.05.17.
 */
public class RealClient extends Client{

    String robotName;
    public List<Robot> externalRobots = Collections.synchronizedList(new ArrayList<Robot>());
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
                synchronized (robots) {
                    if (!created) {
                        robots.add(SimServer.robotHandler.newRobot(robotName, pose.getPose().getPose(), new Twist()));
                        created = true;
                    } else {
                        //Update robots already tracked
                        robots.get(0).updateRobot(pose.getPose().getPose());
                    }
                }
            }
        });

        Topic laserScan = new Topic(ros, "/F1/laser/scan", "sensor_msgs/LaserScan", 100);
        laserScan.subscribe(new MyLaserCallback(this));
    }

    public void updateRobot(Robot robot) {
        //Update robots already tracked
        synchronized (externalRobots) {
            externalRobots.get(robots.indexOf(new Robot(robot.model_name))).updateRobot(robot.pose, robot.twist);
        }
    }

    public void deleteRobot(Robot robot) {
        synchronized (externalRobots) {
            externalRobots.remove(robots.indexOf(new Robot(robot.model_name)));
        }
    }

    public void createRobot(Robot robot) {
        synchronized (externalRobots){
            externalRobots.add(robot);
        }
    }
}
