package com.github.redreaperlp.cdpusher.data.song;

public enum SongDataKeys {
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
    SPOTIFY_MISMATCH("spotify_mismatch"),
    SPOTIFY_MISMATCH_DATA("spotify_mismatch_data");

    private final String key;

    SongDataKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static SongDataKeys fromKey(String key) {
        for (SongDataKeys data : values()) {
            if (data.getKey().equals(key)) {
                return data;
            }
        }
        return null;
    }
}
