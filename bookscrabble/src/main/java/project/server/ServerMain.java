package project.server;

/*
 Response codes:
100 - OK, 'body' (Ack)
200 - Unknown command
300 - Missing arguments 
301 - Invalid arguments , 'arg index'
400 - Server error
500 - Access denied
600 - Game ended, 'body (winner)'
 */

public class ServerMain{

    public static void main(String[] args) {
        //launch();
        System.out.println("Hello from server!");
    }

}