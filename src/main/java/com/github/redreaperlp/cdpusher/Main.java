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
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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

    List<List<String>> data = new ArrayList<>();

    JProgressBar progressBar = new JProgressBar();
    JTable table = new JTable();
    JPanel panel = new JPanel();

    public void init() {
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString("Lade Dateien...");
        progressBar.setMaximum(100);
        progressBar.setMinimum(0);
        progressBar.setVisible(true);

        table.setFillsViewportHeight(true);
        table.setPreferredSize(new Dimension(800, 100));
        table.setVisible(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1600, 900));
        scrollPane.setVisible(true);

        panel.setPreferredSize(new Dimension(1600, 900));
        panel.setLayout(new BorderLayout());
        panel.add(progressBar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.setVisible(true);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);

        update();

        progressBar.setString("Lade Datenbank...");

        JSONObject object = new JSONObject()
                .put("host", "localhost").put("user", "radio")
                .put("password", "radio").put("database", "radio_music");

        conf = new DatabaseConfiguration(object);
        conf.initDatabase();

        progressBar.setString("Lade Dateien...");

        Logger.getLogger("org.jaudiotagger").setLevel(java.util.logging.Level.OFF);

        System.out.print("Bitte Pfad angeben:\n> ");
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        scanner.close();

        File f = new File(path);

        new Thread(() -> {
            String lastHash = "";
            while (true) {
                try {
                    String hash = hashString(f);
                    progressBar.setString("Waiting for changes");
                    if (lastHash.equals(hash)) {
                        System.out.println("Keine Änderungen");
                        Thread.sleep(1000);
                    } else {
                        lastHash = hash;
                        Thread.sleep(1000);
                        while (true) {
                            hash = hashString(f);
                            if (lastHash.equals(hash)) {
                                break;
                            }
                            lastHash = hash;
                            progressBar.setIndeterminate(true);
                            progressBar.setString("Es werden noch Dateien kopiert...");
                            Thread.sleep(1000);
                        }
                        walk(f);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void update() {
        if (this.data.isEmpty()) {
            return;
        }
        Object[][] data = new Object[this.data.size()][this.data.get(0).size()];
        for (int i = 0; i < this.data.size(); i++) {
            for (int j = 0; j < this.data.get(i).size(); j++) {
                data[i][j] = this.data.get(i).get(j);
            }
        }


        table.setModel(new javax.swing.table.DefaultTableModel(
                data,
                new String[]{
                        "Titel", "Interpret", "Album", "Track", "Jahr", "Genre", "Kommentar", "Komponist", "DiscNo", "Länge"
                }
        ));
        //give every row a change listener
        for (int i = 0; i < table.getRowCount(); i++) {
            table.getModel().addTableModelListener(e -> {
                System.out.println("Changed");
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 0 || column == 1 || column == 4) {
                    String oldValue = (String) table.getValueAt(row, column);
                    String newValue = (String) table.getValueAt(row, column);
                    if (oldValue.equals(newValue)) {
                        return;
                    }
                    try (Connection con = conf.getDBManager().getConnection()) {
                        Song song = new Song((String) table.getValueAt(row, 0), (String) table.getValueAt(row, 1), (String) table.getValueAt(row, 2), (String) table.getValueAt(row, 3), (String) table.getValueAt(row, 4), (String) table.getValueAt(row, 5), (String) table.getValueAt(row, 6), (String) table.getValueAt(row, 7), (String) table.getValueAt(row, 8), (String) table.getValueAt(row, 9));
                        song.pushToDB(0, 0, con);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    public void walk(File file) {
        File[] files = file.listFiles();
        List<String> list = new ArrayList<>();
        progressBar.setIndeterminate(false);
        progressBar.setMaximum(files.length);
        progressBar.setString("Upload: 0%");

        for (File f : files) {
            if (f.isFile() && (f.getName().toLowerCase().endsWith(".mp3") || f.getName().toLowerCase().endsWith(".cda"))) {
                System.out.println(f.getName());
                Song song = extractMetadata(f);

                data.add(song.toArray());
                update();
                f.delete();

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

    public static String hashString(File f) {
        StringBuilder fileNames = new StringBuilder();
        for (File file : f.listFiles()) {
            if (file.isFile()) {
                fileNames.append(file.getName());
            }
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(fileNames.toString().getBytes(StandardCharsets.UTF_8));

            // Convert bytes to hexadecimal representation
            StringBuilder hexStringBuilder = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexStringBuilder.append('0');
                }
                hexStringBuilder.append(hex);
            }

            return hexStringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}