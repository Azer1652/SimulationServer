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
    public List<Robot> internalRobot = Collections.synchronizedList(new ArrayList<Robot>());
    boolean created = false;

    public RealClient(String ip, int port, String robotName){
        super(ip, port);
        this.robotName = robotName;
        init();
    }

    public void updateRobots() {
        Topic echoBack = new Topic(ros, "/amcl_pose", "geometry_msgs/PoseWithCovarianceStamped");
        echoBack.subscribe(new TopicCallback() {
            //@Override
            public void handleMessage(Message message) {
                PoseWithCovarianceStamped pose = PoseWithCovarianceStamped.fromMessage(message);
                synchronized (internalRobot) {
                    if (!created) {
                        internalRobot.add(SimServer.robotHandler.newRobot(robotName, pose.getPose().getPose(), new Twist()));
                        created = true;
                    } else {
                        //Update robots already tracked
                        internalRobot.get(0).updateRobot(pose.getPose().getPose());
                    }
                }
            }
        });

        //TODO update to scan topic
        Topic laserScan = new Topic(ros, "/scan", "sensor_msgs/LaserScan");
        //Topic laserScan = new Topic(ros, "/laser", "sensor_msgs/LaserScan", 100);
        laserScan.subscribe(new MyLaserCallback(this));
    }

    public void updateRobot(Robot robot) {
        //Update robots already tracked
        synchronized (robots) {
            //System.out.print("Updating");
            if(robots.indexOf(new Robot(robot.model_name)) != -1)
                robots.get(robots.indexOf(new Robot(robot.model_name))).updateRobot(robot.pose, robot.twist);
        }
    }

    public void deleteRobot(Robot robot) {
        synchronized (robots) {
            robots.remove(robots.indexOf(new Robot(robot.model_name)));
        }
    }

    public void createRobot(Robot robot) {
        synchronized (robots){
            //System.out.print("creating");
            robots.add(robot);
        }
    }
}
