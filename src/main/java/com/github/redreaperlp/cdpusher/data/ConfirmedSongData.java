package com.github.redreaperlp.cdpusher.data;

import org.json.JSONObject;

public class ConfirmedSongData extends SongData {
    public boolean spotifyMissmatch = false;

    public ConfirmedSongData(){}
    public ConfirmedSongData(SongData songData) {
        super(songData);
        if (songData instanceof ConfirmedSongData confirmedSongData) {
            this.spotifySearch = confirmedSongData.spotifySearch;
            this.spotifyMissmatch = confirmedSongData.spotifyMissmatch;
        }
    }

    public ConfirmedSongData(long songID, String title, String artist, String album, int trackNo, int year, int discNo, long timeInSeconds, String imageURI) {
        super(songID, title, artist, album, trackNo, year, discNo, timeInSeconds, imageURI);
    }

    public boolean isSpotifyMissmatch() {
        return spotifyMissmatch;
    }

    public void setSpotifyMissmatch(boolean spotifyMissmatch) {
        this.spotifyMissmatch = spotifyMissmatch;
    }


    @Override
    public JSONObject toJSON() {
        return super.toJSON()
                .put(SongDataKey.SPOTIFY_SEARCH.getKey(), spotifySearch)
                .put(SongDataKey.SPOTIFY_MISSMATCH.getKey(), spotifyMissmatch);
    }
}
