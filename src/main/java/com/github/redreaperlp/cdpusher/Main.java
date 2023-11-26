package com.github.redreaperlp.cdpusher;

import com.github.redreaperlp.cdpusher.database.DatabaseConfiguration;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.json.JSONObject;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Main {

    private static Main instance;
    public DatabaseConfiguration conf;

    public static Main getInstance() {
        return instance;
    }

    public static void main(String[] args) throws IOException {

        instance = new Main();
        instance.init();
    }

    public void init() {
        JSONObject object = new JSONObject()
                .put("host", "localhost")
                .put("user", "radio")
                .put("password", "radio")
                .put("database", "radio_music");

        conf = new DatabaseConfiguration(object);
        conf.initDatabase();

        Logger.getLogger("org.jaudiotagger").setLevel(java.util.logging.Level.OFF);

        File f = new File("D:\\Abstürzende Brieftauben");
        for (File file : f.listFiles()) {
            if (file.isDirectory()) {
                instance.walk(file);
            }
        }
    }

    public void walk(File file) {
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isFile() && (f.getName().toLowerCase().endsWith(".mp3") || file.getName().toLowerCase().endsWith(".cda"))) {
                System.out.println(f.getName());
                Song song = extractMetadata(f);
                try (Connection con = conf.getDBManager().getConnection()) {
                    song.pushToDB(0, 0, con);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Song extractMetadata(File file) {
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTagOrCreateDefault();
            return new Song(tag, audioFile.getAudioHeader().getTrackLength());
        } catch (CannotReadException | TagException | ReadOnlyFileException | InvalidAudioFrameException |
                 IOException e) {
            throw new RuntimeException(e);
        }
    }
}
