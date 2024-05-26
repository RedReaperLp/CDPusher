package com.github.redreaperlp.cdpusher.data;

import com.github.redreaperlp.cdpusher.hibernate.Song;
import org.json.JSONObject;

public class SongMissmatch extends Song {
    public Song spotifySong;

    public SongMissmatch(DiscOGsSong discOGsSong, Song spotifyResult) {
        super(discOGsSong);
        this.spotifyResult = spotifyResult;
        this.setSpotifyMissmatch(true);
    }

    public Song spotifyResult;

    @Override
    public JSONObject toJSON() {
        if (spotifySong == null) {
            return super.toJSON();
        }
        return super.toJSON().put(SongDataKey.SPOTIFY_MISSMATCH_DATA.getKey(), spotifySong.toJSON());
    }

    public Song getSpotifyResult() {
        return spotifySong;
    }
}
