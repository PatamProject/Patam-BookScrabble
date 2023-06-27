package bookscrabble.server.serverHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import bookscrabble.server.App;
import java.util.Observable;

public class MyServer extends Observable{
    private final ClientHandler clientHandler;
    Thread mainThread;
    ExecutorService threadPool;
    ServerSocket server;
    private final int port;
    private volatile boolean stopServer = false;
    private final int maxClients = 4;
    private int timeout = 360;

	public MyServer(int port, ClientHandler ch, int timeout)
    {
        this.clientHandler = ch;
        this.port = port;
        stopServer = false;
        this.timeout = timeout;
    }

    public MyServer(int port, ClientHandler ch)
    {
        this.clientHandler = ch;
        this.port = port;
        stopServer = false;
        this.timeout = 360;
    }

    public void start()
    {
        stopServer = false;
        threadPool = Executors.newFixedThreadPool(maxClients);
        mainThread = new Thread(this::run);
        mainThread.start();
    }

    public void run() {     
        try {
            int timeoutInMsc = timeout * 1000; // Timeout in seconds
            server = new ServerSocket(port);
            App.write("Server is running on port " + port + "\nTimeout is set to " + timeout + " seconds");
            server.setSoTimeout(timeoutInMsc); // Timeout in seconds
            while (!stopServer) {
                try {
                    Socket aClient = server.accept();
                    threadPool.execute(() -> {
                        try {
                            clientHandler.handleClient(aClient.getInputStream(), aClient.getOutputStream(), aClient.getInetAddress().getHostAddress());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            clientHandler.close();
                            try {
                                aClient.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (SocketTimeoutException e) {
                    App.write("Server timed out.");
                    close();
                    setChanged();
                    notifyObservers();
                } catch (IOException e) {
                    App.write("Server closed.\n");
                }
            }
        } catch (IOException e){
            App.write("Error: " + e.getMessage());
        } 
        finally {
            threadPool.shutdown();
            close();
        }
    }

    public void close() {
        stopServer = true;
        try {
            server.close();
        } catch (IOException e) {}
    }

    public boolean isAlive() {
        return mainThread.isAlive();
    }
}
