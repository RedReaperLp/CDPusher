package com.github.redreaperlp.cdpusher.util;

import com.github.redreaperlp.cdpusher.util.logger.types.ErrorPrinter;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileAccessor {
    /**
     * Returns the contents of a file as a byte array. (From the assets/html folder)
     *
     * @param filename The filename of the file.
     * @return The contents of the file as a byte array.
     */
    public static String html(String filename) throws IOException {
        return text("html/" + filename);
    }

    /**
     * Returns the contents of a file as a byte array. (From the assets/css folder)
     *
     * @param filename The filename of the file.
     * @return The contents of the file as a byte array.
     */
    public static String css(String filename) throws IOException {
        return text("css/" + filename);
    }

    /**
     * Returns the contents of a file as a byte array. (From the assets/javascript folder)
     *
     * @param filename The filename of the file.
     * @return The contents of the file as a byte array.
     */
    public static String js(String filename) throws IOException {
        return text("js/" + filename);
    }

    /**
     * Returns the contents of a file as a byte array. (From the assets folder)
     *
     * @param path The path of the file.
     * @return The contents of the file as a byte array.
     */
    public static String text(String path) throws IOException {
        String runPath = System.getProperty("user.dir");
        File file = new File(runPath + "/assets/" + path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        reader.close();
        return builder.toString();
    }

    public static byte[] image(String string) {
        String runPath = System.getProperty("user.dir");
        File file = new File(runPath + "/assets/images/" + string);
        try {
            FileInputStream reader = new FileInputStream(file);
            byte[] bytes = reader.readAllBytes();
            reader.close();
            return bytes;
        } catch (IOException e) {
            new ErrorPrinter().append("Image " + string + " not found").print();
            throw new RuntimeException(e);
        }
    }
}
