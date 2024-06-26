package com.github.redreaperlp.cdpusher.data.song;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

public abstract class SongData {
    public long songID;
    public String title;
    public String artist;
    public String album;
    public int discNo;
    public int trackNo;
    public int year;
    public long timeInSeconds;
    public String imageURI;
    public boolean spotifySearch = true;
    public long discID;

    public SongData() {}
    public SongData(SongData songData) {
        this.songID = songData.songID;
        this.title = songData.title;
        this.artist = songData.artist;
        this.album = songData.album;
        this.trackNo = songData.trackNo;
        this.year = songData.year;
        this.discNo = songData.discNo;
        this.timeInSeconds = songData.timeInSeconds;
        this.imageURI = songData.imageURI;
        this.spotifySearch = songData.spotifySearch;
    }

    public SongData(long songID, String title, String artist, String album, int trackNo, int year, int discNo, long timeInSeconds, String imageURI) {
        this.songID = songID;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.trackNo = trackNo;
        this.year = year;
        this.discNo = discNo;
        this.timeInSeconds = timeInSeconds;
        this.imageURI = imageURI;
    }

    public JSONObject toJSON() {
        return new JSONObject()
                .put(SongDataKeys.SONG_ID.getKey(), songID)
                .put(SongDataKeys.TITLE.getKey(), title)
                .put(SongDataKeys.ARTISTS.getKey(), new JSONArray()
                        .put(artist))
                .put(SongDataKeys.ALBUM.getKey(), album)
                .put(SongDataKeys.TRACK_NO.getKey(), trackNo)
                .put(SongDataKeys.YEAR.getKey(), year)
                .put(SongDataKeys.DISC_NO.getKey(), discNo)
                .put(SongDataKeys.DURATION.getKey(), timeInSeconds)
                .put(SongDataKeys.COVER_URI.getKey(), imageURI)
                .put(SongDataKeys.SPOTIFY_SEARCH.getKey(), spotifySearch);
    }

    public long getSongID() {
        return songID;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public int getDiscNo() {
        return discNo;
    }

    public int getTrackNo() {
        return trackNo;
    }

    public int getYear() {
        return year;
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

    public long getDiscID() {
        return discID;
    }

    public void setSongID(long songID) {
        this.songID = songID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setDiscNo(int discNo) {
        this.discNo = discNo;
    }

    public void setTrackNo(int trackNo) {
        this.trackNo = trackNo;
    }

    public void setYear(int year) {
        this.year = year;
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

    public void setDiscID(long discID) {
        this.discID = discID;
    }
}
