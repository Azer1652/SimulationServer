package clients;

import SimServer.Robot;
import SimServer.SimServer;
import com.google.gson.Gson;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import msgs.ModelState;
import msgs.ModelStates;
import msgs.SpawnModel;

/**
 * Created by the following students at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 * Janssens Arthur, De Laet Jan and Verhoeven Peter.
 *
 * Provides an interface to connect correctly with simulated robots
 **/
public class SimulatedClient extends Client{

    public SimulatedClient(String ip, int port){
        super(ip, port);
        init();
    }

    /**
     * Get robots from client and update robots already tracked
     */
    public void updateOwnedRobots(){
        Topic echoBack = new Topic(ros, "/gazebo/model_states", "gazebo_msgs/ModelStates", 100);
        echoBack.subscribe(new TopicCallback() {
            //@Override
            public void handleMessage(Message message) {
                //String s = message.toString();
                ModelStates m = new Gson().fromJson(message.toString(), ModelStates.class);

                int i = 0;
                //Iterate all robots
                for(String name : m.name){
                    //Add robots only robots
                    if(name.contains("F1")){
                        //Add robots not yet tracked
                        synchronized (ownedRobots) {
                            if (!ownedRobots.contains(new Robot(m.name[i]))) {
                                ownedRobots.add(SimServer.robotHandler.newRobot(m.name[i], m.pose.get(i), m.twist.get(i)));
                            } else {
                                //Update robots already tracked
                                ownedRobots.get(ownedRobots.indexOf(new Robot(m.name[i]))).updateRobot(m.pose.get(i), m.twist.get(i));
                            }
                        }
                    }
                    i++;
                }
            }
        });
    }

    /**
     * Create external robots not yet tracked in client's gazebo instance
     * update robots already tracked
     */
    public void drawExternalRobots() {
        synchronized (externalRobots) {
            for (Robot robot : externalRobots) {
                if (!robot.created) {
                    createRobot(robot);
                    robot.created = true;
                } else {
                    updateRobot(robot);
                }
            }
        }
    }

    /**
     *
     * @param robot
     */
    public void updateExternalRobotPose(Robot robot){
        synchronized (externalRobots) {
            externalRobots.get(externalRobots.indexOf(robot)).updateRobot(robot.pose, robot.twist);
        }
    }

    /**
     * Spawn robot in Gazebo
     * @param robot
     */
    protected void createRobot(Robot robot){
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
        SpawnModel request = new SpawnModel("box"+robot.id, "<?xml version='1.0'?><sdf version ='1.6'>  <model name ='box"+robot.id+"'>    <pose>"+robot.pose.getPosition().getX()+" "+robot.pose.getPosition().getY()+" "+robot.pose.getPosition().getZ()+" "+robot.pose.getOrientation().getX()+" "+robot.pose.getOrientation().getY()+" "+robot.pose.getOrientation().getZ()+"</pose>    <link name ='link'>      <pose>0 0 0.5 0 0 0</pose>      <collision name ='collision'>        <geometry>          <box><size>.535 .2 1</size></box>        </geometry>      </collision>      <visual name ='visual'>   <pose frame=''>0 0 -0.5 0 0 0</pose>     <geometry>          <mesh><uri>model://meshes/ChassisBlue.dae</uri></mesh>        </geometry>      </visual>    </link>  </model></sdf>", "", robot.pose, "world");
        spawnModel.callServiceAndWait(request);
        //robot.created = true;
        //ServiceResponse response = spawnModel.callServiceAndWait(request);
        //System.out.println(response.toString());
    }

    /**
     * Delete robot from gazebo
     * @param robot
     */
    protected void deleteRobot(Robot robot){
        Service deleteModel = new Service(ros, "/gazebo/delete_model", "/gazebo/delete_model");

        ServiceRequest request = new ServiceRequest("{\"model_name\": \"box" +robot.id+ "\"}");
        deleteModel.callService(request, null);
        //ServiceResponse response = deleteModel.callServiceAndWait(request);
        //System.out.println(response.toString());
    }

    /**
     * Update robot Pose in gazebo
     * @param robot
     */
    protected void updateRobot(Robot robot){
        Topic echo = new Topic(ros, "/gazebo/set_model_state", "gazebo_msgs/ModelState");
        robot.refreshStrings();
        ModelState message = new ModelState("box" + robot.id, robot.pose, robot.twist, "world");
        echo.publish(message);
    }
}
