package project.client.model;

import java.io.InputStream;
import java.io.OutputStream;

public interface RequestHandler {
	void handleClient(InputStream inFromclient, OutputStream outToClient);
	void close();
}
