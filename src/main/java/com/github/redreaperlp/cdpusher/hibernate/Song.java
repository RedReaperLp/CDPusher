package com.github.redreaperlp.cdpusher.hibernate;

import com.github.redreaperlp.cdpusher.data.ConfirmedSongData;
import com.github.redreaperlp.cdpusher.data.SongData;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "songs")
public class Song extends ConfirmedSongData {

    @ManyToOne()
    public DiscInformation discInformation;

    public Song() {}
    public Song(SongData songData) {
        super(songData);
    }

    @Id
    public long getSongID() {
        return songID;
    }

    public Song(long songID, String title, String[] artists, String album, int trackNo, int discNo, long duration, int year, String imageURI) {
        super(songID, title, artists, album, trackNo, year, discNo, duration, imageURI);
    }



    public void setTrackAndDisc(int trackNo, int discNo) {
        this.trackNo = trackNo;
        this.discNo = discNo;
    }
}
