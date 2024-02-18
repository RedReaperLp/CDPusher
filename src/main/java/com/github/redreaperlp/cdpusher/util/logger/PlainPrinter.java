package com.github.redreaperlp.cdpusher.util.logger;

import com.github.redreaperlp.cdpusher.Main;
import com.github.redreaperlp.cdpusher.util.logger.types.MessagePart;

import java.util.ArrayList;
import java.util.List;

public class PlainPrinter implements ColorLogger {
    private MessagePart prefix;
    public List<MessagePart> messageParts = new ArrayList<>();
    private MessagePart sender;

    public PlainPrinter(MessagePart sender, MessagePart prefix) {
        this.sender = sender;
        this.prefix = prefix;
        this.prefix.toLength(8);
    }

    public PlainPrinter(MessagePart prefix) {
        this.prefix = prefix;
        this.prefix.toLength(8);
        this.sender = new MessagePart("[CDPusher]", Color.GREEN);
    }

    @Override
    public String getPrefix() {
        List<MessagePart> messageParts = new ArrayList<>(List.of(new MessagePart("<", Color.YELLOW),
                new MessagePart(counter(), Color.GREEN),
                new MessagePart("> ", Color.YELLOW),
                prefix,
                sender,
                new MessagePart(":", Color.YELLOW)));
        StringBuilder msg = new StringBuilder();
        for (MessagePart messagePart : messageParts) {
            msg.append(messagePart);
        }
        return msg.toString();
    }

    @Override
    public ColorLogger append(String message, Color color) {
        messageParts.add(new MessagePart(message, color));
        return this;
    }

    @Override
    public ColorLogger appendNewLine(String message, Color color) {
        messageParts.add(new MessagePart(message + "\n", color));
        return this;
    }

    @Override
    public void print() {
        StringBuilder msg = new StringBuilder();
        for (MessagePart messagePart : messageParts) {
            msg.append(messagePart);
        }
        String[] lines = msg.toString().split("\n");
        for (String line : lines) {
            System.out.println(getPrefix() + " " + line);
        }
    }


    static long counter = 0;

    public static String counter() {
        counter++;
        String underspaces = "____";
        String sCounter = String.valueOf(counter);
        if (sCounter.length() > 3) {
            for (int i = 0; i < sCounter.length() - 3; i++) {
                underspaces += "_";
            }
        }
        underspaces = underspaces.substring(0, underspaces.length() - sCounter.length());
        return counter + underspaces;
    }

    public MessagePart getType() {
        return prefix;
    }

    public MessagePart getSender() {
        return sender;
    }

    public List<MessagePart> getMessage() {
        return messageParts;
    }

    public static String checkColor(String message, Color color) {
        if (Main.getInstance().colored) {
            return color + message + Color.RESET;
        } else {
            return message;
        }
    }
}
