package project.client.model;

import project.client.MyLogger;

public class ClientModel {
    ClientCommunications myConnectionToHost;
    MyHostServer myHostServer;
    private static String myName;
    public boolean isConnectedToHost = false;

    public ClientModel(boolean isHost, String hostIP, int hostPort , String playerName)
    {
        myName = playerName;
        if(!isHost)
            myHostServer = null;
        else //isHost = true
            myHostServer = MyHostServer.getHostServer(); //Create host-side   
        
        isConnectedToHost = createClientCommunications(hostIP, hostPort);    
    }

    public boolean createClientCommunications(String hostIP, int hostPort)
    {
        try {
            myConnectionToHost = new ClientCommunications(hostIP, hostPort); //Create client-side and connect to host-side
            myConnectionToHost.start();
        } catch (Exception e) {
            MyLogger.logError("Unable to connect to host!");
            return false;
        }  
        return true;
    }

    public ClientCommunications getMyClientCommunications() {
        return myConnectionToHost;
    }

    public MyHostServer getMyHostServer() {
        return MyHostServer.getHostServer();
    }

    public void close()
    {
        if(myHostServer != null)
            myHostServer.close();
        myConnectionToHost.close();
    }
    
    public static String getName(){return myName;}
}