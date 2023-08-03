package com.ibm.mq.demo;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerInitializer {
    private static final Level LOGLEVEL = Level.ALL;
    private static final Logger logger = Logger.getLogger("com.ibm.mq.demo");


    public static void initialiseLogging() {
        Logger defaultLogger = Logger.getLogger("");
        Handler[] handlers = defaultLogger.getHandlers();
        if (handlers != null && handlers.length > 0) {
            defaultLogger.removeHandler(handlers[0]);
        }

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(LOGLEVEL);
        logger.addHandler(consoleHandler);

        logger.setLevel(LOGLEVEL);
        logger.finest("Logging initialised");
    }
}
