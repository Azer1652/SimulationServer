
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;
import msgs.ModelStates;
import msgs.ModelState;
import msgs.SpawnModel;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by arthur on 06.05.17.
 */
public class Client {

    private InetAddress ip;
    private Ros ros;

    public List<Robot> robots = Collections.synchronizedList(new ArrayList<Robot>());

    public Client(String ip){
        try {
            this.ip = InetAddress.getByName(ip);
            ros = new Ros(ip);
            ros.connect();

            //Register client List<Robot> in robotHandler
            SimServer.robotHandler.addClient(this);

            //Get Robots
            updateRobots();

        } catch (UnknownHostException e) {
            System.err.println("Invalid Client URI");
            if(SimServer.debug)
                e.printStackTrace();
        }
    }

    public Client(String ip, int port){
        robots = new ArrayList<Robot>();

        try {
            this.ip = InetAddress.getByName(ip);
            ros = new Ros(ip, port);
            ros.connect();

            //Register client List<Robot> in robotHandler
            SimServer.robotHandler.addClient(this);

            //Get Robots
            updateRobots();

        } catch (UnknownHostException e) {
            System.err.println("Invalid Client URI");
            if(SimServer.debug)
                e.printStackTrace();
        }
    }

    public void closeRos(){
        ros.disconnect();
    }

    //get robots from client
    public void updateRobots(){
        Topic echoBack = new Topic(ros, "/gazebo/model_states", "gazebo_msgs/ModelStates", 100);
        echoBack.subscribe(new TopicCallback() {
            //@Override
            public void handleMessage(Message message) {
                String s = message.toString();
                ModelStates m = new Gson().fromJson(message.toString(), ModelStates.class);

                int i = 0;
                //Iterate all robots
                for(String name : m.name){
                    //Add robots only robots
                    if(name.contains("cylinder")){
                        //Add robots not yet tracked
                        synchronized (robots) {
                            if (!robots.contains(new Robot(m.name[i]))) {
                                robots.add(SimServer.robotHandler.newRobot(m.name[i], m.pose.get(i), m.twist.get(i)));
                            } else {
                                //Update robots already tracked
                                robots.get(robots.indexOf(new Robot(m.name[i]))).updateRobot(m.pose.get(i), m.twist.get(i));
                            }
                        }
                    }
                    i++;
                }
            }
        });
    }

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
