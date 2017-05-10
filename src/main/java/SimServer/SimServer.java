package SimServer; /**
 * Created by arthur on 04.05.17.
 */

import clients.Client;

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

        //TESTING CLIENTS
        Client client1 = new Client("127.0.0.1", 9090);
        Client client2 = new Client("127.0.0.1", 9091);

        //DISABLED FOR TESTING PURPOSES
        //Accept new clients and subscribe to topic for robot updates
        //clientAccepter.start();
        //Push updates to robots
        robotUpdater.start();

        //GARBAGE
        /*while (true) {
            c.createRobot();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        /*
        Ros ros = new Ros("localhost");
        ros.connect();

        Topic echo = new Topic(ros, "/echo", "std_msgs/String");
        Message toSend = new Message("{\"data\": \"hello, world!\"}");
        echo.publish(toSend);

        Topic echoBack = new Topic(ros, "/gazebo/model_states", "gazebo_msgs/ModelStates");
        echoBack.subscribe(new TopicCallback() {
            //@Override
            public void handleMessage(Message message) {
                Gson gson = new Gson();
                ModelStates m = gson.fromJson(message.toJsonObject().toString(), ModelStates.class);
                //System.out.println("From ROS: " + message.toString());
            }
        });

        Service deleteModel = new Service(ros, "/gazebo/delete_model", "/gazebo/delete_model");

        ServiceRequest request = new ServiceRequest("{\"model_name\": \"box_0\"}");
        ServiceResponse response = deleteModel.callServiceAndWait(request);
        System.out.println(response.toString());

        while (true){

        }

        //ros.disconnect();
        */
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


