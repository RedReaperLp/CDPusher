package com.github.redreaperlp.cdpusher.user;

import com.github.redreaperlp.cdpusher.http.Topic;
import com.github.redreaperlp.cdpusher.util.logger.types.ErrorPrinter;
import com.github.redreaperlp.cdpusher.util.logger.types.TestPrinter;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.json.JSONObject;

public class WebsocketSession implements WebSocketListener {
    User user;
    WsContext ctx;
    int id;
    public static int nextID = 0;

    public WebsocketSession(WsContext ctx) {
        this.ctx = ctx;
        id = nextID++;
    }

    public void onMessage(WsMessageContext ctx) {
        try {

            JSONObject message = new JSONObject(ctx.message());

            if (user == null) {
                if (message.getString("request").equals("login")) {
                    var username = message.getString("username");
                    UserManager.getInstance().getUser(username).ifPresent(user -> {
                        this.user = user;
                        user.addSession(this);
                    });
                }
                return;
            }
            if (!isUserLoggedIn()) {
                return;
            }
            user.resetDump();

            if (!message.has("topic") || !message.has("request")) {
                send(Topic.UserTopic.INVALID_REQUEST.fillResponse(Topic.Request.ERROR)
                        .put("sent", message).toString());
                return;
            }

            var l = Topic.findEnumByValue(message.getString("topic"), message.getString("request"));
            if (l != null) {
                l.handleRequest(message, user, this);
            } else {
                new TestPrinter().append(message.toString(1)).print();
            }
        } catch (Exception e) {
            new ErrorPrinter().appendException(e).print();
        }
    }

    private boolean isUserLoggedIn() {
        if (user == null) {
            send(Topic.UserTopic.NOT_LOGGED_IN.fillResponse(Topic.Request.ERROR).toString());
            return false;
        }
        return true;
    }

    public void send(String string) {
        if (ctx.session.isOpen()) {
            ctx.send(string);
        }
    }


    private enum Request {
        GET("get"),
        ADD("add"),
        UPDATE("update"),
        SEARCH("search"),
        REMOVE("remove"),
        LOGIN("login"),
        CLEAR_SONGS("clear-songs"),
        PUSH_DATABASE("push"),
        USE_DISCOGS("use-discogs"),
        UNKNOWN("unknown");

        private final String request;

        Request(String request) {
            this.request = request;
        }

        public String getRequest() {
            return request;
        }

        public static Request fromString(String request) {
            for (Request r : Request.values()) {
                if (r.request.equals(request)) {
                    return r;
                }
            }
            return UNKNOWN;
        }
    }
}
