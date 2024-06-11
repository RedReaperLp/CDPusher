package com.github.redreaperlp.cdpusher.hibernate;

import com.github.redreaperlp.cdpusher.Main;
import com.github.redreaperlp.cdpusher.database.DatabaseConfiguration;
import com.github.redreaperlp.cdpusher.database.DatabaseManager;
import com.github.redreaperlp.cdpusher.util.logger.Loggers;
import com.github.redreaperlp.cdpusher.util.logger.types.ErrorPrinter;
import com.github.redreaperlp.cdpusher.util.logger.types.TestPrinter;
import org.junit.Test;

import java.util.List;

public class DiscTest {

    @Test
    public void testDisc() {
        //arrange
        Main.debug = true;
        Loggers.load();

        HibernateSession.getInstance();
        DatabaseConfiguration conf = new DatabaseConfiguration("45.81.235.52", "cdpusher", "cdpusher", "cdpusher");
        conf.initDatabase();

        long maxDiscID = DatabaseManager.getInstance().getHighestDiscID() + 1;
        long maxSongID = DatabaseManager.getInstance().getHighestSongID() + 1;

        DiscInformation disc = new DiscInformation();
        disc.setSongs(List.of(new Song(maxSongID, "some title", "some artist", "some album", 1, 1, 100, 2021, "some image uri")));
        disc.setId(maxDiscID);
        disc.setCountry("some country");
        disc.setYear("2021");
        disc.setTitle("some title");
        disc.setLabel("some label");
        disc.setResourceURL("some url");

        //act
        Exception ex = null;
        try (var session = HibernateSession.getInstance().getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(disc);

            disc.getSongs().forEach(session::persist);

            session.getTransaction().commit();
        } catch (Exception e) {
            ex = e;
            new ErrorPrinter().appendException(e).print();
        }

        long newMaxDiscID = DatabaseManager.getInstance().getHighestDiscID();
        long newMaxSongID = DatabaseManager.getInstance().getHighestSongID();

        new TestPrinter().append("maxDiscID: " + maxDiscID)
                .appendNewLine("\tnewMaxDiscID: " + newMaxDiscID)
                .append("maxSongID: " + maxSongID)
                .append("\tnewMaxSongID: " + newMaxSongID)
                .print();

        //assert
        assert ex == null;
        assert maxDiscID == newMaxDiscID;
        assert maxSongID == newMaxSongID;
    }
}
