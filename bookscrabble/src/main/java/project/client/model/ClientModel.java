package project.client.model;

public class ClientModel {
    private Communications myConnectionToHost;
    private HostModel myHost;
    static String myName;
    public final int myPort = 5005;

    public ClientModel(boolean isHost, String hostIP, int hostPort , String namePlayer) //Basic constructor
    {
        myName = namePlayer;
        myConnectionToHost = new ClientCommunications(hostIP, hostPort);
        if(!isHost)
            myHost = null;
        else 
            myHost = new HostModel(myPort + 1);
    }

    public static class HostModel{
        
        private Communications myConnectionServer;
        final int port;

        public HostModel(int port) //Basic constructor
        {
            myConnectionServer = new MyHostServer(port, port, myName); //TODO: Get BS IP and port
            this.port=port;
            myConnectionServer.start(); //NOT FINISHED
        }
    }
}