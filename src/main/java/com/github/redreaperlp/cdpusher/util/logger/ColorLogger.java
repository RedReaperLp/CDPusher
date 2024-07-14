package com.github.redreaperlp.cdpusher.util.logger;

public interface ColorLogger {
    String getPrefix();
    ColorLogger append(String message, Color color);
    default ColorLogger append(String message) {
        append(message, Color.RESET);
        return this;
    }
    ColorLogger appendNewLine(String message, Color color);

    default ColorLogger appendNewLine(String message) {
        appendNewLine(message, Color.RESET);
        return this;
    }

    void print();
}
