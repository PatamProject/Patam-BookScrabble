package bookscrabble.server.serverHandler;

import java.io.InputStream;
import java.io.OutputStream;

public interface ClientHandler {
	void handleClient(InputStream inFromclient, OutputStream outToClient, String ip);
	void close();
}
