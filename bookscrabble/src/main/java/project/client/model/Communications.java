package project.client.model;


public abstract class Communications {
    private RequestHandler requestHandler;

    public Communications(RequestHandler rh) {this.requestHandler = rh;} // Ctor

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
    protected RequestHandler getRequestHandler() {return requestHandler;} // Getter
    public void close() {}
}