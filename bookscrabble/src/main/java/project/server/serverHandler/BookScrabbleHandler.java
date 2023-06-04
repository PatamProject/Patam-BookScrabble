package project.server.serverHandler;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import project.server.cacheHandler.DictionaryManager;

public class BookScrabbleHandler implements ClientHandler{
    PrintWriter out;
    Scanner in;
    public static String[] dictionaries;

    public BookScrabbleHandler()
    {
        dictionaries = new String[8];
        dictionaries[0] = "Patam-BookScrabble" + File.separator + "bookscrabble" + File.separator + "resources" + File.separator + "project" + File.separator + "Dictionaries" + File.separator + "alice_in_wonderland.txt";
        dictionaries[1] = "Patam-BookScrabble" + File.separator + "bookscrabble" + File.separator + "resources" + File.separator + "project" + File.separator + "Dictionaries" + File.separator + "Frank Herbert - Dune.txt";
        dictionaries[2] = "Patam-BookScrabble" + File.separator + "bookscrabble" + File.separator + "resources" + File.separator + "project" + File.separator + "Dictionaries" + File.separator + "Harry Potter.txt";
        dictionaries[3] = "Patam-BookScrabble" + File.separator + "bookscrabble" + File.separator + "resources" + File.separator + "project" + File.separator + "Dictionaries" + File.separator + "mobydick.txt";
        dictionaries[4] = "Patam-BookScrabble" + File.separator + "bookscrabble" + File.separator + "resources" + File.separator + "project" + File.separator + "Dictionaries" + File.separator + "pg10.txt";
        dictionaries[5] = "Patam-BookScrabble" + File.separator + "bookscrabble" + File.separator + "resources" + File.separator + "project" + File.separator + "Dictionaries" + File.separator + "shakespeare.txt";
        dictionaries[6] = "Patam-BookScrabble" + File.separator + "bookscrabble" + File.separator + "resources" + File.separator + "project" + File.separator + "Dictionaries" + File.separator + "The Matrix.txt";
        dictionaries[7] = "Patam-BookScrabble" + File.separator + "bookscrabble" + File.separator + "resources" + File.separator + "project" + File.separator + "Dictionaries" + File.separator + "wordDictionary.txt";
    }

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {
        out=new PrintWriter(outToClient);
        in=new Scanner(inFromClient);
        String line = in.next();
        char c = line.charAt(0);
        line = line.substring(2);
        String[] args = new String[dictionaries.length + 1];
        System.arraycopy(dictionaries, 0, args, 0, dictionaries.length);
        args[dictionaries.length] = line; //args = [dictionary1, dictionary2, ..., dictionaryN, word]

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
        } else if(c == 'S')
        {
            out.println("Hello");
        }
        out.flush();
    }

    @Override
    public void close() {
        in.close();
        out.close();
    }
}
