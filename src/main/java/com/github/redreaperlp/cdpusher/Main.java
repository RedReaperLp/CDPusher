package com.github.redreaperlp.cdpusher;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Main {

    private static Main instance;
    public static Main getInstance() {
        return instance;
    }

    public static void main(String[] args) throws IOException {
        Logger.getLogger("org.jaudiotagger").setLevel(java.util.logging.Level.OFF);
        instance = new Main();

        List<Song> songs = new ArrayList<>();

        File f = new File("F:\\Kuschelrock\\CD1");
        for (File file : f.listFiles()) {
            songs.add(instance.extractMetadata(file));
        }
        songs.forEach(System.out::println);
    }

    private Song extractMetadata(File file) throws IOException {
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTagOrCreateDefault();
            return new Song(tag, audioFile.getAudioHeader().getTrackLength());
        } catch (CannotReadException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            throw new RuntimeException(e);
        }
    }
}