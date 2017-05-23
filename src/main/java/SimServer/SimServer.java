package SimServer;

import clients.*;
import edu.wpi.rail.jrosbridge.messages.geometry.Point;
import edu.wpi.rail.jrosbridge.messages.geometry.Pose;
import edu.wpi.rail.jrosbridge.messages.geometry.Quaternion;
import edu.wpi.rail.jrosbridge.messages.geometry.Twist;
import extras.Quat;

/**
 * Created by the following students at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 * Janssens Arthur, De Laet Jan & Verhoeven Peter.
 **/
public class SimServer {

    public static boolean debug = true;

    private ClientReceiver clientReceiver;
    public static RobotHandler robotHandler;

    public SimServer(String[] args) {
        //double[] q = Quat.toEulerianAngle(new Quaternion(0,0,-1, 0.0184));
        //System.out.println();

        //init vars and services
        init();

        //Init Threads
        Thread clientAccepter = new Thread(clientReceiver);
        Thread robotUpdater = new Thread(robotHandler);

        //testRaytracing();

        //Accept new clients and subscribe to topic for robot updates
        clientAccepter.start();
        //Push updates to robots
        robotUpdater.start();

        //ros.disconnect();
    }

    public void testRaytracing(){
        RealClient client = new RealClient("127.0.0.1", 9090, "test");
        client.ownedRobots.add(robotHandler.newRobot("main", new Pose(new Point(0, 0, 0),Quat.toQuaternion(0,0,0)), new Twist()));
        client.externalRobots.add(robotHandler.newRobot("inTheWay", new Pose(new Point(3, 0, 0), Quat.toQuaternion(0,0,90)), new Twist()));
    }

    private void init(){
        //init services
        clientReceiver = new ClientReceiver(this);
        robotHandler = new RobotHandler();
    }

    public void addClient(Client client){
        this.robotHandler.addClient(client);
    }

    public static void main(String[] args) {
        new SimServer(args);
    }
}


