package clients;


import SimServer.*;
import com.google.gson.Gson;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import msgs.ModelState;
import msgs.ModelStates;
import msgs.SpawnModel;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by arthur on 06.05.17.
 */
abstract public class Client {

    protected InetAddress ip; // IP address of the client
    protected int port; // Port on which the client has connected
    protected Ros ros;

    public List<Robot> robots = Collections.synchronizedList(new ArrayList<Robot>());

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

    public Client(InetAddress ip, int port){
        this.ip = ip;
        this.port = port;
        ros = new Ros(ip.getHostAddress(), port);
    }

    public void init(){
        ros.connect();

        //Register client List<SimServer.Robot> in robotHandler
        SimServer.robotHandler.addClient(this);

        //Get Robots
        updateRobots();
    }

    public void closeRos(){
        ros.disconnect();
    }

    //get robots from client
    abstract public void updateRobots();

    //works
    abstract public void createRobot(Robot robot);

    abstract public void deleteRobot(Robot robot);

    //works
    abstract public void updateRobot(Robot robot);
}
