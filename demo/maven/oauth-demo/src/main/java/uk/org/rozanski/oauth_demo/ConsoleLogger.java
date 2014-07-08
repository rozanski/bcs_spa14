package uk.org.rozanski.oauth_demo;

import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

/**
 * Log debug, information and error messages to the console.
 *
 * <p>Usage:
 * <ul>
 * <li>{@code ConsoleLogger.error(message, arg, ...)}
 * <li>{@code ConsoleLogger.info(message, arg, ...)}
 * <li>{@code ConsoleLogger.debug(message, arg, ...)}
 * </ul>
 *
 */

public class ConsoleLogger {

    /** we don't ever instantiate a logger but just call its methods statically */
    private ConsoleLogger() {};

    private static Logger logger;
    private static Handler handler;

    // static class initialiser
    static {
        logger = Logger.getLogger(AppData.APP_NAME);
        logger.setUseParentHandlers(false);
        ConsoleHandler fch = new ConsoleHandler();
        logger.addHandler(fch);
        handler = logger.getHandlers()[0];
        enableDebug(System.getenv("OAUTH_DEBUG") != null);
    }

    /**
     * Log a debug message to the console.
     *
     * @param message message to be logged, which contains 3 format specifiers
     * @param arg1 first value to be substituted into the message
     * @param arg2 second value to be substituted into the message
     * @param arg3 third value to be substituted into the message
     */
    public static void debug(String message, Object arg1, Object arg2, Object arg3) {
        debug(String.format(message, arg1, arg2, arg3));
    }
    /**
     * {@inheritDoc}
     */
    public static void debug(String message, Object arg1, Object arg2) {
        debug(String.format(message, arg1, arg2));
    }
    /**
     * {@inheritDoc}
     */
    public static void debug(String message, Object arg1) {
        debug(String.format(message, arg1));
    }
    /**
     * {@inheritDoc}
     */
    public static void debug(String message) { logger.fine(message); }

    /**
     * Log an information message to the console.
     *
     * @param message message to be logged, which contains 3 format specifiers
     * @param arg1 first value to be substituted into the message
     * @param arg2 second value to be substituted into the message
     * @param arg3 third value to be substituted into the message
     */
    public static void info(String message, Object arg1, Object arg2, Object arg3) {
        info(String.format(message, arg1, arg2, arg3));
    }
    /**
     * {@inheritDoc}
     */
    public static void info(String message, Object arg1, Object arg2) {
        info(String.format(message, arg1, arg2));
    }
    /**
     * {@inheritDoc}
     */
    public static void info(String message, Object arg1) {
        info(String.format(message, arg1));
    }
    /**
     * {@inheritDoc}
     */
    public static void info(String message) { logger.info(message); }

    /**
     * Log an error message to the console.
     *
     * @param message message to be logged, which contains 3 format specifiers
     * @param arg1 first value to be substituted into the message
     * @param arg2 second value to be substituted into the message
     * @param arg3 third value to be substituted into the message
     */
    public static void error(String message, Object arg1, Object arg2, Object arg3) {
        error(String.format(message, arg1, arg2, arg3));
    }
    /**
     * {@inheritDoc}
     */
    public static void error(String message, Object arg1, Object arg2) {
        error(String.format(message, arg1, arg2));
    }
    /**
     * {@inheritDoc}
     */
    public static void error(String message, Object arg1) {
        error(String.format(message, arg1));
    }
    /**
     * {@inheritDoc}
     */
    public static void error(String message) { logger.severe(message); }

    /**
     * Enable / disable debug mode.
     *
     * @param enable if true, debug messages are logged,
     *               otherwise only information and error messages are logged
     */
    public static void enableDebug(boolean enable) {
        if (enable) { setLevel(Level.FINER); }
        else        { setLevel(Level.INFO); }
    }
    /**
     * Set logging level.
     *
     * @param level required logging level
     *
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/util/logging/Level.html">Logging Levels</a>
     */
    public static void setLevel(java.util.logging.Level level) {
        logger.setLevel(level);
        handler.setLevel(level);
    }
}

final class ConsoleHandler extends Handler {
    static SimpleDateFormat dateFormatter = new SimpleDateFormat ("dd-MM HH:mm:ss");
    public void publish(LogRecord record) {
        System.out.println(String.format("%s: %s (%s %s)",
            record.getLevel().toString(), record.getMessage(), record.getSourceClassName(),
            dateFormatter.format(new Date(record.getMillis()))));
    }
    public void close() {}
    public void flush() {}
}

