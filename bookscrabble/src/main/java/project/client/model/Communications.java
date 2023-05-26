package project.client.model;

public interface Communications {
    public void start();
    public void run() throws Exception;
    public void close();
}