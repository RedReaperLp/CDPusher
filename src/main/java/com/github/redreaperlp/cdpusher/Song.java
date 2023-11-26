package com.github.redreaperlp.cdpusher;

import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public void pushToDB(int discNo, int totalDiscs, Connection connection) {
        if (existsInDB(connection)) {
            int dialogResult = JOptionPane.showConfirmDialog(null, "Song already exists in database: " + title + " - " + artist + " - " + year + "\nDo you want to update it?", "Song already exists", JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    PreparedStatement stmt = connection.prepareStatement("UPDATE radio_music.music SET title = ?, artist = ?, album = ?, track = ?, year = ?, genre = ?, comment = ?, interpreter = ? WHERE title = ? AND artist = ? AND year = ?");
                    stmt.setString(1, title);
                    stmt.setString(2, artist);
                    stmt.setString(3, album);
                    stmt.setString(4, track);
                    stmt.setInt(5, year.isEmpty() ? 0 : Integer.parseInt(year));
                    stmt.setString(6, genre);
                    stmt.setString(7, comment);
                    stmt.setString(8, composer);
                    stmt.setString(9, title);
                    stmt.setString(10, artist);
                    stmt.setInt(11, year.isEmpty() ? 0 : Integer.parseInt(year));
                    stmt.execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return;
            } else {
                return;
            }
        }
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO radio_music.music (title, artist, album, track, year, genre, comment, interpreter) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setString(1, title);
            stmt.setString(2, artist);
            stmt.setString(3, album);
            stmt.setString(4, track);
            stmt.setInt(5, year.isEmpty() ? 0 : Integer.parseInt(year));
            stmt.setString(6, genre);
            stmt.setString(7, comment);
            stmt.setString(8, composer);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsInDB(Connection connection) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM radio_music.music WHERE title = ? AND artist = ? AND year = ?");
            stmt.setString(1, title);
            stmt.setString(2, artist);
            stmt.setInt(3, year.isEmpty() ? 0 : Integer.parseInt(year));
            stmt.execute();
            ResultSet resultSet = stmt.getResultSet();
            if (resultSet.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
