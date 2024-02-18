package com.github.redreaperlp.cdpusher;

import com.github.redreaperlp.cdpusher.data.DataKeys;
import com.github.redreaperlp.cdpusher.data.SongData;
import com.github.redreaperlp.cdpusher.http.SpotifySearch;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.Arrays;

public class TrackInformation implements SongData {
    private long songID;
    private String title;
    private String album;
    private int discNo = 1;
    private int trackNo;
    private int duration;
    private String cover;
    private String[] artists;
    private String albumReleaseDate;
    private boolean spotifySearch = false;
    private MissmatchData missmatchData;
    private boolean spotifySearchMissMatch = false;

    private int internalCDNumeration = 1;


    public TrackInformation(long songID, JSONObject jsonObject) {
        this.songID = songID;
        setTitle(jsonObject.getString("title"));

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
            if (position.length > 1) {
                this.discNo = Integer.parseInt(position[0]);
                this.trackNo = Integer.parseInt(position[1]);
            } else if (position.length == 1) {
                this.trackNo = Integer.parseInt(position[0]);
            }
        } catch (NumberFormatException e) {
            this.discNo = -1;
            this.trackNo = -1;
        }
    }

    public String getTitle() {
        return title;
    }

    public int getDuration() {
        return duration;
    }

    public String getImage() {
        return cover;
    }

    public int getDiscNo() {
        return discNo;
    }

    public int getTrackNo() {
        return trackNo;
    }

    @Override
    public long getSongID() {
        return songID;
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

    public void setCover(String cover) {
        this.cover = cover;
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

    public SongData spotifySearch() {
        if (!spotifySearch) {
            System.out.println("Suche kann nicht durchgeführt werden, da keine Künstler angegeben sind.");
            return this;
        }

        JSONObject track = SpotifySearch.getInstance().finalizeSearch(this).getJSONArray("items").getJSONObject(0);
        JSONObject album = track.getJSONObject("album");

        String title = Normalizer.normalize(track.getString("name"), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").trim().toLowerCase();
        String[] artists = new String[track.getJSONArray("artists").length()];
        for (int i = 0; i < artists.length; i++) {
            artists[i] = track.getJSONArray("artists").getJSONObject(i).getString("name");
        }

        int duration = track.getInt("duration_ms") / 1000;
        String albumName = album.getString("name");
        String albumReleaseDate = album.getString("release_date");
        String cover = album.getJSONArray("images").getJSONObject(0).getString("url");

        if (!titlesMatch(title, this.title)) {
            spotifySearchMissMatch = true;
            missmatchData = new MissmatchData(songID, title, artists, duration, cover, albumName, albumReleaseDate, discNo, this.trackNo, internalCDNumeration);
            return this;
        } else {
            this.artists = artists;
            this.duration = duration;
            this.cover = cover;
            this.album = albumName;
            this.albumReleaseDate = albumReleaseDate;
            return new Song(this.songID,
                    this.title,
                    this.artists[0],
                    this.album,
                    this.trackNo,
                    this.discNo,
                    this.duration,
                    Integer.parseInt(this.albumReleaseDate.split("-")[0]),
                    this.internalCDNumeration,
                    this.cover);
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

        object.put(DataKeys.SongData.TITLE.getKey(), title == null ? JSONObject.NULL : title);
        object.put(DataKeys.SongData.ARTIST.getKey(), artists == null ? JSONObject.NULL : artists);
        object.put(DataKeys.SongData.DURATION.getKey(), duration);
        object.put(DataKeys.SongData.COVER_URI.getKey(), cover == null ? JSONObject.NULL : cover);
        object.put(DataKeys.SongData.ALBUM.name(), album);
        object.put(DataKeys.SongData.YEAR.getKey(), albumReleaseDate);
        object.put(DataKeys.SongData.SPOTIFY_MISSMATCH.getKey(), spotifySearchMissMatch);
        object.put(DataKeys.SongData.SPOTIFY_SEARCH.getKey(), spotifySearch);
        object.put(DataKeys.SongData.SPOTIFY_MISSMATCH_DATA.getKey(), missmatchData);
        object.put(DataKeys.SongData.DISC_NO.getKey(), discNo);
        object.put(DataKeys.SongData.TRACK_NO.getKey(), trackNo);
        object.put(DataKeys.SongData.INTERNAL_DISC_NO.getKey(), internalCDNumeration);
        object.put(DataKeys.SongData.TRACK_ID.getKey(), songID);

        return object;
    }

    @Override
    public String toString() {
        return "TrackInformation{" +
                "title='" + title + '\'' +
                ", artists=" + (artists == null ? null : Arrays.asList(artists)) +
                ", duration=" + duration +
                ", cdNumber=" + discNo +
                ", trackNumber=" + trackNo +
                ", internalCDNumeration=" + internalCDNumeration +
                ", imageURL='" + cover + '\'' +
                ", album='" + album + '\'' +
                ", albumReleaseDate='" + albumReleaseDate + '\'' +
                "}";
    }

    public static class MissmatchData implements SongData {
        private long songID;
        private String title;
        private int discNo;
        private int trackNo;
        private int duration;
        private String album;
        private String[] artists;
        private String imageURL;
        private int internalDiscNo;
        private String albumReleaseDate;

        public MissmatchData(long songID, String title, String[] artists, int duration, String imageURL, String album, String albumReleaseDate, int discNo, int trackNo, int internalDiscNo) {
            this.title = title;
            this.album = album;
            this.artists = artists;
            this.songID = songID;
            this.duration = duration;
            this.imageURL = imageURL;
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
        public int getDiscNo() {
            return discNo;
        }

        @Override
        public int getTrackNo() {
            return trackNo;
        }

        @Override
        public long getSongID() {
            return songID;
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

        @Override
        public JSONObject toJSON() {
            JSONObject object = new JSONObject();

            object.put(DataKeys.SongData.TITLE.getKey(), title);
            object.put(DataKeys.SongData.ARTIST.getKey(), artists);
            object.put(DataKeys.SongData.DURATION.getKey(), duration);
            object.put(DataKeys.SongData.COVER_URI.getKey(), imageURL);
            object.put(DataKeys.SongData.ALBUM.name(), album);
            object.put(DataKeys.SongData.YEAR.getKey(), albumReleaseDate);
            object.put(DataKeys.SongData.DISC_NO.getKey(), discNo);

            return object;
        }
    }
}