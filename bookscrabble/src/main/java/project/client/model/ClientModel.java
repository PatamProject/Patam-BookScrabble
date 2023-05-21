package project.client.model;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class ClientModel {
    private Communications myConnectionHost;
    private HostModel myHost = null;
    static String myName;
    public String hostIP;
    int port;

    public ClientModel(boolean isHost, String hostIP, int port , String namePlayer) //Basic constructor
    {
        myName = namePlayer;
        this.hostIP = hostIP;
        this.port = port;
        if(!isHost)
        {
            this.hostIP = hostIP;
            //myConnection = new CommunicationModel(hostIP, port);

        }
        else {
            myHost = new HostModel(port);
        }

    }

    private void msgToHost(String msg)
    {
//        try {
//            Socket server=new Socket(hostIP, port);
//            PrintWriter outToServer=new PrintWriter(server.getOutputStream());
//            outToServer.println(msg);
//            outToServer.close();
//            server.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
    public static class HostModel{
        
        private Communications myConnectionServer;

        static HashMap<String, Socket> clients;
        int port;

        public HostModel(int port) //Basic constructor
        {
            //myConnectionServer = new CommunicationModel(port , (ClientHandler) new BookScrabbleHandler());

            clients = new HashMap<String, Socket>();
            this.port=port;
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

        public static void updateFromPlayer(String pName,String msg, Socket player)
        {
            if(!clients.containsKey(pName))
                clients.put(pName,player);
            try {
                player.getInputStream().read(msg.getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        public boolean msgToBSH(String msg)
//        {
//            String response;
//            try {
//                Socket server=new Socket("localhost", port);
//                PrintWriter outToServer=new PrintWriter(server.getOutputStream());
//                Scanner in=new Scanner(server.getInputStream());
//                outToServer.println(msg);
//                response = in.next();
//                in.close();
//                outToServer.close();
//                server.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            return Boolean.parseBoolean(response);
//        }

    }

  
}