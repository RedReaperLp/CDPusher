package com.github.redreaperlp.cdpusher;

import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class Song {

    String title;
    String artist;
    String album;
    String track;
    String year;
    String genre;
    String comment;
    String composer;
    String discNo;
    long timeInSeconds;

    public Song(Tag tag, long timeInSeconds) {
        this.title = tag.getFirst(FieldKey.TITLE);
        this.artist = tag.getFirst(FieldKey.ARTIST);
        this.album = tag.getFirst(FieldKey.ALBUM);
        this.track = tag.getFirst(FieldKey.TRACK);
        this.year = tag.getFirst(FieldKey.YEAR);
        this.genre = tag.getFirst(FieldKey.GENRE);
        this.comment = tag.getFirst(FieldKey.COMMENT);
        this.composer = tag.getFirst(FieldKey.COMPOSER);
        this.discNo = tag.getFirst(FieldKey.DISC_NO);
        this.timeInSeconds = timeInSeconds;
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

    public String getTrack() {
        return track;
    }

    public String getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    public String getComment() {
        return comment;
    }

    public String getComposer() {
        return composer;
    }

    public String getDiscNo() {
        return discNo;
    }

    public long getTimeInSeconds() {
        return timeInSeconds;
    }

    @Override
    public String toString() {
        return "Song{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", track='" + track + '\'' +
                ", year='" + year + '\'' +
                ", genre='" + genre + '\'' +
                ", comment='" + comment + '\'' +
                ", composer='" + composer + '\'' +
                ", discNo='" + discNo + '\'' +
                ", timeInSeconds=" + timeInSeconds +
                '}';
    }
}
