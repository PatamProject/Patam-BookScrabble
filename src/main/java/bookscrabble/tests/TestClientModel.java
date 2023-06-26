package bookscrabble.tests;

import bookscrabble.client.model.ClientModel;
import bookscrabble.server.serverHandler.MyServer;
import bookscrabble.server.serverHandler.BookScrabbleHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

//not finished yet
public class TestClientModel
{
    public static void TestMsgToBS() // 'id':'name'&join' || 'word,row,col,isVertical'
    {
        String response;
        try {
            Socket server=new Socket("localhost",8002);
            PrintWriter outToServer=new PrintWriter(server.getOutputStream());
            Scanner in=new Scanner(server.getInputStream());
            outToServer.println("0:hostPlayer&join");
            outToServer.println("1:player1&join");
            outToServer.flush();
            response = in.next();
            System.out.println(response);
            outToServer.println("0:hostPlayer&C:IS,7,7,true");
            outToServer.flush();
            response = in.next();
            outToServer.close();
            in.close();
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        MyServer myServer = new MyServer(1234,new BookScrabbleHandler());
        myServer.start();
        ClientModel host = ClientModel.getClientModel();
        //ClientModel player1 = new ClientModel();
        host.createClient(true,"localhost",8002,"hostPlayer");
        //player1.createClient(false,"localhost",8002,"player1");
        TestMsgToBS();
        myServer.close();
        host.close();
        //player1.close();
    }
}
