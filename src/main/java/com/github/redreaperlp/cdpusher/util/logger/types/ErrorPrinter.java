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
}
