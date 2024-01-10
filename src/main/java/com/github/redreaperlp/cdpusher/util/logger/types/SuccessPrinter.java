package com.github.redreaperlp.cdpusher.util.logger.types;

import com.github.redreaperlp.cdpusher.util.logger.Color;
import com.github.redreaperlp.cdpusher.util.logger.PlainPrinter;

public class SuccessPrinter extends PlainPrinter {
    public SuccessPrinter(MessagePart sender){
        super(sender, new MessagePart("SUCCESS ", Color.GREEN));
    }

    public SuccessPrinter(){
        super(new MessagePart("SUCCESS ", Color.GREEN));
    }
}
