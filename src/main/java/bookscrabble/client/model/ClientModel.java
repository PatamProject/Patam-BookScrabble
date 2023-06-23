package bookscrabble.client.model;

import java.util.Observable;
import java.util.Observer;

public class ClientModel extends Observable{
    ClientCommunications myConnectionToHost;
    MyHostServer myHostServer;
    private static String myName;
    String hostIP, BsIP;
    int hostPort, BsPort;
    public boolean isConnectedToHost = false;
    boolean isHost = false;
    String lastErrorReceivedFromClient;

    public ClientModel(){} //Ctor

    public void createClient(boolean isHost, String hostIP, int hostPort, String name) //Only activate after setting all the needed IPs and ports
    {
        myName = name;
        if(!isHost)
            myHostServer = null;
        else //isHost = true
            myHostServer = getMyHostServer(); //Create host-side
        
        isConnectedToHost = createClientCommunications(hostIP, hostPort);    
        setChanged();
        notifyObservers();
    }

    public boolean createClientCommunications(String hostIP, int hostPort)
    {
        try {
            myConnectionToHost = new ClientCommunications(hostIP, hostPort); //Create client-side and connect to host-side
            myConnectionToHost.start();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {}
        } catch (Exception e) { //Failed to connect to host
            lastErrorReceivedFromClient = "Failed to connect to BookScrabble Server"; //Set error message to show in GUI
            setChanged();
            notifyObservers();
            close();
            return false;
        }  
        return true;
    }
    
    public void close() //Close method for ClientModel
    {
        if(myHostServer != null)
            myHostServer.close();
        if(myConnectionToHost != null)    
            myConnectionToHost.close();
    }

    //setters
    public void setMyName(String name) {myName = name;}
    public void setIfHost(boolean isHost){this.isHost = isHost;}
    public void setHostIP(String hostIP){this.hostIP = hostIP;}
    public void setHostPort(int hostPort){this.hostPort = hostPort;}
    public void setBsIP(String BsIP){this.BsIP = BsIP;}
    public void setBsPort(int BsPort){this.BsPort = BsPort;}

    //getters
    public String getErrorMessage() {return lastErrorReceivedFromClient;}
    public boolean isHost() {return isHost;}
    public String getHostIP() {return hostIP;}
    public Integer getHostPort() {return hostPort;}
    public String getBsIP() {return BsIP;}
    public Integer getBsPort() {return BsPort;}
    public MyHostServer getMyHostServer() { return MyHostServer.getHostServer();}
    public static String getName(){return myName;}
    public ClientCommunications getMyConnectionToHost() {return myConnectionToHost;}
}