package project.server.serverHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;


public class MyServer {
    private final ClientHandler clientHandler;
    private final int port;
    private volatile boolean stopServer = false;

	public MyServer(int port, ClientHandler ch)
    {
        this.clientHandler = ch;
        this.port = port;
        stopServer = false;
    }

    public void start()
    {
        new Thread(()->{
            try {
                run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void run() throws Exception
    {
        ServerSocket server = new ServerSocket(port);
        server.setSoTimeout(360*1000); //Timeout in seconds 
        while(!stopServer)
        {
            try {
                Socket aClient = server.accept();
                try {
                    clientHandler.handleClient(aClient.getInputStream(), aClient.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    clientHandler.close();
                    aClient.close();
                }
            } catch (SocketTimeoutException e) {System.out.println(e.getMessage());}
        }
        server.close();
    }

    public void close()
    {
        stopServer = true;
    }
}
