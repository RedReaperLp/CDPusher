package com.github.redreaperlp.cdpusher.data.disc;

import com.github.redreaperlp.cdpusher.data.song.Song;
import com.github.redreaperlp.cdpusher.database.DatabaseManager;
import com.github.redreaperlp.cdpusher.http.DiscOgsSearch;
import com.github.redreaperlp.cdpusher.http.Topic;
import com.github.redreaperlp.cdpusher.user.User;
import com.github.redreaperlp.cdpusher.util.Value;
import com.github.redreaperlp.cdpusher.util.logger.types.ErrorPrinter;
import com.github.redreaperlp.cdpusher.util.logger.types.InfoPrinter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Statement;
import java.util.List;

public class DiscInformation {
    private long id;
    private String country;
    private int year;
    private String title;
    private String label;
    private String resourceURL;
    private List<Song> songs;

    public DiscInformation() {
    }

    public DiscInformation(JSONObject requestResponse) {
        if (requestResponse.has("country")) this.country = requestResponse.getString("country");
        if (requestResponse.has("year")) this.year = Integer.parseInt(requestResponse.getString("year"));
        if (requestResponse.has("title")) this.title = requestResponse.getString("title");
        if (requestResponse.has("label"))
            this.label = requestResponse.getJSONArray("label").toList().stream().map(Object::toString).toArray(String[]::new)[0];
        if (requestResponse.has("resource_url")) this.resourceURL = requestResponse.getString("resource_url");
    }

    public void loadTracks(User requester) {
        var songs = DiscOgsSearch.getInstance().searchDiscTracks(this);
        if (songs.isEmpty()) {
            new InfoPrinter().append("Failed to load tracks for " + title).print();
            requester.broadcastMessage(Topic.Disc.FAILED.fillResponse(Topic.Request.ERROR)
                    .put("message", "Failed to load tracks for " + title).toString());
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

    public int getYear() {
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

    /**
     * Pushes the DiscInformation to the Database
     */
    public Value<Long, String> pushToDB() {
        long l = doesDiscExist();
        if (l != -200) {
            if (l == -500) {
                return new Value<>(500, -1L, "Database error");
            }
            new InfoPrinter().append("Disc already exists").print();
            return new Value<>(409, l, "Disc already exists");
        }
        try (var con = DatabaseManager.getInstance().getConnection()) {
            var ps = con.prepareStatement("INSERT INTO cdpusher.discs (Title, Label, Country, ResourceUrl, Year) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setString(2, label);
            ps.setString(3, country);
            ps.setString(4, resourceURL);
            ps.setInt(5, year);
            ps.executeUpdate();

            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    this.setId(rs.getLong(1));
                }
            }
        } catch (Exception e) {
            new ErrorPrinter().appendException(e).print();
            return new Value<>(500, -1L);
        }
        return new Value<>(200, getId());
    }

    public long doesDiscExist() {
        try (var con = DatabaseManager.getInstance().getConnection()) {
            var ps = con.prepareStatement("SELECT DiscId FROM cdpusher.discs WHERE Title = ? AND Label = ? AND Country = ? AND ResourceUrl = ? AND Year = ?");
            ps.setString(1, title);
            ps.setString(2, label);
            ps.setString(3, country);
            ps.setString(4, resourceURL);
            ps.setInt(5, year);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("DiscId");
            }
        } catch (Exception e) {
            new ErrorPrinter().appendException(e).print();
            return -500;
        }
        return -200;
    }

    public boolean pushSongsToDB() {
        if (id == -1) {
            new ErrorPrinter().append("DiscInformation not pushed to DB").print();
            return false;
        }
        try (var con = DatabaseManager.getInstance().getConnection()) {
            con.setAutoCommit(false);
            for (Song song : songs) {
                song.pushToDB(con, id);
            }
            con.commit();
            return true;
        } catch (Exception e) {
            new ErrorPrinter().appendException(e).print();
            return false;
        }
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setYear(int year) {
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
