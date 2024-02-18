package com.github.redreaperlp.cdpusher.data;

import org.json.JSONObject;

public interface SongData {
    JSONObject toJSON();

    int getDiscNo();
    int getTrackNo();

    long getSongID();
}
