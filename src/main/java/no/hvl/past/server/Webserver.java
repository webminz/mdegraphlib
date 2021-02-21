package no.hvl.past.server;

import io.javalin.Javalin;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

public class Webserver {

    private final Javalin javalin;

    private Webserver(Javalin javalin) {
        this.javalin = javalin;
    }

    public void registerOptions(WebserviceRequestHandler handler, Map<String, String> options) {
            this.javalin.options(handler.getContextPath(), ctx -> {
                HttpServletResponse response = ctx.res;
                for (Map.Entry<String, String> entries : options.entrySet()) {
                    response.setHeader(entries.getKey(), entries.getValue());
                }
                response.setHeader("Content-Type", null);
                response.setHeader("Content-Length", "0");

                response.setStatus(204);
            });

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

    public static Webserver start(int port) {
        Javalin javalin = Javalin.create().start(port);
        return new Webserver(javalin);
    }
}
