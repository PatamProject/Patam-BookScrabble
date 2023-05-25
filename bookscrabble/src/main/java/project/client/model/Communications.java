package project.client.model;


public abstract class Communications {
    private RequestHandler requestHandler;

    public Communications(RequestHandler hostSideHandler) {this.requestHandler = hostSideHandler;} // Ctor

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
    protected Communications getCommunications() {return this;} // Getter
    public void close() {}
}