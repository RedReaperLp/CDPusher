package com.github.redreaperlp.cdpusher.util.logger;

public enum Color {
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    ORANGE("\u001B[38;5;208m"),
    INVERT_ORANGE("\033[48;5;196m"),
    BLUE("\u001B[34m"),
    LIGHT_BLUE("\u001B[38;5;39m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    INVERT_DARK_GREEN("\033[48;5;22m"),
    GRAY("\u001B[38;5;240m"),
    LIGHT_GRAY("\u001B[38;5;245m"),
    WHITE("\u001B[37m"),
    RESET("\u001B[0m");

    private final String color;

    Color(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return color;
    }
}
