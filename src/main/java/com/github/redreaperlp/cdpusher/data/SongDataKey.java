package com.github.redreaperlp.cdpusher.data;

public enum SongDataKey {
    SONG_ID("song_id"),
    TITLE("song_title"),
    ARTISTS("song_artists"),
    ALBUM("song_album"),
    TRACK_NO("song_track_no"),
    DISC_NO("song_disc_no"),
    YEAR("song_year"),
    DURATION("song_duration"),
    INTERNAL_DISC_NO("disc_id"),
    COVER_URI("song_cover_uri"),
    SPOTIFY_SEARCH("spotify_search"),
    SPOTIFY_MISSMATCH("spotify_missmatch"),
    SPOTIFY_MISSMATCH_DATA("spotify_missmatch_data");

    private String key;

    private SongDataKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static SongDataKey fromKey(String key) {
        for (SongDataKey data : values()) {
            if (data.getKey().equals(key)) {
                return data;
            }
        }
        return null;
    }
}
