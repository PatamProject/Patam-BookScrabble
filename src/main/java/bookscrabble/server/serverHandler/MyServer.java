package bookscrabble.server.serverHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MyServer {
    private final ClientHandler clientHandler;
    ExecutorService threadPool;
    private final int port;
    private volatile boolean stopServer = false;
    private final int maxClients = 4;

	public MyServer(int port, ClientHandler ch)
    {
        this.clientHandler = ch;
        this.port = port;
        stopServer = false;
    }

    public void start()
    {
        threadPool = Executors.newFixedThreadPool(maxClients);
        new Thread(()->{
            try {
                run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void run() {     
        try (ServerSocket server = new ServerSocket(port)) {
            server.setSoTimeout(360 * 1000); // Timeout in seconds
            while (!stopServer) {
                try {
                    Socket aClient = server.accept();
                    threadPool.execute(() -> {
                        try {
                            clientHandler.handleClient(aClient.getInputStream(), aClient.getOutputStream());
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
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

    public void close() {
        stopServer = true;
    }
}
