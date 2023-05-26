package project.client.model;

import java.io.OutputStream;

public interface RequestHandler {
	void handleClient(String sender,String commandName, String[] args, OutputStream outToClient);
	void close();
}
