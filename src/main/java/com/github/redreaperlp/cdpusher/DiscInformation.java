package com.github.redreaperlp.cdpusher;

import com.github.redreaperlp.cdpusher.http.DiscOgsSearch;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DiscInformation {
    private String country;
    private String year;
    private String title;
    private String[] labels;
    private String resourceURL;

    private List<List<TrackInformation>> songs = new ArrayList<>();

    //Todo: fix track with url: example https://open.spotify.com/intl-de/track/4D9cEbNYC8AVSl7d4mLrip?si=a531873bc03647be
    //ID here is 4D9cEbNYC8AVSl7d4mLrip
    public DiscInformation(JSONObject requestResponse) {
        if (requestResponse.has("country")) this.country = requestResponse.getString("country");
        if (requestResponse.has("year")) this.year = requestResponse.getString("year");
        if (requestResponse.has("title")) this.title = requestResponse.getString("title");
        if (requestResponse.has("label"))
            this.labels = requestResponse.getJSONArray("label").toList().stream().map(Object::toString).toArray(String[]::new);
        if (requestResponse.has("resource_url")) this.resourceURL = requestResponse.getString("resource_url");
    }

    public void loadTracks() {
        int failed = 0;
        JSONArray tracks = DiscOgsSearch.getInstance().searchDiscTracks(this);
        List<TrackInformation> tracksList = new ArrayList<>();
        if (tracks == null) {
            System.out.println("Failed to load tracks");
            return;
        }
        for (int i = 0; i < tracks.length(); i++) {
            TrackInformation t = new TrackInformation(tracks.getJSONObject(i));
            t.spotifySearch();
            tracksList.add(t);
            if (t.isSpotifySearchMissMatch() || !t.isSpotifySearch()) {
                failed++;
                continue;
            }
        }
        this.songs.add(tracksList);
        System.out.println("Failed: " + failed + "/" + tracks.length());
    }

    public String getCountry() {
        return country;
    }

    public String getYear() {
        return year;
    }

    public String getTitle() {
        return title;
    }

    public String[] getLabels() {
        return labels;
    }

    public String getResourceURL() {
        return resourceURL;
    }

    public List<List<TrackInformation>> getSongs() {
        return songs;
    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        object.put("country", country);
        object.put("year", year);
        object.put("title", title);
        object.put("labels", labels);
        object.put("resourceURL", resourceURL);

        JSONArray songs = new JSONArray();
        for (List<TrackInformation> trackList : this.songs) {
            for (TrackInformation track : trackList) {
                songs.put(track.toJSON());
            }
        }

        object.put("tracks", new JSONArray(songs));
        return object;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
}
