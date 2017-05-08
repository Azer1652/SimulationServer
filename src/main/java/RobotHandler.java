import edu.wpi.rail.jrosbridge.messages.geometry.Pose;
import edu.wpi.rail.jrosbridge.messages.geometry.Twist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arthur on 06.05.17.
 */
public class RobotHandler implements Runnable{
    private long robotCounter = 0;
    public boolean shutdown = false;

    private List<Client> clients;

    public RobotHandler(){
        clients = new ArrayList<Client>();
    }

    public void addClient(Client client){
        clients.add(client);
    }

    public Robot newRobot(String model_name, Pose pose, Twist twist){
        robotCounter++;
        return new Robot(robotCounter, model_name, pose, twist);
    }

    public void run() {
        while (!shutdown) {
            //For every client check the robots and update other clients
            for (Client client1 : clients) {
                synchronized (client1.robots) {
                    for (Robot robot : client1.robots) {
                        if (!robot.created) {
                            for (Client client2 : clients) {
                                if (!(client1.equals(client2))) {
                                    client2.createRobot(robot);
                                }
                            }
                        } else {
                            for (Client client2 : clients) {
                                if (!(client1.equals(client2))) {
                                    client2.updateRobot(robot);
                                }
                            }
                        }
                    }
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
