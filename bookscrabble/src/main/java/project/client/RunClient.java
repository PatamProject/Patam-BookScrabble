package project.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project.client.model.MyHostServer;
import project.server.serverHandler.BookScrabbleHandler;
import project.server.serverHandler.MyServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * JavaFX App
 */
public class RunClient  { // extends Application

//    private static Scene scene;
//
//    @Override
//    public void start(Stage stage) throws IOException {
//        scene = new Scene(loadFXML("primary"), 640, 480);
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    static void setRoot(String fxml) throws IOException {
//        scene.setRoot(loadFXML(fxml));
//    }
//
//    private static Parent loadFXML(String fxml) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(RunClient.class.getResource(fxml + ".fxml"));
//        return fxmlLoader.load();
//    }

    public static void client_check1()
    {
        String response;
        try {
            Socket server=new Socket("localhost",5050);
            PrintWriter outToServer=new PrintWriter(server.getOutputStream());
            Scanner in=new Scanner(server.getInputStream());
            outToServer.println("Q,Hello");
            outToServer.flush();
            response = in.next();
            System.out.println(response);
            outToServer.close();
            in.close();
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void client_check2()
    {
        String response;
        try {
            Socket server=new Socket("localhost",5050);
            PrintWriter outToServer=new PrintWriter(server.getOutputStream());
            Scanner in=new Scanner(server.getInputStream());
            outToServer.println("!Q|Hello");
            outToServer.flush();
            response = in.next();
            System.out.println(response);
            outToServer.close();
            in.close();
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        // Client logging game progress
        //Prints.log("Welcome to the game!");
        //Prints.log("Choose your role. Are you a guest or the host?");
        MyServer myServer = new MyServer(1234,new BookScrabbleHandler());
        myServer.start();
        MyHostServer myHostServer = new MyHostServer(5050,1234,"localhost");
        myHostServer.start();
        client_check1();
     //   client_check2();
        myServer.close();
        myHostServer.close();
    }

}