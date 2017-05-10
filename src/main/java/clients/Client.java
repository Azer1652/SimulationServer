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

    protected InetAddress ip;
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

    public Client(String ip, int port){
        try {
            this.ip = InetAddress.getByName(ip);
            ros = new Ros(ip, port);

        } catch (UnknownHostException e) {
            System.err.println("Invalid clients.Client URI");
            if(SimServer.debug)
                e.printStackTrace();
        }
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
    public void createRobot(Robot robot){
        Service spawnModel = new Service(ros, "/gazebo/spawn_sdf_model", "/gazebo/spawn_sdf_model");

        /*ServiceRequest request = new ServiceRequest("{"
            +"\"model_name\": \"box"+robot.id+"\","
            +"\"model_xml\": \"<?xml version='1.0'?><sdf version ='1.6'>  <model name ='box"+robot.id+"'>    <pose>"+robot.pose.getPosition().getX()+" "+robot.pose.getPosition().getY()+" "+robot.pose.getPosition().getZ()+" "+robot.pose.getOrientation().getX()+" "+robot.pose.getOrientation().getY()+" "+robot.pose.getOrientation().getZ()+"</pose>    <link name ='link'>      <pose>0 0 .125 0 0 0</pose>      <collision name ='collision'>        <geometry>          <box><size>.33 .2 .09</size></box>        </geometry>      </collision>      <visual name ='visual'>        <geometry>          <box><size>.33 .2 .09</size></box>        </geometry>      </visual>    </link>  </model></sdf>\","
            +"\"robot_namespace\": \"\","
            +"\"initial_pose\": {\"position\" : {\"x\":"+robot.pose.getPosition().getX()+",\"y\":"+robot.pose.getPosition().getY()+",\"z\":"+robot.pose.getPosition().getZ()+"},\"orientation\": {\"x\":"+robot.pose.getOrientation().getX()+",\"y\":"+robot.pose.getOrientation().getY()+",\"z\":"+robot.pose.getOrientation().getZ()+",\"w\":"+robot.pose.getOrientation().getW()+"}},"
            +"\"reference_frame\": \"world\""
            +"}");
            */
        //ServiceRequest request = new ServiceRequest("{\"model_name\": \"box" +robot.id+ "\"}");

        robot.refreshStrings();
        SpawnModel request = new SpawnModel("box"+robot.id, "<?xml version='1.0'?><sdf version ='1.6'>  <model name ='box"+robot.id+"'>    <pose>"+robot.pose.getPosition().getX()+" "+robot.pose.getPosition().getY()+" "+robot.pose.getPosition().getZ()+" "+robot.pose.getOrientation().getX()+" "+robot.pose.getOrientation().getY()+" "+robot.pose.getOrientation().getZ()+"</pose>    <link name ='link'>      <pose>0 0 .125 0 0 0</pose>      <collision name ='collision'>        <geometry>          <box><size>.33 .2 .09</size></box>        </geometry>      </collision>      <visual name ='visual'>        <geometry>          <box><size>.33 .2 .09</size></box>        </geometry>      </visual>    </link>  </model></sdf>", "", robot.pose, "world");
        spawnModel.callServiceAndWait(request);
        robot.created = true;
        //ServiceResponse response = spawnModel.callServiceAndWait(request);
        //System.out.println(response.toString());
    }

    public void deleteRobot(Robot robot){
        Service deleteModel = new Service(ros, "/gazebo/delete_model", "/gazebo/delete_model");

        ServiceRequest request = new ServiceRequest("{\"model_name\": \"box" +robot.id+ "\"}");
        deleteModel.callService(request, null);
        //ServiceResponse response = deleteModel.callServiceAndWait(request);
        //System.out.println(response.toString());
    }

    //works
    public void updateRobot(Robot robot){
        Topic echo = new Topic(ros, "/gazebo/set_model_state", "gazebo_msgs/ModelState");
        robot.refreshStrings();
        ModelState message = new ModelState("box" + robot.id, robot.pose, robot.twist, "world");
        echo.publish(message);
    }
}
