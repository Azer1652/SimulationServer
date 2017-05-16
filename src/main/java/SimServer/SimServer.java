package SimServer; /**
 * Created by arthur on 04.05.17.
 */

import clients.*;
import com.google.gson.Gson;
import edu.wpi.rail.jrosbridge.messages.geometry.Point;
import edu.wpi.rail.jrosbridge.messages.geometry.Pose;
import edu.wpi.rail.jrosbridge.messages.geometry.Quaternion;
import edu.wpi.rail.jrosbridge.messages.geometry.Twist;
import edu.wpi.rail.jrosbridge.messages.std.Header;
import extras.Quat;
import msgs.LaserScan;
import raytrace.RayTracer;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

public class SimServer {

    public static boolean debug = true;

    private ClientReceiver clientReceiver;
    public static RobotHandler robotHandler;

    public SimServer(String[] args) {
        //init vars and services
        init();

        //Init Threads
        Thread clientAccepter = new Thread(clientReceiver);
        Thread robotUpdater = new Thread(robotHandler);

        test();

        //testRaytracing();

        //TESTING CLIENTS
        //Client client1 = new RealClient("127.0.0.1", 9090, "helloo");
        //Client client1 = new SimulatedClient("127.0.0.1", 9090);
        //Client client2 = new SimulatedClient("127.0.0.1", 9091);

        //DISABLED FOR TESTING PURPOSES
        //Accept new clients and subscribe to topic for robot updates
        clientAccepter.start();
        //Push updates to robots
        robotUpdater.start();

        //ros.disconnect();
    }

    public void test(){
        System.out.println("First connecting");
        new SimulatedClient("146.175.140.39", 9090);
        System.out.println("Second connecting");
        new SimulatedClient("127.0.0.1", 9090);
    }

    public void testRaytracing(){
        RealClient client = new RealClient("127.0.0.1", 9090, "test");
        client.robots.add(robotHandler.newRobot("main", new Pose(new Point(0, 0, 0), new Quaternion(0,0,0,1)), new Twist()));
        //client.createRobot(robotHandler.newRobot("inTheWay", new Pose(new Point(3, 0, 0), new Quaternion(0,0,-0.367834975507,0.929891085447)), new Twist()));
        client.createRobot(robotHandler.newRobot("inTheWay", new Pose(new Point(3, 0, 0), Quat.toQuaternion(0,0,0)), new Twist()));

        while (true);
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


