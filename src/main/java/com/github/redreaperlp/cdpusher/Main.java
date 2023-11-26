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

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.File;
import java.io.IOException;
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

    JProgressBar progressBar = new JProgressBar();
    JTextArea textArea = new JTextArea();
    JPanel panel = new JPanel();

    public void init() {
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString("Lade Dateien...");
        progressBar.setMaximum(100);
        progressBar.setMinimum(0);
        progressBar.setVisible(true);

        textArea.setEditable(false);
        textArea.setPreferredSize(new Dimension(500, 100));
        textArea.setVisible(true);

        JScrollPane scroll = new JScrollPane(textArea);

        panel.setLayout(new BorderLayout());
        panel.add(progressBar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);

        JSONObject object = new JSONObject()
                .put("host", "localhost")
                .put("user", "radio")
                .put("password", "radio")
                .put("database", "radio_music");

        conf = new DatabaseConfiguration(object);
        conf.initDatabase();

        Logger.getLogger("org.jaudiotagger").setLevel(java.util.logging.Level.OFF);

        File f = new File("F:\\Kuschelrock\\CD2\\");
        instance.walk(f);

        conf.getDBManager().close();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }

    public void walk(File file) {
        File[] files = file.listFiles();
        List<String> list = new ArrayList<>();
        progressBar.setIndeterminate(false);
        progressBar.setMaximum(files.length);
        progressBar.setString("Upload: 0%");

        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        for (File f : files) {
            if (f.isFile() && (f.getName().toLowerCase().endsWith(".mp3") || f.getName().toLowerCase().endsWith(".cda"))) {
                System.out.println(f.getName());
                Song song = extractMetadata(f);
                try (Connection con = conf.getDBManager().getConnection()) {
                    song.pushToDB(0, 0, con);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressBar.setValue(progressBar.getValue() + 1);
                progressBar.setString("Upload: " + 100 * progressBar.getValue() / progressBar.getMaximum() + "%");

                list.add(song.title);
                if (list.size() > 6) {
                    list.remove(0);
                }
                textArea.setText(String.join("\n", list));
                textArea.setCaretPosition(textArea.getDocument().getLength());
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
