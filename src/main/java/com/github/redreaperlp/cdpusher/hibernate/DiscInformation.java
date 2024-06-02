package com.github.redreaperlp.cdpusher.hibernate;

import com.github.redreaperlp.cdpusher.data.DiscOGsSong;
import com.github.redreaperlp.cdpusher.http.DiscOgsSearch;
import com.github.redreaperlp.cdpusher.user.User;
import com.github.redreaperlp.cdpusher.util.logger.types.ErrorPrinter;
import com.github.redreaperlp.cdpusher.util.logger.types.InfoPrinter;
import jakarta.persistence.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "discs")
public class DiscInformation implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "country")
    private String country;
    @Column(name = "year")
    private String year;
    @Column(name = "title")
    private String title;
    @Column(name = "label")
    private String label;
    @Column(name = "resource_url")
    private String resourceURL;

    @OneToMany(mappedBy = "discInformation", fetch = FetchType.EAGER)
    private List<Song> songs;

    public DiscInformation() {
    }

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

    public long getId() {
        return id;
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

    public void pushToDB() {
        try (var session = HibernateSession.getInstance().getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(this);
            session.getTransaction().commit();
        } catch (Exception e) {
            new ErrorPrinter().appendException(e).print();
        }
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setResourceURL(String resourceURL) {
        this.resourceURL = resourceURL;
    }
}
