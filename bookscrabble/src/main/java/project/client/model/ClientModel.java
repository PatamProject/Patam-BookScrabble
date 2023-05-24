package project.client.model;

import java.util.concurrent.BlockingQueue;

public class ClientModel {
    private ClientCommunications myConnectionToHost;
    private New_HostModel myHost;
    static String myName;
    static String myID;
    public static final int myPort = 5005;

    public ClientModel(boolean isHost, String hostIP, int hostPort , String namePlayer) //Basic constructor
    {
        myName = namePlayer;
        myConnectionToHost = new ClientCommunications(hostIP, hostPort);
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

        public New_HostModel(int port) //Basic constructor
        {
            myConnectionServer = new MyHostServer(ClientModel.myName, port, port, myName); //TODO: Get BS IP and port
            this.port=port;
            myConnectionServer.start(); //NOT FINISHED
        }
    }

    public static MyHostServer getHostServer() {
        return ClientModel.New_HostModel.myConnectionServer;
    }

    public static String getName(){return myName;}
}