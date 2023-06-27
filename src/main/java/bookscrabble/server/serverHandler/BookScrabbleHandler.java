package bookscrabble.server.serverHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import bookscrabble.server.App;
import bookscrabble.server.cacheHandler.DictionaryManager;

public class BookScrabbleHandler implements ClientHandler{
    PrintWriter out;
    Scanner in;
    public static String[] dictionaries;

    public BookScrabbleHandler()
    {
        dictionaries = new String[8]; //bookscrabble\src\main\resources\bookscrabble\Dictionaries\alice_in_wonderland.txt
        dictionaries[0] = "/bookscrabble/Dictionaries/alice_in_wonderland.txt";
        dictionaries[1] = "/bookscrabble/Dictionaries/Frank Herbert - Dune.txt";
        dictionaries[2] = "/bookscrabble/Dictionaries/Harry Potter.txt";
        dictionaries[3] = "/bookscrabble/Dictionaries/mobydick.txt";
        dictionaries[4] = "/bookscrabble/Dictionaries/pg10.txt";
        dictionaries[5] = "/bookscrabble/Dictionaries/shakespeare.txt";
        dictionaries[6] = "/bookscrabble/Dictionaries/The Matrix.txt";
        dictionaries[7] = "/bookscrabble/Dictionaries/wordDictionary.txt";
    }

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient, String ip) {
        out=new PrintWriter(outToClient,true);
        in=new Scanner(inFromClient);
        String line = in.next();
        char c = line.charAt(0);
        line = line.substring(2);
        String[] args = new String[dictionaries.length + 1];
        System.arraycopy(dictionaries, 0, args, 0, dictionaries.length);
        args[dictionaries.length] = line; //args = [dictionary1, dictionary2, ..., dictionaryN, word]
        String word = args[args.length - 1];
        word = word.toUpperCase();
        word = word.replaceAll("[^a-zA-Z]", ""); //Remove all non alphabetic characters
        word = word.trim();
        args[args.length - 1] = word;
        App.write("Server received a request from " + ip + " : " + args[args.length - 1]);

        if(c == 'Q')
        {
            if(DictionaryManager.get().query(args))
                send("true",ip);
            else
                send("false",ip);
        }
        else if(c == 'C')
        {
            if(DictionaryManager.get().challenge(args))
                send("true",ip);
            else
                send("false",ip);
        } else if(c == 'S')
        {
            send("Hello",ip);
        }
        out.flush();
    }

    private void send(String msg, String ip) {
        out.println(msg);
        App.write("Server replied to " + ip + " : " + msg);
    }

    @Override
    public void close() {
        if(out != null)
            out.close();
        if(in != null)
            in.close();
    }
}
