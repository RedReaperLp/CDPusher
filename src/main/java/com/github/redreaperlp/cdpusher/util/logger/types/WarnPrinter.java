package com.github.redreaperlp.cdpusher.util.logger.types;

import com.github.redreaperlp.cdpusher.util.logger.Color;
import com.github.redreaperlp.cdpusher.util.logger.PlainPrinter;

public class WarnPrinter extends PlainPrinter {

    public WarnPrinter(com.github.redreaperlp.cdpusher.util.logger.types.MessagePart sender) {
        super(sender, new com.github.redreaperlp.cdpusher.util.logger.types.MessagePart("WARN  ", Color.YELLOW));
    }

    public WarnPrinter() {
        super(new com.github.redreaperlp.cdpusher.util.logger.types.MessagePart("WARN  ",Color.YELLOW));
    }
}
