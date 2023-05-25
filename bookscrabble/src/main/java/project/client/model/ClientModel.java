package project.client.model;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class ClientModel {
    static ClientCommunications myConnectionToHost;
    private New_HostModel myHost;
    static String myName;
    public static final int myPort = 5005;

    public ClientModel(boolean isHost, String hostIP, int hostPort , String namePlayer) //Basic constructor
    {
        myName = namePlayer;
        try {
            myConnectionToHost = new ClientCommunications(hostIP, hostPort);
        } catch (IOException e) {
            System.out.println("Unable to connect to host!");
            e.printStackTrace();
        }
        myConnectionToHost.start();
        if(!isHost)
            myHost = null;
        else 
            myHost = new New_HostModel(myPort + 1);
    }

    public static class New_HostModel{
        
        static MyHostServer myConnectionServer;
        BlockingQueue<String> myTasks;
        int port;

        public New_HostModel(int port) //constructor
        {
            myConnectionServer = new MyHostServer(ClientModel.myName, port, port, myName); //TODO: Get BS IP and port
            this.port=port;
            myConnectionServer.start();
        }
    }

    public static MyHostServer getHostServer() {
        return ClientModel.New_HostModel.myConnectionServer;
    }

    public static String getName(){return myName;}
}