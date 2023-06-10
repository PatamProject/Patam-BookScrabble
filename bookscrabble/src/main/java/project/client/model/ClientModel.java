package project.client.model;

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
            Thread.sleep(1000);
        } catch (Exception e) {
            close();
            return false;
        }  
        return true;
    }

    public void close()
    {
        if(myHostServer != null)
            myHostServer.close();
        if(myConnectionToHost != null)    
            myConnectionToHost.close();
    }
    
    public static String getName(){return myName;}
    public ClientCommunications getMyConnectionToHost() {return myConnectionToHost;}
}