package com.github.redreaperlp.cdpusher;

import com.github.redreaperlp.cdpusher.database.DatabaseManager;
import com.github.redreaperlp.cdpusher.http.DiscOgsSearch;
import com.github.redreaperlp.cdpusher.user.User;
import com.github.redreaperlp.cdpusher.util.logger.types.InfoPrinter;
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

    private long trackidCounter = DatabaseManager.getInstance().getHighestID();

    private final List<List<TrackInformation>> songs = new ArrayList<>();

    public DiscInformation(JSONObject requestResponse) {
        if (requestResponse.has("country")) this.country = requestResponse.getString("country");
        if (requestResponse.has("year")) this.year = requestResponse.getString("year");
        if (requestResponse.has("title")) this.title = requestResponse.getString("title");
        if (requestResponse.has("label"))
            this.labels = requestResponse.getJSONArray("label").toList().stream().map(Object::toString).toArray(String[]::new);
        if (requestResponse.has("resource_url")) this.resourceURL = requestResponse.getString("resource_url");
    }

    public void loadTracks(User requester) {
        int failed = 0;
        JSONArray tracks = DiscOgsSearch.getInstance().searchDiscTracks(this);
        if (tracks == null) {
            new InfoPrinter().append("Failed to load tracks for " + title).print();
            return;
        }
        for (int i = 0; i < tracks.length(); i++) {
            TrackInformation t = new TrackInformation(trackidCounter++, tracks.getJSONObject(i));
            requester.addSong(t.spotifySearch());
        }
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
