package project.client.model;


public abstract class New_Communications {
    private New_RequestHandler requestHandler;

    public New_Communications(New_RequestHandler hostSideHandler) {this.requestHandler = hostSideHandler;} // Ctor

    public void start() { // Starts run method by executing it in a separate thread
        new Thread(()-> {
            try {
                run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    protected void run() throws Exception {}
    protected New_RequestHandler getRequestHandler() {return requestHandler;} // Getter
    public void close() {}
}