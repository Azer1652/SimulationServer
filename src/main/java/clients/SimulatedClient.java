package clients;

import SimServer.Robot;
import SimServer.SimServer;
import com.google.gson.Gson;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import msgs.ModelStates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by arthur on 10.05.17.
 */
public class SimulatedClient extends Client{

    public SimulatedClient(String ip, int port){
        super(ip, port);
        init();
    }

    //get robots from client
    public void updateRobots(){
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

}
