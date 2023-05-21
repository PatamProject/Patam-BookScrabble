package project.client.model;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class ClientModel {
    private CommunicationModel myConnection;
    private HostModel myHost;

    public ClientModel(boolean isHost, String hostIP, int port) //Basic constructor
    {
        if(isHost)
            myHost = new HostModel();
        else
            myHost = null;
        
        //myConnection = new CommunicationModel(hostIP, port);
    }

    public static class HostModel{
        
        private CommunicationModel myConnection;
        static HashMap<String, Socket> clients;

        public HostModel() //Basic constructor
        {
            //myConnection = new CommunicationModel();
            clients = new HashMap<String, Socket>();
            //clients.put("host", new Socket("localhost", 5002));
        }

        public static void updateAllPlayers(String msg)
        {
            for (String key : clients.keySet()) {
                Socket client = clients.get(key);
                try {
                    client.getOutputStream().write(msg.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public static void updateFromPlayer(String msg, Socket player)
        {
            try {
                player.getInputStream().read(msg.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

  
}

