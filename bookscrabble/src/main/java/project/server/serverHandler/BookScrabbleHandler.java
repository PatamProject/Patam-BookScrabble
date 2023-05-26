package project.server.serverHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import project.server.cacheHandler.DictionaryManager;

public class BookScrabbleHandler implements ClientHandler{
    PrintWriter out;
    Scanner in;

    public BookScrabbleHandler(){}

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {
        out=new PrintWriter(outToClient);
        in=new Scanner(inFromClient);
        String line = in.next();
        char c = line.charAt(0);
        line = line.substring(2);
        String[] args = line.split(",");

        if(c == 'Q')
        {
            if(DictionaryManager.get().query(args))
                out.println("true");
            else
                out.println("false");
        }
        else if(c == 'C')
        {
            if(DictionaryManager.get().challenge(args))
                out.println("true");
            else
                out.println("false");
        }
        out.flush();
    }

    @Override
    public void close() {
        in.close();
        out.close();
    }
}
