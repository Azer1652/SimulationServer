package clients;

import SimServer.*;
import edu.wpi.rail.jrosbridge.Ros;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by the following students at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 * Janssens Arthur, De Laet Jan & Verhoeven Peter.
 **/
abstract public class Client {

    protected InetAddress ip; // IP address of the client
    protected int port; // Port on which the client has connected
    protected Ros ros;

    public final List<Robot> ownedRobots = Collections.synchronizedList(new ArrayList<Robot>());
    public final List<Robot> externalRobots = Collections.synchronizedList(new ArrayList<Robot>());

    /**
     *
     * @param ip
     */
    public Client(String ip){
        try {
            this.ip = InetAddress.getByName(ip);
            ros = new Ros(ip);

        } catch (UnknownHostException e) {
            System.err.println("Invalid clients.Client URI");
            if(SimServer.debug)
                e.printStackTrace();
        }
    }

    /**
     *
     * @param ip
     * @param port
     */
    public Client(String ip, int port){
        try {
            this.ip = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.port = port;
        ros = new Ros(ip, port);
    }

    /**
     * Add client to robotHandler to allow for robot updates
     */
    public void init(){
        ros.connect();

        //Register client List<SimServer.Robot> in robotHandler
        SimServer.robotHandler.addClient(this);

        //Get Robots
        updateOwnedRobots();
    }

    public void closeRos(){
        ros.disconnect();
    }

    //get robots from client
    abstract public void updateOwnedRobots();

    /**
     * Add a clone of an external robot to the exteralrobots
     */
    public void addAndUpdateExternalRobot(Robot robot){
        synchronized (externalRobots){
            if(!externalRobots.contains(robot)){
                externalRobots.add(robot.clone());
            }else{
                externalRobots.get(externalRobots.indexOf(robot)).updateRobot(robot.pose, robot.twist);
            }
        }
    }

    /**
     * Create external robots not yet tracked in client's gazebo instance
     * update robots already tracked
     */
    abstract public void drawExternalRobots();
}
