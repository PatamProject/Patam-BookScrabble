package project.tests;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import project.client.MyLogger;

public class TestClientCommunication {
    public static void main(String[] args) {
        Socket client;
        PrintWriter out = null;
        Scanner in = null;
        try {
            client = new Socket("localhost",5005);
            out = new PrintWriter(client.getOutputStream());
            in = new Scanner(client.getInputStream());
            out.println("0:g&join");
            MyLogger.println(in.nextLine());
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
            in.close();
        }
        
        System.out.println("Done!");
    }
}
