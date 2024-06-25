package com.github.redreaperlp.cdpusher.data.song;

import com.github.redreaperlp.cdpusher.util.logger.types.ErrorPrinter;

import java.sql.Connection;
import java.sql.SQLException;

public class Song extends ConfirmedSongData {

    public Song(SongData songData) {
        super(songData);
    }

    public Song(long songID, String title, String artist, String album, int trackNo, int discNo, long duration, int year, String imageURI) {
        super(songID, title, artist, album, trackNo, year, discNo, duration, imageURI);
    }

    /**
     * Inserts a Song into the Database
     */
    public void insertToDatabase() {

    }

    public void pushToDB(Connection con, long id) throws SQLException {
        var ps = con.prepareStatement("INSERT INTO songs (Title, Artist, Album, TrackNumber, CdNumber, Duration, Year, ImageUri, DiscId, SpotifySearch, SpotifyMismatch)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        ps.setString(1, title);
        ps.setString(2, artist);
        ps.setString(3, album);
        ps.setInt(4, trackNo);
        ps.setInt(5, discNo);
        ps.setLong(6, timeInSeconds);
        ps.setInt(7, year);
        ps.setString(8, imageURI);
        ps.setLong(9, id);
        ps.setBoolean(10, spotifySearch);
        ps.setBoolean(11, spotifyMismatch);
        ps.executeUpdate();
    }
}
