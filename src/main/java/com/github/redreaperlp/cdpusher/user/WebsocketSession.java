package com.github.redreaperlp.cdpusher.user;

import com.github.redreaperlp.cdpusher.DiscInformation;
import com.github.redreaperlp.cdpusher.Song;
import com.github.redreaperlp.cdpusher.TrackInformation;
import com.github.redreaperlp.cdpusher.data.DataKeys;
import com.github.redreaperlp.cdpusher.data.SongData;
import com.github.redreaperlp.cdpusher.http.DiscOgsSearch;
import com.github.redreaperlp.cdpusher.http.SpotifySearch;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.json.JSONObject;

import java.util.List;

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
                    if (checkUser()) return;
                    String songURI = message.getString("uri");
                    long songID = message.getLong(DataKeys.SongData.TRACK_ID.getKey());
                    Song song = SpotifySearch.getInstance().searchSong(songID, songURI);
                    user.setSong(song);
                }
                case SEARCH -> {
                    if (checkUser()) return;
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
                case USE_DISCOGS -> {
                    if (checkUser()) return;
                    var res = user.allSongs().stream().filter(s -> {
                        return s.getDiscNo() == message.getInt("discNo") && s.getTrackNo() == message.getInt("trackNo");
                    }).findFirst();
                    if (res.isPresent()) {
                        if (res.get() instanceof TrackInformation info) {
                            var song = info.toSong();
                            user.setSong(song);
                        }
                    } else {
                        send(new JSONObject().put("request", "use-discogs")
                                .put("status", "not-found").toString());
                    }
                }
                case PUSH_DATABASE -> {
                    if (checkUser()) return;
                    List<SongData> songs = user.allSongs().stream()
                            .filter(s -> s instanceof TrackInformation)
                            .toList();
                    if (songs.isEmpty()) {
                        send(new JSONObject().put("request", "push-database")
                                .put("status", "no-missmatch").toString());
                    } else {
                        send(new JSONObject().put("request", "push-database")
                                .put("status", "missmatches")
                                .put("songs", songs.stream().map(SongData::toJSON).toList()).toString());
                    }
                }
                case LOGIN -> {
                    var username = message.getString("username");
                    UserManager.getInstance().getUser(username).ifPresent(user -> {
                        this.user = user;
                        user.addSession(this);
                    });
                }
                case CLEAR_SONGS -> {
                    if (checkUser()) return;
                    user.clearSongs();
                }
                default -> {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkUser() {
        if (user == null) {
            send(new JSONObject().put("request", "not-logged-in").toString());
            return true;
        }
        return false;
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
