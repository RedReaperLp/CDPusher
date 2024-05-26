package com.github.redreaperlp.cdpusher.hibernate;

import com.github.redreaperlp.cdpusher.data.DiscOGsSong;
import com.github.redreaperlp.cdpusher.data.SongData;
import com.github.redreaperlp.cdpusher.http.DiscOgsSearch;
import com.github.redreaperlp.cdpusher.user.User;
import com.github.redreaperlp.cdpusher.util.logger.types.InfoPrinter;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

@Entity
@Table(name = "discs")
public class DiscInformation {
    @Id
    private long id = 0;
    private String country;
    private String year;
    private String title;
    private String label;
    private String resourceURL;
    @OneToMany()
    private List<Song> songs;

    public DiscInformation() {}

    public DiscInformation(JSONObject requestResponse) {
        if (requestResponse.has("country")) this.country = requestResponse.getString("country");
        if (requestResponse.has("year")) this.year = requestResponse.getString("year");
        if (requestResponse.has("title")) this.title = requestResponse.getString("title");
        if (requestResponse.has("label"))
            this.label = requestResponse.getJSONArray("label").toList().stream().map(Object::toString).toArray(String[]::new)[0];
        if (requestResponse.has("resource_url")) this.resourceURL = requestResponse.getString("resource_url");
    }

    public void loadTracks(User requester) {
        var songs = DiscOgsSearch.getInstance().searchDiscTracks(this);
        if (songs.isEmpty()) {
            new InfoPrinter().append("Failed to load tracks for " + title).print();
            return;
        }
        int i = 0;
        for (DiscOGsSong song : songs) {
            song.songID = i++;
            var validation = song.spotifySearch();
            requester.addSong(validation);
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

    public String getLabel() {
        return label;
    }

    public String getResourceURL() {
        return resourceURL;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        object.put("country", country);
        object.put("year", year);
        object.put("title", title);
        object.put("labels", label);
        object.put("resourceURL", resourceURL);
        object.put("id", id);


        JSONArray songs = new JSONArray();
        if (this.songs != null) {
            for (Song song : this.songs) {
                songs.put(song.toJSON());
            }
        }

        object.put("songs", songs);
        return object;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
}
