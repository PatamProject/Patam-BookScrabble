package project.client.model;

import java.util.concurrent.BlockingQueue;

public class New_ClientModel {
    private ClientCommunications myConnectionToHost;
    private New_HostModel myHost;
    static String myName;
    public static final int myPort = 5005;

    public New_ClientModel(boolean isHost, String hostIP, int hostPort , String namePlayer) //Basic constructor
    {
        myName = namePlayer;
        myConnectionToHost = new New_ClientCommunications(hostIP, hostPort);
        if(!isHost)
            myHost = null;
        else 
            myHost = new New_HostModel(myPort + 1);
    }

    public static class New_HostModel{
        
        static New_MyHostServer myConnectionServer;
        BlockingQueue<String> myTasks;
        int port;

        public New_HostModel(int port) //Basic constructor
        {
            myConnectionServer = new New_MyHostServer(New_ClientModel.myName, port, port, myName); //TODO: Get BS IP and port
            this.port=port;
            myConnectionServer.start(); //NOT FINISHED
        }

        public static boolean dictionaryLegal(String... words)
        {
            New_MyHostServer myConnectionServer = New_ClientModel.getHostServer();
        }
    }

    public static New_MyHostServer getHostServer() {
        return New_ClientModel.New_HostModel.myConnectionServer;
    }
}