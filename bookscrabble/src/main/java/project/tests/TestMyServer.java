package project.tests;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import project.server.serverHandler.BookScrabbleHandler;
import project.server.serverHandler.MyServer;

public class TestMyServer {
    static int port = 5556;
    static int queryCount = 0, challengeCount = 0;
    public static void main(String[] args) {
        MyServer server = new MyServer(port, new BookScrabbleHandler());
        server.start();
        try {
            File file = new File("Patam-BookScrabble" + File.separator + "bookscrabble" + File.separator + "resources" + File.separator + "project" + File.separator + "TestMyServer" + File.separator + "wordsToTest.txt");
            Scanner fileScanner = new Scanner(file);
            String[] moreWordsToTest ={"duzzzzzer","Bilibobo","speeding","","queue","boy!"};
            try {
                for (String w : moreWordsToTest) {
                    if(!testQuery(w))
                        testChallenge(w);
                }
    
                while(fileScanner.hasNextLine())
                {
                    String line = fileScanner.nextLine();
                    line = line.trim();
                    String[] words = line.split(" ");
                    for (String w : words) {
                        if(!testQuery(w))
                            testChallenge(w);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            fileScanner.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        server.close();
        System.out.println("Query count: " + queryCount);
            if(queryCount != 220)
                System.out.println("Error: wrong query count");
        System.out.println("Challenge count: " + challengeCount);
            if(challengeCount != 6)
                System.out.println("Error: wrong challenge count");
        System.out.println("Done! :)");
    }

    private static boolean testQuery(String word) throws UnknownHostException, IOException {
        Socket socket = new Socket("localhost", port);
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        Scanner in = new Scanner(socket.getInputStream());
        String res = "";
        out.println("Q," + word);
        out.flush();
        res = in.nextLine();
        in.close();
        out.close();
        socket.close();
        if(Boolean.parseBoolean(res))
            queryCount++;
        return Boolean.parseBoolean(res);    
    }

    private static void testChallenge(String word) throws UnknownHostException, IOException {
        Socket socket = new Socket("localhost", port);
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        Scanner in = new Scanner(socket.getInputStream());
        String res = "";
        System.out.println("Challenge: " + word);
        out.println("C," + word);
        out.flush();
        res = in.nextLine();
        in.close();
        out.close();
        socket.close();
        if(Boolean.parseBoolean(res))
            challengeCount++;
    }
}
