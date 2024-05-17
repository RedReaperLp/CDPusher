package com.github.redreaperlp.cdpusher.user;

import com.github.redreaperlp.cdpusher.DiscInformation;
import com.github.redreaperlp.cdpusher.Song;
import com.github.redreaperlp.cdpusher.data.DataKeys;
import com.github.redreaperlp.cdpusher.http.DiscOgsSearch;
import com.github.redreaperlp.cdpusher.http.SpotifySearch;
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
            if (user != null) user.resetDump();
            JSONObject message = new JSONObject(ctx.message());
            switch (Request.fromString(message.getString("request"))) {
                case GET -> {
                }
                case ADD -> {
                }
                case UPDATE -> {
                    if (user == null) return;
                    String songURI = message.getString("uri");
                    long songID = message.getLong(DataKeys.SongData.TRACK_ID.getKey());
                    Song song = SpotifySearch.getInstance().searchSong(songID, songURI);
                    user.setSong(song);
                }
                case SEARCH -> {
                    if (user == null || user.isSearching()) {
                        return;
                    }
                    user.setSearching(true);
                    user.clearSongs();
                    new Thread(() -> {
                        String ean = message.getString("ean").replace(" ", "");
                        DiscInformation disc = DiscOgsSearch.getInstance().searchEan(ean);
                        disc.loadTracks(user);
                        user.setSearching(false);
                    }, "SongSearch").start();
                }
                case REMOVE -> {
                }
                case LOGIN -> {
                    var username = message.getString("username");
                    UserManager.getInstance().getUser(username).ifPresent(user -> {
                        this.user = user;
                        user.addSession(this);
                    });
                }
                case CLEAR_SONGS -> {
                    if (user == null) return;
                    user.clearSongs();
                }
                default -> {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String string) {
        new TestPrinter().append("Sent to " + id + ": " + string).print();
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
