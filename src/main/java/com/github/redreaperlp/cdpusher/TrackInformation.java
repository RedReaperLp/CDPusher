package com.github.redreaperlp.cdpusher;

import com.github.redreaperlp.cdpusher.http.SpotifySearch;
import com.github.redreaperlp.cdpusher.util.logger.types.TestPrinter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.Arrays;

public class TrackInformation {
    private String title;
    private int duration;
    private String[] artists;
    private String imageURL;
    private String album;
    private String albumReleaseDate;

    private int cdNumber;
    private int trackNumber;

    private boolean spotifySearch = false;
    private boolean spotifySearchMissMatch = false;
    private MissmatchData missmatchData;

    private int internalCDNumeration;

    public TrackInformation(JSONObject jsonObject) {
        setTitle(jsonObject.getString("title"));
        System.out.println(jsonObject.toString(5));


        JSONArray artists;
        if (jsonObject.has("artists")) {
            artists = jsonObject.getJSONArray("artists");
            this.artists = new String[artists.length()];
            for (int i = 0; i < artists.length(); i++) {
                this.artists[i] = artists.getJSONObject(i).getString("name");
            }
            spotifySearch = true;
        }
        try {
            String[] duration = jsonObject.getString("duration").split(":");
            this.duration = Integer.parseInt(duration[0]) * 60 + Integer.parseInt(duration[1]);
        } catch (NumberFormatException e) {
            this.duration = 0;
        }
        try {
            String[] position = jsonObject.getString("position").split("-");
            this.cdNumber = Integer.parseInt(position[0]);
            if (position.length > 1) this.trackNumber = Integer.parseInt(position[1]);
        } catch (NumberFormatException e) {
            this.cdNumber = -1;
            this.trackNumber = -1;
        }
    }

    public String getTitle() {
        return title;
    }

    public int getDuration() {
        return duration;
    }

    public String getImage() {
        return imageURL;
    }

    public int getCdNumber() {
        return cdNumber;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public int getInternalCDNumeration() {
        return internalCDNumeration;
    }

    public String getArtist(int i) {
        if (artists == null || artists.length <= i) return null;
        return artists[i];
    }

    public void setTitle(String title) {
        String regex = "\\([^)]+\\)([^()]*)$";
        this.title = title.replaceAll(regex, "").trim();
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setArtists(JSONArray artists) {
        this.artists = new String[artists.length()];
        for (int i = 0; i < artists.length(); i++) {
            this.artists[i] = artists.getJSONObject(i).getString("name");
        }
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setAlbumReleaseDate(String albumReleaseDate) {
        this.albumReleaseDate = albumReleaseDate;
    }

    public void spotifySearch() {
        if (!spotifySearch) {
            System.out.println("Suche kann nicht durchgeführt werden, da keine Künstler angegeben sind.");
            return;
        }

        JSONObject track = SpotifySearch.getInstance().finalizeSearch(this).getJSONArray("items").getJSONObject(0);
        track.remove("available_markets");

        JSONObject album = track.getJSONObject("album");
        album.remove("available_markets");

        String title = Normalizer.normalize(track.getString("name"), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").trim().toLowerCase();
        String[] artists = new String[track.getJSONArray("artists").length()];
        for (int i = 0; i < artists.length; i++) {
            artists[i] = track.getJSONArray("artists").getJSONObject(i).getString("name");
        }

        int duration = track.getInt("duration_ms") / 1000;
        String albumName = album.getString("name");
        String albumReleaseDate = album.getString("release_date");
        String imageURL = album.getJSONArray("images").getJSONObject(0).getString("url");

        if (!titlesMatch(title, this.title)) {
            spotifySearchMissMatch = true;
            System.out.println("\n\t\t\t\tTitle mismatch: " + title.trim() + " != " + this.title.trim());
            missmatchData = new MissmatchData(title, artists, duration, imageURL, albumName, albumReleaseDate);

            System.out.println("\n\n" + this.toString());
            System.out.println(missmatchData.toString() + "\n\n");
        } else {
            this.artists = artists;
            this.duration = duration;
            this.imageURL = imageURL;
            this.album = albumName;
            this.albumReleaseDate = albumReleaseDate;
        }
    }

    public boolean isSpotifySearchMissMatch() {
        return spotifySearchMissMatch;
    }

    public boolean isSpotifySearch() {
        return spotifySearch;
    }

    public boolean titlesMatch(String originalTitle, String updatedTitle) {
        String cleanedOriginal = cleanAndNormalizeTitle(originalTitle);
        String cleanedUpdated = cleanAndNormalizeTitle(updatedTitle);

        if (cleanedOriginal.length() > cleanedUpdated.length()) {
            String bigger = cleanedOriginal;
            cleanedOriginal = cleanedUpdated;
            cleanedUpdated = bigger;
        }

        return cleanedUpdated.startsWith(cleanedOriginal);
    }

    private String cleanAndNormalizeTitle(String title) {
        return Normalizer.normalize(title.replaceAll("\\([^)]+\\)([^()]*)$", ""), Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .trim()
                .toLowerCase();
    }


    public JSONObject toJSON() {
        JSONObject object = new JSONObject();

        object.put("title", title == null ? JSONObject.NULL : title);
        object.put("artists", artists == null ? JSONObject.NULL : artists);
        object.put("duration", duration);
        object.put("imageURL", imageURL == null ? JSONObject.NULL : imageURL);
        object.put("album", album);
        object.put("albumReleaseDate", albumReleaseDate);
        object.put("spotifySearchMissMatch", spotifySearchMissMatch);
        object.put("missmatchData", missmatchData);
        object.put("cdNumber", cdNumber);
        object.put("trackNumber", trackNumber);
        object.put("internalCDNumeration", internalCDNumeration);

        new TestPrinter().append(object.toString(5)).print();
        return object;
    }

    @Override
    public String toString() {
        return "TrackInformation{" +
                "title='" + title + '\'' +
                ", artists=" + (artists == null ? null : Arrays.asList(artists)) +
                ", duration=" + duration +
                ", cdNumber=" + cdNumber +
                ", trackNumber=" + trackNumber +
                ", internalCDNumeration=" + internalCDNumeration +
                ", imageURL='" + imageURL + '\'' +
                ", album='" + album + '\'' +
                ", albumReleaseDate='" + albumReleaseDate + '\'' +
                "}";
    }

    public class MissmatchData {
        private String title;
        private String[] artists;
        private int duration;
        private String imageURL;
        private String album;
        private String albumReleaseDate;

        public MissmatchData(String title, String[] artists, int duration, String imageURL, String album, String albumReleaseDate) {
            this.title = title;
            this.artists = artists;
            this.duration = duration;
            this.imageURL = imageURL;
            this.album = album;
            this.albumReleaseDate = albumReleaseDate;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String[] getArtists() {
            return artists;
        }

        public void setArtists(String[] artists) {
            this.artists = artists;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getImageURL() {
            return imageURL;
        }

        public void setImageURL(String imageURL) {
            this.imageURL = imageURL;
        }

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public String getAlbumReleaseDate() {
            return albumReleaseDate;
        }

        public void setAlbumReleaseDate(String albumReleaseDate) {
            this.albumReleaseDate = albumReleaseDate;
        }

        @Override
        public String toString() {
            return "MissmatchData{" +
                    "title='" + title + '\'' +
                    ", artists=" + (artists == null ? null : Arrays.asList(artists)) +
                    ", duration=" + duration +
                    ", imageURL='" + imageURL + '\'' +
                    ", album='" + album + '\'' +
                    ", albumReleaseDate='" + albumReleaseDate + '\'' +
                    '}';
        }
    }
}