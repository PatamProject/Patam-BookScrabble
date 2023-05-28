package project.client.model;

import java.util.Scanner;

import project.client.MyLogger;

public class ClientModel {
    ClientCommunications myConnectionToHost;
    public static MyHostServer myHostServer;
    private static String myName;

    public ClientModel(boolean isHost, String hostIP, int hostPort , String playerName)
    {
        myName = playerName;
        if(!isHost)
            myHostServer = null;
        else
        {
            Scanner sc = MyLogger.getScanner();
            String bs_IP;
            int bs_port;
            System.out.println("Please connect to a BookScrabble server");
            System.out.println("Enter BookScrabble Server IP: ");
            bs_IP = sc.nextLine();
            System.out.println("Enter Port: ");
            bs_port = sc.nextInt();
            myHostServer = new MyHostServer(hostPort, bs_port, bs_IP); //Create host-side
            myHostServer.start();
        } 

        try {
            myConnectionToHost = new ClientCommunications(hostIP, hostPort); //Create client-side and connect to host-side
            myConnectionToHost.start();
            Thread.sleep(5000);
        } catch (Exception e) {
            System.out.println("Unable to connect to host!");
            e.printStackTrace();
        }  

        if(isHost)
        {
            while(MyHostServer.connectedClients.size() == 0); //Wait for host to join
            myHostServer.checkBSConnection(); //Check if connection to BookScrabble server is established
        }
    }

    public ClientCommunications getMyClientCommunications() {
        return myConnectionToHost;
    }
    
    public static String getName(){return myName;}
}