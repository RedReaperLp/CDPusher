package com.github.redreaperlp.cdpusher.user;

import com.github.redreaperlp.cdpusher.DiscInformation;
import com.github.redreaperlp.cdpusher.Song;
import com.github.redreaperlp.cdpusher.data.DataKeys;
import com.github.redreaperlp.cdpusher.data.SongData;
import com.github.redreaperlp.cdpusher.http.DiscOgsSearch;
import com.github.redreaperlp.cdpusher.http.SpotifySearch;
import com.github.redreaperlp.cdpusher.util.logger.types.TestPrinter;
import io.javalin.websocket.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class User {
    private List<SongData> songs = new ArrayList<>();
    private LocalDateTime dumpUser = LocalDateTime.now().plusMinutes(1);
    private String username;

    WsContext ctx;

    public void onConnect(WsConnectContext ctx) {
        this.ctx = ctx;
    }

    public void onClose(WsCloseContext ctx) {
        new TestPrinter().append("User disconnected").print();
    }

    public void onMessage(WsMessageContext ctx) {
        try {
            dumpUser = LocalDateTime.now().plusMinutes(1);
            JSONObject message = new JSONObject(ctx.message());
            switch (Request.fromString(message.getString("request"))) {
                case GET -> {
                }
                case ADD -> {
                }
                case UPDATE -> {
                    String songURI = message.getString("uri");
                    long songID = message.getLong(DataKeys.SongData.TRACK_ID.getKey());
                    Song song = SpotifySearch.getInstance().searchSong(songID, songURI);
                    for (int i = 0; i < songs.size(); i++) {
                        if (songs.get(i).getSongID() == songID) {
                            SongData s = songs.get(i);
                            song.setIDs(s.getTrackNo(), s.getDiscNo());
                            songs.set(i, song);
                            break;
                        }
                    }
                    ctx.send(new JSONObject().put("request", "song-update").put("song", song.toJSON()).toString());
                }
                case SEARCH -> {
                    String ean = message.getString("ean").replace(" ", "");
                    new TestPrinter().append("Searching for " + ean).print();
                    DiscInformation disc = DiscOgsSearch.getInstance().searchEan(ean);
                    disc.loadTracks(this);
                }
                case REMOVE -> {
                }
                case LOGIN -> {
                    username = message.getString("username");
                    UserManager.getInstance().addUser(this);
                }
                case CLEAR_SONGS -> {
                    songs.clear();
                }
                default -> {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addSong(SongData song) {
        songs.add(song);
        ctx.send(new JSONObject().put("request", "song-response").put("song", song.toJSON()).toString());
    }

    public JSONArray allSongsJSON() {
        JSONArray array = new JSONArray();
        for (SongData song : songs) {
            array.put(song.toJSON());
        }
        return array;
    }

    public Consumer<WsConfig> getHandler() {
        return (config) -> {
            config.onConnect(this::onConnect);
            config.onClose(this::onClose);
            config.onMessage(this::onMessage);
        };
    }

    public void apply(User toApply) {
        this.songs = toApply.songs;
    }

    public String getUsername() {
        return username;
    }

    public boolean shouldDump() {
        if (LocalDateTime.now().isAfter(dumpUser)) {
            new TestPrinter().append("User " + username + " dumped").print();
            return true;
        }
        new TestPrinter().append("Remaining time for " + username + " is " + (dumpUser.toLocalTime().toEpochSecond(LocalDate.MAX, ZoneOffset.UTC) - LocalDateTime.now().toLocalTime().toEpochSecond(LocalDate.MAX, ZoneOffset.UTC))).print();
        return false;
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User user) {
            return user.username.equals(username);
        }
        return false;
    }
}
