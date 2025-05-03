package org.engine.utils;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;


public class ImGuiLogHandler extends Handler {
    @Override
public void publish(LogRecord record) {
    if (record.getLevel().intValue() >= Level.INFO.intValue()) {
        String logMessage = "[" + record.getLevel().getName() + "] " + record.getMessage();
        ImGuiHandler.logToConsole(logMessage);
    }
}

    @Override
    public void flush() {
        //SKIP
    }

    @Override
    public void close() throws SecurityException {
        //SKIP
    }
}