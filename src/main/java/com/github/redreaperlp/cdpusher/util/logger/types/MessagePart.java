package com.github.redreaperlp.cdpusher.util.logger.types;

import com.github.redreaperlp.cdpusher.util.logger.Color;
import com.github.redreaperlp.cdpusher.util.logger.PlainPrinter;

public class MessagePart {
    String message;
    Color color;

    public MessagePart(String message, Color color) {
        this.message = message;
        this.color = color;
    }

    public MessagePart(String message) {
        this.message = message;
        this.color = Color.RESET;
    }

    @Override
    public String toString() {
        return PlainPrinter.checkColor(message, color);
    }

    public void toLength(int i) {
        while (message.length() < i) {
            message += " ";
        }
    }
}
