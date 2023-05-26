package project.client.model;

import java.io.IOException;
import java.util.Scanner;

public class ClientModel {
    static ClientCommunications myConnectionToHost;
    static MyHostServer myHostServer;
    static String myName;
    public static final int myPort = 5005;

    public ClientModel(boolean isHost, String hostIP, int hostPort , String namePlayer) //Basic constructor
    {
        myName = namePlayer;
        try {
            myConnectionToHost = new ClientCommunications(hostIP, hostPort);
        } catch (IOException e) {
            System.out.println("Unable to connect to host!");
            e.printStackTrace();
        }
        myConnectionToHost.start();
        if(!isHost)
            myHostServer = null;
        else
        {
            String bs_IP;
            int bs_port;
            System.out.println("Enter BookScrabble Server IP: ");
            System.out.println("Enter Port: ");
            Scanner sc = new Scanner(System.in);
            bs_IP = sc.nextLine();
            bs_port = sc.nextInt();
            sc.close();
            myHostServer = new MyHostServer(myPort, bs_port, bs_IP);
            myHostServer.start();
        } 
    }

    public static String getName(){return myName;}
}