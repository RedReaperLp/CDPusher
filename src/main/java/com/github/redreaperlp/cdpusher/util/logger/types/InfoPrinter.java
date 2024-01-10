package com.github.redreaperlp.cdpusher.util.logger.types;

import com.github.redreaperlp.cdpusher.util.logger.Color;
import com.github.redreaperlp.cdpusher.util.logger.PlainPrinter;

public class InfoPrinter extends PlainPrinter {

    public InfoPrinter(MessagePart sender) {
        super(sender, new MessagePart("INFO  ", Color.LIGHT_BLUE));
    }

    public InfoPrinter() {
        super(new MessagePart("INFO  ", Color.LIGHT_BLUE));
    }
}
