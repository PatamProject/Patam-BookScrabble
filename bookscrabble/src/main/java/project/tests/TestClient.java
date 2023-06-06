package project.tests;

import project.client.RunClient;
import project.client.model.ClientCommunications;
import project.client.model.ClientModel;
import project.client.model.ClientSideHandler;

public class TestClient {
    public static void main(String[] args) {
        new RunClient();
        

        ClientCommunications.sendAMessage(ClientSideHandler.getId(), ClientModel.getName()+"&leave");
    }
}
