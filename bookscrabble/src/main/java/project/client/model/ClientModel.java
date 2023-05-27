package project.client.model;

import java.io.IOException;
import java.util.Scanner;

import project.client.MyLogger;

public class ClientModel {
    ClientCommunications myConnectionToHost;
    public static MyHostServer myHostServer;
    static String myName;

    public ClientModel(boolean isHost, String hostIP, int hostPort , String playerName) //Basic constructor
    {
        myName = playerName;
        if(!isHost)
            myHostServer = null;
        else
        {
            Scanner sc = MyLogger.scanner;
            String bs_IP;
            int bs_port;
            System.out.println("Please connect to a BookScrabble server");
            System.out.println("Enter BookScrabble Server IP: ");
            bs_IP = sc.nextLine();
            System.out.println("Enter Port: ");
            bs_port = sc.nextInt();
            myHostServer = new MyHostServer(hostPort, bs_port, bs_IP);
            myHostServer.start();
        } 

        try {
            myConnectionToHost = new ClientCommunications(hostIP, hostPort);
            myConnectionToHost.start();
        } catch (IOException e) {
            System.out.println("Unable to connect to host!");
            e.printStackTrace();
        }
    }

    public ClientCommunications getMyClientCommunications() {
        return myConnectionToHost;
    }
    
    public static String getName(){return myName;}
}