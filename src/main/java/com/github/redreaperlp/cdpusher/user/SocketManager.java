package com.github.redreaperlp.cdpusher.user;

import io.javalin.websocket.WsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SocketManager {
    private static SocketManager instance;

    private SocketManager() {
    }

    public static SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    private List<WebsocketSession> sessions = new ArrayList<>();

    public void addSession(WebsocketSession session) {
        sessions.add(session);
    }

    public void removeSession(WebsocketSession session) {
        sessions.remove(session);
    }

    public Optional<WebsocketSession> getSession(WsContext ctx) {
        for (WebsocketSession session : sessions) {
            if (session.ctx.equals(ctx)) {
                return Optional.of(session);
            }
        }
        return Optional.empty();
    }
}
