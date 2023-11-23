package com.github.redreaperlp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        try {
            InputStream is = new FileInputStream("F:\\Kuschelrock\\CD1\\01 Don't Speak.mp3");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}