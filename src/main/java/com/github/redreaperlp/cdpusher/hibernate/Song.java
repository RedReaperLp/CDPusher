package com.github.redreaperlp.cdpusher.hibernate;

import com.github.redreaperlp.cdpusher.data.ConfirmedSongData;
import com.github.redreaperlp.cdpusher.data.SongData;
import jakarta.persistence.*;

@Entity
@Table(name = "songs")
public class Song extends ConfirmedSongData {

    public Song() {
    }

    public Song(SongData songData) {
        super(songData);
    }

    public Song(long songID, String title, String artist, String album, int trackNo, int discNo, long duration, int year, String imageURI) {
        super(songID, title, artist, album, trackNo, year, discNo, duration, imageURI);
    }

    @Id
    public long getId() {
        return songID;
    }

    public void setId(long songID) {
        this.songID = songID;
    }

    @ManyToOne
    @JoinColumn(name = "disc_id", referencedColumnName = "disc_id")
    private DiscInformation discInformation;

    @Override
    @Column(name = "artist")
    public String getArtist() {
        return super.getArtist();
    }

    @Override
    @Column(name = "album")
    public String getAlbum() {
        return super.getAlbum();
    }

    @Override
    @Column(name = "title")
    public String getTitle() {
        return super.getTitle();
    }

    @Override
    @Column(name = "track_no")
    public int getTrackNo() {
        return super.getTrackNo();
    }

    @Override
    @Column(name = "year")
    public int getYear() {
        return super.getYear();
    }

    @Override
    @Column(name = "disc_no")
    public int getDiscNo() {
        return super.getDiscNo();
    }

    @Override
    @Column(name = "duration")
    public long getTimeInSeconds() {
        return super.getTimeInSeconds();
    }

    @Override
    @Column(name = "image_uri")
    public String getImageURI() {
        return super.getImageURI();
    }

    @Override
    @Column(name = "spotify_search")
    public boolean isSpotifySearch() {
        return super.isSpotifySearch();
    }

    @Override
    @Column(name = "spotify_missmatch")
    public boolean isSpotifyMissmatch() {
        return super.isSpotifyMissmatch();
    }

    public void setDiscInformation(DiscInformation discInformation) {
        this.discInformation = discInformation;
    }

    public DiscInformation getDiscInformation() {
        return discInformation;
    }
}
