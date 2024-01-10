package com.github.redreaperlp.cdpusher.util.logger.types;


import com.github.redreaperlp.cdpusher.util.logger.Color;
import com.github.redreaperlp.cdpusher.util.logger.PlainPrinter;

import java.util.ArrayList;
import java.util.List;

public class TestPrinter extends PlainPrinter {

    public TestPrinter(MessagePart sender) {
        super(sender,new MessagePart("TEST  ", Color.RED));
    }

    public TestPrinter() {
        super(new MessagePart("TEST  ", Color.RED));
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

    @Override
    public String getPrefix() {
        List<MessagePart> messageParts = new ArrayList<>(List.of(
                new MessagePart("<", Color.YELLOW),
                new MessagePart(counter(), Color.RED),
                new MessagePart("> ", Color.YELLOW),
                getType(),
                getSender(),
                new MessagePart(":", Color.YELLOW)));
        StringBuilder msg = new StringBuilder();
        for (MessagePart messagePart : messageParts) {
            msg.append(messagePart);
        }
        return msg.toString();
    }
}
