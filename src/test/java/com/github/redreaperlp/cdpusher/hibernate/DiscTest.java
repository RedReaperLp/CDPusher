package com.github.redreaperlp.cdpusher.hibernate;

import com.github.redreaperlp.cdpusher.data.disc.DiscInformation;
import com.github.redreaperlp.cdpusher.data.song.Song;
import com.github.redreaperlp.cdpusher.util.logger.types.TestPrinter;
import org.junit.Test;

import java.util.List;

public class DiscTest {

    @Test
    public void testDisc() {
        var disc = new DiscInformation();
        disc.setCountry("country");
        disc.setYear(2021);
        disc.setTitle("title");
        disc.setLabel("label");
        disc.setResourceURL("resourceURL");
        disc.setSongs(List.of(
                new Song(-1, "title", "artist", "album", 1, 1, 1, 1, "imageURI")
        ));
        disc.pushToDB();
        disc.pushSongsToDB();
        new TestPrinter().append(disc.toJSON().toString(1)).print();
    }
}
