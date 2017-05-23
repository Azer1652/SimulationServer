package clients;

import SimServer.SimServer;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.messages.geometry.PoseWithCovarianceStamped;
import edu.wpi.rail.jrosbridge.messages.geometry.Twist;

/**
 * Created by the following students at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 * Janssens Arthur, De Laet Jan & Verhoeven Peter.
 **/
public class RealClient extends Client{

    String robotName;
    boolean created;

    public RealClient(String ip, int port, String robotName){
        super(ip, port);
        this.robotName = robotName;
        init();
        //Test robot
        //ownedRobots.add(SimServer.robotHandler.newRobot(robotName, new Pose(), new Twist()));
    }

    public void updateOwnedRobots() {
        Topic echoBack = new Topic(ros, "/amcl_pose", "geometry_msgs/PoseWithCovarianceStamped");
        echoBack.subscribe(new TopicCallback() {
            //@Override
            public void handleMessage(Message message) {
                PoseWithCovarianceStamped pose = PoseWithCovarianceStamped.fromMessage(message);
                synchronized (ownedRobots) {
                    if (!created) {
                        ownedRobots.add(SimServer.robotHandler.newRobot(robotName, pose.getPose().getPose(), new Twist()));
                        created = true;
                    } else {
                        //Update robots already tracked
                        ownedRobots.get(0).updateRobot(pose.getPose().getPose());
                    }
                }
            }
        });

        Topic laserScan = new Topic(ros, "/scan", "sensor_msgs/LaserScan", 90);
        //Topic laserScan = new Topic(ros, "/laser", "sensor_msgs/LaserScan");
        laserScan.subscribe(new MyLaserCallback(this));
    }

    public void drawExternalRobots() {
        return;
    }
}
