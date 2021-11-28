package no.hvl.past.server;

import io.javalin.Javalin;
import io.javalin.core.event.EventHandler;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

public class Webserver {

    private final Javalin javalin;

    private Webserver(Javalin javalin) {
        this.javalin = javalin;
    }


    public void registerHandler(WebserviceRequestHandler handler) {
        switch (handler.getMethod()) {
            case GET:
                this.javalin.get(handler.getContextPath(), handler);
                break;
            case PUT:
                this.javalin.put(handler.getContextPath(), handler);
                break;
            case POST:
                this.javalin.post(handler.getContextPath(), handler);
                break;
            case DELETE:
                this.javalin.delete(handler.getContextPath(), handler);
                break;
                case HEAD:
                this.javalin.head(handler.getContextPath(), handler);
                break;
            case PATCH:
                this.javalin.patch(handler.getContextPath(), handler);
                break;
        }

    }

    public void shutdown() {
        this.javalin.stop();
    }

    public static Webserver start(int port, EventHandler startedHandler, EventHandler stoppedHandler) {
        Javalin javalin = Javalin.create(config -> {
            config.enableCorsForAllOrigins();
        }).events(event -> {
            event.serverStarted(startedHandler);
            event.serverStopped(stoppedHandler);
        }).start(port);
        return new Webserver(javalin);
    }
}
