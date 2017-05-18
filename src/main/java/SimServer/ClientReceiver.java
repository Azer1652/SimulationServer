package SimServer;

import clients.Client;
import clients.RealClient;
import clients.SimulatedClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by arthur on 06.05.17.
 */
public class ClientReceiver implements Runnable{

    private int port = 6666;
    private boolean accepting = true;
    private boolean shutdown = false;

    SimServer simServer;
    ServerSocket serverSocket;

    ClientReceiver(SimServer server){
        this.simServer=server;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not open serverSocket");
            if(SimServer.debug)
                e.printStackTrace();
        }
    }

    public synchronized void pause(){accepting = false;}
    public synchronized void resume(){accepting = true;}

    public synchronized void shutdown(){shutdown = true;}

    public void run() {
        while(!shutdown){
            if(accepting){
                try {
                    Socket server = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
                    InetAddress IP = server.getInetAddress();
                    int ros_port = Integer.parseInt(in.readLine());
                    Boolean real = Boolean.parseBoolean(in.readLine());

                    System.out.println("New client connected:");
                    System.out.println("IP address: " + IP);
                    System.out.println("ROS Bridge Port: " + ros_port);
                    System.out.println("Real robot: " + real);

                    if(real)
                    {
                        simServer.addClient(new RealClient(IP.getHostAddress(),ros_port, "F1"));
                    }
                    else
                    {
                        simServer.addClient(new SimulatedClient(IP.getHostAddress(),ros_port));
                    }

                    server.close();
                } catch (IOException e) {
                    System.err.println("Could not accept/close serverSocket connection");
                    if (SimServer.debug)
                        e.printStackTrace();
                }
            }else{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
