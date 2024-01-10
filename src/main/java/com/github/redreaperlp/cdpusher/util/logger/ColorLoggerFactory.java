package com.github.redreaperlp.cdpusher.util.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.LogbackException;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.Status;
import com.github.redreaperlp.cdpusher.util.logger.types.*;

import java.util.List;

public class ColorLoggerFactory implements Appender<ILoggingEvent> {

    static String error;
    static int countDown = 2;
    static Thread sender;

    @Override
    public String getName() {
        return "ColorLogger";
    }

    @Override
    public void doAppend(ILoggingEvent event) throws LogbackException {
        String loggername = event.getLoggerName().split("\\.")[event.getLoggerName().split("\\.").length - 1];
        if (event.getLevel() == Level.DEBUG) {
            DebugPrinter printer = new DebugPrinter(new MessagePart("[" + loggername + "]", Color.YELLOW));
            printer.append(event.getFormattedMessage()).print();
            return;
        } else if (event.getLevel() == Level.INFO) {
            InfoPrinter printer = new InfoPrinter(new MessagePart("[" + loggername + "]", Color.YELLOW));
            printer.append(event.getFormattedMessage()).print();
            return;
        } else if (event.getLevel() == Level.WARN) {
            WarnPrinter printer = new WarnPrinter(new MessagePart("[" + loggername + "]", Color.YELLOW));
            printer.append(event.getFormattedMessage()).print();
            return;
        } else if (event.getLevel() == Level.ERROR) {
            ErrorPrinter printer = new ErrorPrinter(new MessagePart("[" + loggername + "]", Color.YELLOW));
            printer.append(event.getFormattedMessage());
            for (StackTraceElement trace : event.getCallerData()) {
                printer.appendNewLine("    at " + trace.getClassName() + "." + trace.getMethodName() + "(" + trace.getFileName() + ":" + trace.getLineNumber() + ")", Color.RED);
            }
            printer.print();
            return;
        }
        new InfoPrinter(new MessagePart("[" + loggername + "]", Color.YELLOW)).append(event.getFormattedMessage()).print();
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void setContext(Context context) {

    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void addStatus(Status status) {

    }

    @Override
    public void addInfo(String msg) {
        new InfoPrinter().append(msg).print();
    }

    @Override
    public void addInfo(String msg, Throwable ex) {
        new InfoPrinter().append(msg).print();
    }

    @Override
    public void addWarn(String msg) {
        new WarnPrinter().append(msg).print();
    }

    @Override
    public void addWarn(String msg, Throwable ex) {
        new WarnPrinter().append(msg).print();
    }

    @Override
    public void addError(String msg) {
        new ErrorPrinter().append(msg).print();
    }

    @Override
    public void addError(String msg, Throwable ex) {
        new ErrorPrinter().append(msg).print();
    }

    @Override
    public void addFilter(Filter<ILoggingEvent> newFilter) {

    }

    @Override
    public void clearAllFilters() {

    }

    @Override
    public List<Filter<ILoggingEvent>> getCopyOfAttachedFiltersList() {
        return null;
    }

    @Override
    public FilterReply getFilterChainDecision(ILoggingEvent event) {
        return null;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isStarted() {
        return true;
    }
}