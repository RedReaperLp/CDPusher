package com.github.redreaperlp.cdpusher;

import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        System.out.println(tag.getFirst(FieldKey.COVER_ART ));
        this.year = tag.getFirst(FieldKey.YEAR);
        this.genre = tag.getFirst(FieldKey.GENRE);
        this.comment = tag.getFirst(FieldKey.COMMENT);
        this.composer = tag.getFirst(FieldKey.COMPOSER);
        this.discNo = tag.getFirst(FieldKey.DISC_NO);
        this.timeInSeconds = timeInSeconds;

        Pattern pattern = Pattern.compile("\\(.*\\)");
        Matcher matcher = pattern.matcher(genre);
        if (matcher.find()) {
            genre = genre.replace(matcher.group(0), "");
        }
        genre = genre.trim();
        System.out.println(genre);

        if (album.toLowerCase().contains("unbekannt") || album.toLowerCase().contains("unknown")) {
            album = "Unbekannt";
        }

        if (genre.toLowerCase().contains("unbekannt") || genre.toLowerCase().contains("unknown")) {
            genre = "Unbekannt";
        }
        if (composer.isEmpty()) {
            composer = "Unbekannt";
        }
    }

    public Song(String title, String interpreter, String album, String track, String year, String genre, String comment, String composer, String discNo, String length) {
        this.title = title;
        this.artist = interpreter;
        this.album = album;
        this.track = track;
        this.year = year;
        this.genre = genre;
        this.comment = comment;
        this.composer = composer;
        this.discNo = discNo;
        this.timeInSeconds = Long.parseLong(length);
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
                updateDB(connection);
            }
        } else {
            insertIntoDB(connection);
        }
    }

    private void updateDB(Connection connection) {
        try {
            String updateQuery = "UPDATE radio_music.music SET title = ?, artist = ?, album = ?, track = ?, year = ?, genre = ?, comment = ?, composer = ?, length = ? WHERE title = ? AND artist = ? AND year = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
                stmt.setString(10, title);
                stmt.setString(11, artist);
                stmt.setInt(12, year.isEmpty() ? 0 : Integer.parseInt(year));
                setParameters(stmt);
                stmt.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertIntoDB(Connection connection) {
        try {
            String insertQuery = "INSERT INTO radio_music.music (title, artist, album, track, year, genre, comment, composer, length) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
                setParameters(stmt);
                stmt.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setParameters(PreparedStatement stmt) throws SQLException {
        stmt.setString(1, title);
        stmt.setString(2, artist);
        stmt.setString(3, album);
        stmt.setString(4, track);
        stmt.setInt(5, year.isEmpty() ? 0 : Integer.parseInt(year));
        stmt.setString(6, genre);
        stmt.setString(7, comment);
        stmt.setString(8, composer);
        stmt.setLong(9, timeInSeconds);
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

    public List<String> toArray() {
        return List.of(title, artist, album, track, year, genre, comment, composer, discNo, String.valueOf(timeInSeconds));
    }
}
