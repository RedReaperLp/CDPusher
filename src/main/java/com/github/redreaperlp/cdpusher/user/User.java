package com.github.redreaperlp.cdpusher.user;

import com.github.redreaperlp.cdpusher.Song;
import com.github.redreaperlp.cdpusher.data.SongData;
import com.github.redreaperlp.cdpusher.util.logger.types.TestPrinter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class User {
    private final List<SongData> songs = new ArrayList<>();
    private LocalDateTime dumpUser = LocalDateTime.now().plusMinutes(1);
    private final List<WebsocketSession> sessions = new ArrayList<>();
    private final String username;

    private boolean searching = false;

    public User(String username) {
        this.username = username;
    }

    public void clearSongs() {
        songs.clear();
        broadcastMessage(new JSONObject().put("request", "clear-songs").toString());
    }

    public void addSong(SongData song) {
        songs.add(song);
        broadcastMessage(new JSONObject().put("request", "song-response").put("song", song.toJSON()).toString());
    }

    public void setSong(Song song) {
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getSongID() == song.getSongID()) {
                songs.set(i, song);
                broadcastMessage(new JSONObject().put("request", "song-update").put("song", song.toJSON()).toString());
                return;
            }
        }
    }

    private void broadcastMessage(String string) {
        sessions.forEach(session -> session.send(string));
    }

    public JSONArray allSongsJSON() {
        JSONArray array = new JSONArray();
        for (SongData song : songs) {
            array.put(song.toJSON());
        }
        return array;
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

    public void resetDump() {
        dumpUser = LocalDateTime.now().plusMinutes(1);
    }

    public void removeSession(WebsocketSession websocketSession) {
        sessions.remove(websocketSession);
    }

    public void addSession(WebsocketSession websocketSession) {
        sessions.add(websocketSession);
        new TestPrinter().append("Added session to " + username).print();
    }

    public void setSearching(boolean searching) {
        this.searching = searching;
    }

    public boolean isSearching() {
        return searching;
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