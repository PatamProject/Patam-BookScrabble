package project.server;

import java.util.Scanner;

import project.server.serverHandler.BookScrabbleHandler;
import project.server.serverHandler.MyServer;

public class ServerMain{

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Starting server...");
        System.out.println("Please choose a port: ");
        Integer port = scanner.nextInt();
        MyServer myServer = new MyServer(port,new BookScrabbleHandler()); //Local server
        System.out.println("Lunching server...");
        myServer.start();
        scanner.close();
    }

}