package com.github.redreaperlp.cdpusher.data;

public class DataKeys {
    public static enum SongData {
        TITLE("title"),
        ARTIST("artist"),
        ALBUM("album"),
        TRACK_NO("trackNo"),
        DISC_NO("discNo"),
        YEAR("year"),
        DURATION("duration"),
        INTERNAL_DISC_NO("internalDiscNo"),
        COVER_URI("coverURI"),
        SPOTIFY_SEARCH("spotifySearch"),
        SPOTIFY_MISSMATCH("spotifyMissmatch"),
        SPOTIFY_MISSMATCH_DATA("spotifyMissmatchData"),
        TRACK_ID("trackID");

        private String key;

        private SongData(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public static SongData fromKey(String key) {
            for (SongData data : values()) {
                if (data.getKey().equals(key)) {
                    return data;
                }
            }
            return null;
        }
    }
}
