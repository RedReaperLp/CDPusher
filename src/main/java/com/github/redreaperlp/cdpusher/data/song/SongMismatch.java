package com.github.redreaperlp.cdpusher.data.song;

import com.github.redreaperlp.cdpusher.data.disc.DiscOGsSong;
import org.json.JSONObject;

public class SongMismatch extends Song {
    public Song spotifySong;

    public SongMismatch(DiscOGsSong discOGsSong, Song spotifyResult) {
        super(discOGsSong);
        this.spotifyResult = spotifyResult;
        this.setSpotifyMismatch(true);
    }

    public Song spotifyResult;

    @Override
    public JSONObject toJSON() {
        if (spotifySong == null) {
            return super.toJSON();
        }
        return super.toJSON().put(SongDataKeys.SPOTIFY_MISMATCH_DATA.getKey(), spotifySong.toJSON());
    }

    public Song getSpotifyResult() {
        return spotifySong;
    }
}
