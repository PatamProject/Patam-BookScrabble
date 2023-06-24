package bookscrabble.tests;
import bookscrabble.client.misc.RunClient;
import bookscrabble.server.serverHandler.BookScrabbleHandler;
import bookscrabble.server.serverHandler.MyServer;

public class TestRunClient {
    public static void main(String[] args) {
        MyServer myServer = new MyServer(5555,new BookScrabbleHandler()); //Local server
        myServer.start();

        new RunClient();        
    }

}
