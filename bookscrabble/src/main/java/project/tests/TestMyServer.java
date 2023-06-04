package project.tests;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import project.server.serverHandler.BookScrabbleHandler;
import project.server.serverHandler.MyServer;

public class TestMyServer {
    static int port = 5555;
    public static void main(String[] args) {
        MyServer server = new MyServer(port, new BookScrabbleHandler());
        server.start();
        int queryCount = 0, challengeCount = 0;
        try {
            Socket socket = new Socket("localhost", port);
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            Scanner in = new Scanner(socket.getInputStream());
            File file = new File("bookscrabble" + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "project" + File.separator + "TestMyServer" + File.separator + "wordsToTest.txt");
            Scanner fileScanner = new Scanner(file);
            while(fileScanner.hasNextLine())
            {
                String line = fileScanner.nextLine();
                line = line.trim();
                String[] words = line.split(" ");
                for (String w : words) {
                    if(testQuery(out, in, w))
                    {
                        queryCount++;
                        if(testChallenge(out, in, w))
                            challengeCount++;
                    }
                }
            }

            in.close();
            out.close();
            fileScanner.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        server.close();
        System.out.println("Query count: " + queryCount);
        System.out.println("Challenge count: " + challengeCount);
        System.out.println("Done.");
    }

    private static boolean testQuery(PrintWriter out, Scanner in, String word) {
        String res = "";
        out.println("Q," + word);
        out.flush();
        res = in.nextLine();
        return Boolean.parseBoolean(res);
    }

    private static boolean testChallenge(PrintWriter out, Scanner in, String word) {
        String res = "";
        out.println("C," + word);
        out.flush();
        res = in.nextLine();
        return Boolean.parseBoolean(res);
    }
}
