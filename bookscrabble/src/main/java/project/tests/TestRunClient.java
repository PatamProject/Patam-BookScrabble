package project.tests;

import project.client.RunClient;
import project.client.model.ClientModel;
import project.server.serverHandler.BookScrabbleHandler;
import project.server.serverHandler.MyServer;

public class TestRunClient {
    
    public static void main(String[] args) {
        MyServer myServer = new MyServer(5555,new BookScrabbleHandler()); //Local server
        myServer.start();

        RunClient host = new RunClient();
        ClientModel guest = new ClientModel(false, "localhost", 5005, "guest");

    }

}
