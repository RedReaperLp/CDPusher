package com.github.redreaperlp.cdpusher.http;

import com.github.redreaperlp.cdpusher.data.disc.DiscInformation;
import com.github.redreaperlp.cdpusher.data.song.Song;
import com.github.redreaperlp.cdpusher.data.song.SongData;
import com.github.redreaperlp.cdpusher.data.song.SongDataKeys;
import com.github.redreaperlp.cdpusher.data.song.SongMismatch;
import com.github.redreaperlp.cdpusher.user.User;
import com.github.redreaperlp.cdpusher.user.WebsocketSession;
import org.json.JSONObject;

import java.util.List;

public class Topic {

    public enum Topics {
        DISC("disc", Disc.class),
        SEARCH("search", Search.class),
        SONGS("songs", Songs.class),
        USER("user", UserTopic.class);

        private final String value;
        private final Class<? extends Enum<?>> enumClass;

        Topics(String value, Class<? extends Enum<?>> enumClass) {
            this.value = value;
            this.enumClass = enumClass;
        }

        public String getValue() {
            return value;
        }

        public Class<? extends Enum<?>> getEnumClass() {
            return enumClass;
        }

        public static Topics findByTopicValue(String value) {
            for (Topics topic : Topics.values()) {
                if (topic.getValue().equals(value)) {
                    return topic;
                }
            }
            return null;
        }
    }

    public static <E extends Enum<E> & HasValue> E findEnumByValue(String topic, String value) {
        Topics topicEnum = Topics.findByTopicValue(topic);
        if (topicEnum == null) {
            return null;
        }

        Class<? extends Enum<?>> rawEnumClass = topicEnum.getEnumClass();
        if (HasValue.class.isAssignableFrom(rawEnumClass)) {
            @SuppressWarnings("unchecked")
            Class<E> enumClass = (Class<E>) rawEnumClass;
            return findEnumConstant(enumClass, value);
        }
        return null;
    }

    private static <E extends Enum<E> & HasValue> E findEnumConstant(Class<E> enumClass, String value) {
        for (E enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.getValue().equals(value)) {
                return enumConstant;
            }
        }
        return null;
    }

    public interface HasValue {
        String getValue();

        void handleRequest(JSONObject request, User user, WebsocketSession session);
    }

    public enum Disc implements HasValue {
        // Client -> Server
        SEARCH("search", null),
        PUSH_TO_DB("push_to_db", null),
        CLEAR("clear", null),


        // Client <- Server
        SUBMIT_DISC_INFO("submit_disc_info", null),

        // Client <- Server
        // Sending result in Popup Message
        ALREADY_EXISTS("already_exists", "Disc already exists"),
        STILL_MISMATCHES("still_mismatches", "There are still mismatches"),
        STILL_INDEXING("still_indexing", "Disc is still indexing"),
        PUSHED_TO_DB("pushed_to_db", "Disc pushed to database"),
        NOT_FOUND("not_found", "Disc not found"),
        SUCCESS("success", "Disc found"),
        FAILED("failed", "Internal Server Error");

        private final String value;
        private final String description;

        Disc(String value, String description) {
            this.value = value;
            this.description = description;
        }

        @Override
        public String getValue() {
            return value;
        }

        public JSONObject fillResponse(Request req) {
            JSONObject object = req.createRequest().put("topic", "disc")
                    .put("type", value);
            if (description != null) object.put("description", description);
            return object;
        }

        @Override
        public void handleRequest(JSONObject request, User user, WebsocketSession session) {
            switch (this) {
                case PUSH_TO_DB -> {
                    if (user.isSearching()) {
                        session.send(Topic.Disc.STILL_INDEXING
                                .fillResponse(Topic.Request.ERROR)
                                .toString());
                        return;
                    }
                    List<SongData> songs = user.allSongs().stream()
                            .filter(s -> s instanceof SongMismatch)
                            .toList();
                    if (songs.isEmpty()) {
                        user.finish();
                    } else {
                        session.send(Topic.Disc.STILL_MISMATCHES
                                .fillResponse(Topic.Request.ERROR)
                                .put("songs", songs.stream().map(SongData::toJSON).toList()).toString());
                    }
                }
                case CLEAR -> {
                    if (user.isSearching()) {
                        session.send(Topic.Disc.STILL_INDEXING
                                .fillResponse(Topic.Request.ERROR)
                                .toString());
                        return;
                    }
                    user.clearSongs();
                }
            }
        }
    }

    public enum Search implements HasValue {
        // Received

        // Server -> Client
        FINISHED("finished", null),
        FAILED("failed", null),
        ADD("add", null),

        // Client <-> Server
        START("start", "Search started");

        private final String value;
        private final String description;

        Search(String value, String description) {
            this.value = value;
            this.description = description;
        }

        @Override
        public String getValue() {
            return value;
        }

        public JSONObject fillResponse(Request req) {
            return req.createRequest().put("topic", "search")
                    .put("type", value)
                    .put("description", description);
        }

        @Override
        public void handleRequest(JSONObject request, User user, WebsocketSession session) {
            switch (this) {
                case START -> {
                    if (user.isSearching()) {
                        session.send(Disc.STILL_INDEXING.fillResponse(Request.SEARCH).toString());
                    } else {
                        user.clearSongs();
                        user.setSearching(true);
                        user.broadcastMessage(Search.START.fillResponse(Request.SUCCESS).toString());
                        searchSong(request.getString("ean").replace(" ", ""), user);
                    }
                }
            }
        }

        private static void searchSong(String ean, User user) {
            new Thread(() -> {
                DiscInformation disc = DiscOgsSearch.getInstance().searchEan(ean);
                if (disc == null) {
                    user.broadcastMessage(Disc.NOT_FOUND.fillResponse(Request.ERROR)
                            .put("ean", ean)
                            .toString());
                    user.setSearching(false);
                    return;
                }
                user.setDisc(disc);
                user.broadcastMessage(Disc.SUBMIT_DISC_INFO.fillResponse(Request.SUCCESS)
                        .put("info", disc.toJSON()).toString());
                disc.loadTracks(user);
                user.setSearching(false);
                user.broadcastMessage(Search.FINISHED.fillResponse(Request.SUCCESS).toString());
            }, "SongSearch").start();
        }
    }

    public enum Songs implements HasValue {
        // Server -> Client,
        NOT_FOUND("not_found", null),

        // Client -> Server,
        USE_DISCOGS("use_discogs", null),

        // Client <-> Server,
        UPDATE("update", null),
        ;
        private final String value;
        private final String description;

        Songs(String value, String description) {
            this.value = value;
            this.description = description;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public void handleRequest(JSONObject request, User user, WebsocketSession session) {
            switch (this) {
                case UPDATE -> {
                    String songURI = request.getString("uri");
                    long songID = request.getLong(SongDataKeys.SONG_ID.getKey());

                    Song song = SpotifySearch.getInstance().searchSong(songID, songURI);
                    user.allSongs().stream().filter(s -> s.songID == songID).findFirst()
                            .ifPresent(s -> {
                                song.setDiscNo(s.getDiscNo());
                                song.setTrackNo(s.getTrackNo());
                            });
                    user.setSong(song);
                }
                case USE_DISCOGS -> {
                    session.send(Search.START.fillResponse(Request.SUCCESS).toString());
                    var res = user.allSongs().stream().filter(s -> {
                        return s.getDiscNo() == request.getInt(SongDataKeys.DISC_NO.getKey()) && s.getTrackNo() == request.getInt(SongDataKeys.TRACK_NO.getKey());
                    }).findFirst();
                    if (res.isPresent()) {
                        if (res.get() instanceof SongMismatch info) {
                            var song = new Song(info);
                            song.spotifySearch = true;
                            song.spotifyMismatch = false;
                            user.setSong(song);
                        }
                    } else {
                        user.broadcastMessage(fillResponse(Request.ERROR)
                                .put("status", "not-found").toString());
                    }
                }
            }
        }

        public JSONObject fillResponse(Request req) {
            return req.createRequest().put("topic", "songs")
                    .put("type", value)
                    .put("description", description);
        }
    }

    public enum UserTopic implements HasValue {
        // Client -> Server
        PING("ping", null),
        LOGIN("login", null),

        // Client <- Server
        NOT_LOGGED_IN("not_logged_in", null),
        INVALID_REQUEST("missing_data", null);

        private final String value;
        private final String description;

        UserTopic(String value, String description) {
            this.value = value;
            this.description = description;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public void handleRequest(JSONObject request, User user, WebsocketSession session) {
            switch (this) {
                case PING -> {
                    user.resetDump();
                }
            }
        }

        public JSONObject fillResponse(Request request) {
            var res = request.createRequest().put("topic", "user")
                    .put("type", value);
            if (description != null) res.put("description", description);
            return res;
        }
    }

    public enum Request {
        UPDATE("update"),
        ADD("add"),
        GET("get"),
        SEARCH("search"),
        LOGIN("login"),
        ERROR("error"),
        SUCCESS("success"),
        INVALID("invalid");

        private final String value;

        Request(String value) {
            this.value = value;
        }

        public JSONObject createRequest() {
            return new JSONObject().put("request", value);
        }
    }
}
