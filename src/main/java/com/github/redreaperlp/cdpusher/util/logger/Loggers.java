package com.github.redreaperlp.cdpusher.util.logger;

import ch.qos.logback.classic.Logger;
import com.github.redreaperlp.cdpusher.util.logger.types.DebugPrinter;
import com.github.redreaperlp.cdpusher.util.logger.types.SuccessPrinter;
import org.slf4j.LoggerFactory;

public class Loggers {
    public static void load() {
        new DebugPrinter().append("Loading Loggers").print();
        Logger logger = (Logger) LoggerFactory.getLogger("com.zaxxer.hikari");
        logger.getLoggerContext().reset();
        logger.addAppender(new ColorLoggerFactory());
        logger.getAppender("ColorLogger").start();

        Logger javalinlogger = (Logger) LoggerFactory.getLogger("io.javalin");
        javalinlogger.addAppender(new ColorLoggerFactory());
        javalinlogger.getAppender("ColorLogger").start();

        Logger eclipseLogger = (Logger) LoggerFactory.getLogger("org.eclipse.jetty");
        eclipseLogger.addAppender(new ColorLoggerFactory());
        eclipseLogger.getAppender("ColorLogger").start();
        new SuccessPrinter().append("Loggers loaded").print();

    }
}
