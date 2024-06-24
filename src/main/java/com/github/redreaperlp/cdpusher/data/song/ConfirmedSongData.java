package com.github.redreaperlp.cdpusher.data.song;

import org.json.JSONObject;

public class ConfirmedSongData extends SongData {
    public boolean spotifyMismatch = false;

    public ConfirmedSongData(SongData songData) {
        super(songData);
        if (songData instanceof ConfirmedSongData confirmedSongData) {
            this.spotifySearch = confirmedSongData.spotifySearch;
            this.spotifyMismatch = confirmedSongData.spotifyMismatch;
        }
    }

    public ConfirmedSongData(long songID, String title, String artist, String album, int trackNo, int year, int discNo, long timeInSeconds, String imageURI) {
        super(songID, title, artist, album, trackNo, year, discNo, timeInSeconds, imageURI);
    }

    public boolean isSpotifyMismatch() {
        return spotifyMismatch;
    }

    public void setSpotifyMismatch(boolean spotifyMismatch) {
        this.spotifyMismatch = spotifyMismatch;
    }


    @Override
    public JSONObject toJSON() {
        return super.toJSON()
                .put(SongDataKeys.SPOTIFY_SEARCH.getKey(), spotifySearch)
                .put(SongDataKeys.SPOTIFY_MISMATCH.getKey(), spotifyMismatch);
    }
}
