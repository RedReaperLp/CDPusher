package com.github.redreaperlp.cdpusher.hibernate;

import com.github.redreaperlp.cdpusher.data.ConfirmedSongData;
import com.github.redreaperlp.cdpusher.data.SongData;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "songs")
public class Song extends ConfirmedSongData implements Serializable {

    public Song() {
    }

    public Song(SongData songData) {
        super(songData);
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getSongID() {
        return songID;
    }

    public Song(long songID, String title, String artist, String album, int trackNo, int discNo, long duration, int year, String imageURI) {
        super(songID, title, artist, album, trackNo, year, discNo, duration, imageURI);
    }

    public void setTrackAndDisc(int trackNo, int discNo) {
        this.trackNo = trackNo;
        this.discNo = discNo;
    }

    @ManyToOne
    @JoinColumn(name = "disc_id")
    @Access(AccessType.FIELD)
    private DiscInformation discInformation;


    @Override
    @Access(AccessType.PROPERTY)
    @Column(name = "artist")
    public String getArtist() {
        return super.getArtist();
    }

    @Override
    @Access(AccessType.PROPERTY)
    @Column(name = "album")
    public String getAlbum() {
        return super.getAlbum();
    }

    @Override
    @Access(AccessType.PROPERTY)
    @Column(name = "title")
    public String getTitle() {
        return super.getTitle();
    }

    @Override
    @Access(AccessType.PROPERTY)
    @Column(name = "track_no")
    public int getTrackNo() {
        return super.getTrackNo();
    }

    @Override
    @Access(AccessType.PROPERTY)
    @Column(name = "year")
    public int getYear() {
        return super.getYear();
    }

    @Override
    @Access(AccessType.PROPERTY)
    @Column(name = "disc_no")
    public int getDiscNo() {
        return super.getDiscNo();
    }

    @Override
    @Access(AccessType.PROPERTY)
    @Column(name = "duration")
    public long getTimeInSeconds() {
        return super.getTimeInSeconds();
    }

    @Override
    @Access(AccessType.PROPERTY)
    @Column(name = "image_uri")
    public String getImageURI() {
        return super.getImageURI();
    }

    @Override
    @Access(AccessType.PROPERTY)
    @Column(name = "spotify_search")
    public boolean isSpotifySearch() {
        return super.isSpotifySearch();
    }

    @Override
    @Access(AccessType.PROPERTY)
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
