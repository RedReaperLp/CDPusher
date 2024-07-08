package com.github.redreaperlp.cdpusher.user;

import com.github.redreaperlp.cdpusher.data.disc.DiscInformation;
import com.github.redreaperlp.cdpusher.data.song.Song;
import com.github.redreaperlp.cdpusher.data.song.SongData;
import com.github.redreaperlp.cdpusher.http.Topic;
import com.github.redreaperlp.cdpusher.util.logger.types.TestPrinter;
import org.json.JSONArray;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {
    private final List<SongData> songs = new ArrayList<>();
    private LocalDateTime dumpUser = LocalDateTime.now().plusMinutes(1);
    private final List<WebsocketSession> sessions = new ArrayList<>();
    private final String username;
    private DiscInformation disc;

    private boolean searching = false;

    public User(String username) {
        this.username = username;
    }

    public void clearSongs() {
        songs.clear();
        broadcastMessage(Topic.Disc.CLEAR.fillResponse(Topic.Request.UPDATE).toString());
    }

    public void addSong(SongData song) {
        songs.add(song);
        broadcastMessage(Topic.Songs.UPDATE.fillResponse(Topic.Request.UPDATE)
                .put("song", song.toJSON())
                .toString());
    }

    public void setSong(Song song) {
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getSongID() == song.getSongID()) {
                songs.set(i, song);
                broadcastMessage(Topic.Songs.UPDATE.fillResponse(Topic.Request.UPDATE)
                        .put("song", song.toJSON())
                        .toString());
                return;
            }
        }
    }

    public void broadcastMessage(String string) {
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
            return true;
        }
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
    }

    public void setSearching(boolean searching) {
        this.searching = searching;
    }

    public boolean isSearching() {
        return searching;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User user) {
            return user.username.equals(username);
        }
        return false;
    }

    public List<SongData> allSongs() {
        return songs;
    }

    public void finish() {
        if (disc == null) {
            broadcastMessage(Topic.Disc.NOT_FOUND.fillResponse(Topic.Request.ERROR).toString());
            return;
        }
        List<Song> songs = new ArrayList<>();
        var res = disc.pushToDB();
        if (res.isErrored()) {
            switch (res.getStatus()) {
                case 409 -> broadcastMessage(Topic.Disc.ALREADY_EXISTS
                        .fillResponse(Topic.Request.ERROR)
                        .put("conflict", res.getValue())
                        .toString());
                case 500 -> broadcastMessage(Topic.Disc.FAILED
                        .fillResponse(Topic.Request.ERROR)
                        .put("error", res.getError())
                        .toString());
            }
            return;
        }

        for (SongData song : this.songs) {
            if (song instanceof Song song1) {
                songs.add(song1);
                song1.setDiscID(disc.getId());
            }
        }
        disc.setSongs(songs);
        disc.pushSongsToDB();

        clearSongs();
        broadcastMessage(Topic.Disc.PUSHED_TO_DB
                .fillResponse(Topic.Request.SUCCESS)
                .put("disc_id", res.getValue())
                .toString());
    }

    public void setDisc(DiscInformation disc) {
        this.disc = disc;
    }

    public DiscInformation getDisc() {
        return disc;
    }
}