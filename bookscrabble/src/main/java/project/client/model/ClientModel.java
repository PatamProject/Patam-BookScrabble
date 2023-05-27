package project.client.model;

import java.io.IOException;
import java.util.Scanner;

public class ClientModel {
    ClientCommunications myConnectionToHost;
    public static MyHostServer myHostServer;
    static String myName;

    public ClientModel(boolean isHost, String hostIP, int hostPort , String playerName)
    {
        myName = playerName;
        if(!isHost)
            myHostServer = null;
        else
        {
            Scanner sc = new Scanner(System.in);
            String bs_IP;
            int bs_port;
            System.out.println("Please connect to a BookScrabble server");
            System.out.println("Enter BookScrabble Server IP: ");
            bs_IP = sc.nextLine();
            System.out.println("Enter Port: ");
            bs_port = sc.nextInt();
            sc.close();
            myHostServer = new MyHostServer(hostPort, bs_port, bs_IP); //Create host-side
            myHostServer.start();
        } 

        try {
            myConnectionToHost = new ClientCommunications(hostIP, hostPort); //Create client-side and connect to host-side
            myConnectionToHost.start();
        } catch (IOException e) {
            System.out.println("Unable to connect to host!");
            e.printStackTrace();
        }  

        if(isHost)
        {
            myHostServer.checkBSConnection(); //Check if connection to BookScrabble server is established
        }
    }

    public ClientCommunications getMyClientCommunications() {
        return myConnectionToHost;
    }
    
    public static String getName(){return myName;}
}