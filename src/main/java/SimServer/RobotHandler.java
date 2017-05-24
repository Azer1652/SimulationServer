package SimServer;

import clients.Client;
import edu.wpi.rail.jrosbridge.messages.geometry.Pose;
import edu.wpi.rail.jrosbridge.messages.geometry.Twist;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by the following students at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 * Janssens Arthur, De Laet Jan & Verhoeven Peter.
 *
 * This class manages robots and if every robot is created using this class, no robot can have the same ID.
 **/
public class RobotHandler implements Runnable{
    private long robotCounter = 0;
    public boolean shutdown = false;

    private List<Client> clients;

    /**
     * New Robothandler
     */
    public RobotHandler(){
        clients = new ArrayList<Client>();
    }

    public void addClient(Client client){
        clients.add(client);
    }

    /**
     * Create a new robot with a new ID
     * @param model_name
     * @param pose
     * @param twist
     * @return
     */
    public Robot newRobot(String model_name, Pose pose, Twist twist){
        robotCounter++;
        return new Robot(robotCounter, model_name, pose, twist, this);
    }

    /**
     * Continuously update every non local robot's location for every client
     */
    public void run() {
        while (!shutdown) {
            //Make sure every robot is up to date on every client
            for (Client client1 : clients) {
                synchronized (client1.ownedRobots) {
                    for (Robot robot : client1.ownedRobots) {
                        for (Client client2 : clients) {
                            if (!(client1.equals(client2))) {
                                client2.addAndUpdateExternalRobot(robot);
                            }
                        }
                    }
                }
            }

            //draw all robots (Only works on simulated clients)
            for(Client client: clients){
                client.drawExternalRobots();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
