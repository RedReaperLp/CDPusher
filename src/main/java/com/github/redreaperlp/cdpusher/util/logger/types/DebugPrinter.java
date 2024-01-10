package com.github.redreaperlp.cdpusher.util.logger.types;

import com.github.redreaperlp.cdpusher.Main;
import com.github.redreaperlp.cdpusher.util.logger.Color;
import com.github.redreaperlp.cdpusher.util.logger.PlainPrinter;

public class DebugPrinter extends PlainPrinter {

    public DebugPrinter(MessagePart sender) {
        super(sender,  new MessagePart("DEBUG ", Color.ORANGE));
    }

    public DebugPrinter() {
        super(new MessagePart("DEBUG ", Color.ORANGE));
    }

    @Override
    public void print() {
        if (!Main.getInstance().debug) {
            return;
        }
        super.print();
    }
}
