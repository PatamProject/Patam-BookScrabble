package bookscrabble.client.model;

import java.util.Observable;

public class ClientModel extends Observable{
    private static ClientModel myClientModel; //Singleton
    ClientCommunications myConnectionToHost;
    MyHostServer myHostServer;
    private static String myName;
    String hostIP, BsIP;
    int hostPort, BsPort;
    public boolean isConnectedToHost = false;
    boolean isHost = false;
    public static boolean isGameRunning = false;
    String lastMsgReceivedFromClient;

    public static ClientModel getClientModel() //Singleton
    {
        if(myClientModel == null)
            myClientModel = new ClientModel();
        return myClientModel;
    }

    private ClientModel(){} //Ctor

    public void createClient(boolean isHost, String hostIP, int hostPort, String name) //Only activate after setting all the needed IPs and ports
    {
        myName = name;
        if(!isHost)
            myHostServer = null;
        else //isHost = true
            myHostServer = getMyHostServer(); //Create host-side
        
        isConnectedToHost = createClientCommunications(hostIP, hostPort);  
        if(!isConnectedToHost)
            if(isHost)
                lastMsgReceivedFromClient = "Failed to connect to BookScrabble Server"; //Set error message to show in GUI
            else
                lastMsgReceivedFromClient = "Failed to connect to host";  
        setChanged();
        notifyObservers();
    }

    public boolean createClientCommunications(String hostIP, int hostPort)
    {

        try {
            Thread.sleep(1000);
            myConnectionToHost = new ClientCommunications(hostIP, hostPort); //Create client-side and connect to host-side
            myConnectionToHost.start();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {}
        } catch (Exception e) { //Failed to connect to host
            disconnectedFromHost(e.getMessage());
            return false;
        }  
        return true;
    }

    public void startGame() //Called when the game starts
    {
        setChanged();
        notifyObservers("gameStarted");
    }

    public synchronized void disconnectedFromHost(String msg) //Called when the client is disconnected from the host
    {
        String knownExeptions = "gameStarted, endGame, Disconnected from host!";
        if(knownExeptions.contains(msg)) //Known exceptions
            lastMsgReceivedFromClient = msg;
        
        if(isGameRunning)
        {
            setChanged();
            notifyObservers("endGame");
            isGameRunning = false;
        }    
        isGameRunning = false;
        if(isConnectedToHost != true)
            return;

        isConnectedToHost = false;
        clear();
    }
    
    public synchronized void close() //Close method for ClientModel
    {
        if(myHostServer != null)
            myHostServer.tryClose();
        if(myConnectionToHost != null)    
            myConnectionToHost.close();
    }

    public synchronized void clear()
    {
        close();
        myConnectionToHost = null;
        myHostServer = null;
        isConnectedToHost = false;
        isGameRunning = false;
    }

    //setters
    public void setMyName(String name) {myName = name;}
    public void setIfHost(boolean isHost){this.isHost = isHost;}
    public void setHostIP(String hostIP){this.hostIP = hostIP;}
    public void setHostPort(int hostPort){this.hostPort = hostPort;}
    public void setBsIP(String BsIP){this.BsIP = BsIP;}
    public void setBsPort(int BsPort){this.BsPort = BsPort;}

    //getters
    public String getErrorMessage() {return lastMsgReceivedFromClient;}
    public boolean isHost() {return isHost;}
    public String getHostIP() {return hostIP;}
    public Integer getHostPort() {return hostPort;}
    public String getBsIP() {return BsIP;}
    public Integer getBsPort() {return BsPort;}
    public MyHostServer getMyHostServer() { return MyHostServer.getHostServer();}
    public static String getMyName(){return myName;}
    public ClientCommunications getMyConnectionToHost() {return myConnectionToHost;}
}