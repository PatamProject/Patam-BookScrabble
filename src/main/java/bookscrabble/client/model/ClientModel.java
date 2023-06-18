package bookscrabble.client.model;

import java.util.Observable;

public class ClientModel extends Observable{
    ClientCommunications myConnectionToHost;
    MyHostServer myHostServer;
    private static String myName;
    String hostIP, BsIP;
    int hostPort, BsPort;
    public boolean isConnectedToHost = false;
    boolean isHost = false;

    public ClientModel(){} //Ctor

    public void createClient(boolean isHost, String hostIP, int hostPort, String name) //Only activate after setting all the needed IPs and ports
    {
        myName = name;
        if(!isHost)
            myHostServer = null;
        else //isHost = true
            myHostServer = getMyHostServer(); //Create host-side
        
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
    
    public void close() //Close method for ClientModel
    {
        if(myHostServer != null)
            myHostServer.close();
        if(myConnectionToHost != null)    
            myConnectionToHost.close();
    }

    private void setChangedAndNotify() //Notifies the observers about a change
    {
        setChanged();
        notifyObservers();
    }

    //setters
    public void setMyName(String name) {myName = name; setChangedAndNotify();}
    public void setIfHost(boolean isHost){this.isHost = isHost; setChangedAndNotify();}
    public void setHostIP(String hostIP){this.hostIP = hostIP; setChangedAndNotify();}
    public void setHostPort(int hostPort){this.hostPort = hostPort; setChangedAndNotify();}
    public void setBsIP(String BsIP){this.BsIP = BsIP; setChangedAndNotify();}
    public void setBsPort(int BsPort){this.BsPort = BsPort; setChangedAndNotify();}

    //getters
    public boolean isHost() {return isHost;}
    public String getHostIP() {return hostIP;}
    public Integer getHostPort() {return hostPort;}
    public String getBsIP() {return BsIP;}
    public Integer getBsPort() {return BsPort;}
    public MyHostServer getMyHostServer() { return MyHostServer.getHostServer();}
    public static String getName(){return myName;}
    public ClientCommunications getMyConnectionToHost() {return myConnectionToHost;}
}