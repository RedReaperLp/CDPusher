package com.github.redreaperlp.cdpusher.data;

import org.json.JSONObject;

public abstract class SongData {
    public long songID;
    public String title;
    public String[] artists;
    public String album;
    public int trackNo;
    public int year;
    public int discNo;
    public long timeInSeconds;
    public String imageURI;
    public boolean spotifySearch = true;

    public SongData() {}
    public SongData(SongData songData) {
        this.songID = songData.songID;
        this.title = songData.title;
        this.artists = songData.artists;
        this.album = songData.album;
        this.trackNo = songData.trackNo;
        this.year = songData.year;
        this.discNo = songData.discNo;
        this.timeInSeconds = songData.timeInSeconds;
        this.imageURI = songData.imageURI;
        this.spotifySearch = songData.spotifySearch;
    }

    public SongData(long songID, String title, String[] artists, String album, int trackNo, int year, int discNo, long timeInSeconds, String imageURI) {
        this.songID = songID;
        this.title = title;
        this.artists = artists;
        this.album = album;
        this.trackNo = trackNo;
        this.year = year;
        this.discNo = discNo;
        this.timeInSeconds = timeInSeconds;
        this.imageURI = imageURI;
    }

    public JSONObject toJSON() {
        return new JSONObject()
                .put(SongDataKey.SONG_ID.getKey(), songID)
                .put(SongDataKey.TITLE.getKey(), title)
                .put(SongDataKey.ARTISTS.getKey(), artists)
                .put(SongDataKey.ALBUM.getKey(), album)
                .put(SongDataKey.TRACK_NO.getKey(), trackNo)
                .put(SongDataKey.YEAR.getKey(), year)
                .put(SongDataKey.DISC_NO.getKey(), discNo)
                .put(SongDataKey.DURATION.getKey(), timeInSeconds)
                .put(SongDataKey.COVER_URI.getKey(), imageURI)
                .put(SongDataKey.SPOTIFY_SEARCH.getKey(), spotifySearch);
    }

    public long getSongID() {
        return songID;
    }

    public String getTitle() {
        return title;
    }

    public String[] getArtists() {
        return artists;
    }

    public String getAlbum() {
        return album;
    }

    public int getTrackNo() {
        return trackNo;
    }

    public int getYear() {
        return year;
    }

    public int getDiscNo() {
        return discNo;
    }

    public long getTimeInSeconds() {
        return timeInSeconds;
    }

    public String getImageURI() {
        return imageURI;
    }

    public boolean isSpotifySearch() {
        return spotifySearch;
    }

    public void setSongID(long songID) {
        this.songID = songID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtists(String[] artists) {
        this.artists = artists;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setTrackNo(int trackNo) {
        this.trackNo = trackNo;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setDiscNo(int discNo) {
        this.discNo = discNo;
    }

    public void setTimeInSeconds(long timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public void setSpotifySearch(boolean spotifySearch) {
        this.spotifySearch = spotifySearch;
    }
}
