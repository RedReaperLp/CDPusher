package com.github.redreaperlp.cdpusher;

import com.github.redreaperlp.cdpusher.data.DataKeys;
import com.github.redreaperlp.cdpusher.data.SongData;
import org.json.JSONObject;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Song implements SongData {
    private final long songID;
    private String title;
    private String artist;
    private String album;
    private int trackNo;
    private int year;
    private int discNo;
    private long timeInSeconds;
    private long internalDiscNo;
    private String imageURI;

    public Song(long songID, String title, String artist, String album, int trackNo, int discNo, long duration, int year, long internalDiscNo, String imageURI) {
        this.internalDiscNo = internalDiscNo;
        this.timeInSeconds = duration;
        this.imageURI = imageURI;
        this.trackNo = trackNo;
        this.songID = songID;
        this.discNo = discNo;
        this.album = album;
        this.artist = artist;
        this.year = year;
        this.title = title;
    }

    @Override
    public String toString() {
        return "Song{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", track=" + trackNo +
                ", year=" + year +
                ", discNo=" + discNo +
                ", timeInSeconds=" + timeInSeconds +
                ", internalDiscNo=" + internalDiscNo +
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
            String updateQuery = "UPDATE cd_pusher.music SET title = ?, artist = ?, album = ?, track = ?,discNo=?, year = ?, duration = ?, image = ?, internalDiscNo = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
                setParameters(stmt);
                stmt.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertIntoDB(Connection connection) {
        try {
            try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO cd_pusher.music (track, artist, album, track, discNo, year, duration, image, internalDiscNo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
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
        stmt.setInt(4, trackNo);
        stmt.setInt(5, discNo);
        stmt.setInt(6, year);
        stmt.setLong(7, timeInSeconds);
        stmt.setString(8, imageURI);
        stmt.setLong(9, internalDiscNo);
    }


    public boolean existsInDB(Connection connection) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM cd_pusher.music WHERE title = ? AND artist = ? AND year = ?");
            stmt.setString(1, title);
            stmt.setString(2, artist);
            stmt.setInt(3, year);
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

    public JSONObject toJSON() {
        return new JSONObject()
                .put(DataKeys.SongData.TITLE.getKey(), title)
                .put(DataKeys.SongData.ARTIST.getKey(), artist)
                .put(DataKeys.SongData.ALBUM.getKey(), album)
                .put(DataKeys.SongData.TRACK_NO.getKey(), trackNo)
                .put(DataKeys.SongData.YEAR.getKey(), year)
                .put(DataKeys.SongData.DISC_NO.getKey(), discNo)
                .put(DataKeys.SongData.DURATION.getKey(), timeInSeconds)
                .put(DataKeys.SongData.COVER_URI.getKey(), imageURI)
                .put(DataKeys.SongData.INTERNAL_DISC_NO.getKey(), internalDiscNo)
                .put(DataKeys.SongData.SPOTIFY_SEARCH.getKey(), true)
                .put(DataKeys.SongData.SPOTIFY_MISSMATCH.getKey(), false)
                .put(DataKeys.SongData.TRACK_ID.getKey(), songID);
    }

    public void setIDs(int trackNo, int discNo) {
        this.trackNo = trackNo;
        this.discNo = discNo;
    }
}
