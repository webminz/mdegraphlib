package no.hvl.past.di;

import no.hvl.past.server.Webserver;
import org.springframework.beans.factory.annotation.Autowired;

public class ServerStarter {

    @Autowired
    private PropertyHolder propertyHolder;
    private Webserver webserver;
    private boolean isRunning;


    public ServerStarter() {
        this.isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public synchronized Webserver getWebserverStartIfNecessary() {
        if (!isRunning) {
            this.webserver = Webserver.start(propertyHolder.getServerPort());
            this.isRunning = true;
        }
        return this.webserver;
    }

    public synchronized void stopServer() {
        this.webserver.shutdown();
        this.webserver = null;
        this.isRunning = false;
    }
}
