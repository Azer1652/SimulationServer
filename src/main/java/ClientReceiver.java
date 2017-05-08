import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

/**
 * Created by arthur on 06.05.17.
 */
public class ClientReceiver implements Runnable{

    private int port = 1652;
    private boolean accepting = true;
    private boolean shutdown = true;

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
                    serverSocket.accept();
                    simServer.addClient(new Client(serverSocket.getInetAddress().toString()));
                    serverSocket.close();
                } catch (IOException e) {
                    System.err.println("Could not accept/close serverSocket connection");
                    if(SimServer.debug)
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
