package com.github.redreaperlp.cdpusher.util.logger.types;

import com.github.redreaperlp.cdpusher.util.logger.Color;
import com.github.redreaperlp.cdpusher.util.logger.PlainPrinter;

public class ErrorPrinter extends PlainPrinter {

    public ErrorPrinter(MessagePart sender) {
        super(sender, new MessagePart("ERROR ", Color.RED));
    }

    public ErrorPrinter() {
        super(new MessagePart("ERROR ", Color.RED));
    }

    @Override
    public ErrorPrinter append(String message, Color color) {
        messageParts.add(new MessagePart(message.contains("com.github.redreaperlp") ? "\t\t" + message : message, color));
        return this;
    }

    @Override
    public ErrorPrinter appendNewLine(String message, Color color) {
        append(message + "\n", color);
        return this;
    }

    public ErrorPrinter appendException(Exception e) {
        append(e.getMessage(), Color.RED);
        for (StackTraceElement trace : e.getStackTrace()) {
            appendNewLine("    at " + trace.getClassName() + "." + trace.getMethodName() + "(" + trace.getFileName() + ":" + trace.getLineNumber() + ")", Color.RED);
        }
        return this;
    }
}