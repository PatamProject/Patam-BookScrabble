package project.tests;

import project.client.RunClient;
import project.server.serverHandler.BookScrabbleHandler;
import project.server.serverHandler.MyServer;

public class TestRunClient {
    
    public static void main(String[] args) {
        MyServer myServer = new MyServer(5555,new BookScrabbleHandler()); //Local server
        myServer.start();

        new RunClient();
        
    }

}
