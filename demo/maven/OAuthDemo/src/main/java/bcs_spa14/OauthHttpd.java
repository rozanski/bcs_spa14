import java.io.*;

import java.net.*;
import com.sun.net.httpserver.*;

import oauth_demo.*;

/**
 * This class implements a simple HTTP server on the computer running the demo.
 *
 * <p>The server listens on host {@code HttpConfig.HTTP_SERVER} and port {@code HttpConfig.HTTP_PORT}.
 * It runs indefinitely until the user interrupts using control-C.
 *
 * It serves various URLs, including:
 * <ul>
 * <li>{@code HttpConfig.HOME_PAGE} - display a home page (use this to test that the server is running ok)
 * <li>{@code HttpConfig.FINISH_PAGE} - run the finish step of the Dropbox redirect workflow.
 * </ul>
 *
 * @see http://www.java2s.com/Tutorial/Java/0320__Network/LightweightHTTPServer.htm
 */
public class OauthHttpd {

    /**
     *
     * This function is invoked when the HTTP server is started from the command line.
     * It runs the server indefinitely.
     *
     * For example:
     *     $ export CLASSPATH=...
     *     $ java OauthHttpd
     *
     * @param args arguments passed to the command line (the only valid argument is the string 'debug')
     *
     * @throws IOException if server cannot start
     */
    public static void main(String[] args) throws IOException {

        // run in debug mode if debug has been included in the command line
        for (String arg: args) {
            if (arg.toLowerCase().equals("debug")) { ConsoleLogger.enableDebug(true); }
        }

        ConsoleLogger.info("running HTTP server from the command line");
        HttpdServer.startServer();
        HttpdServer.watchServer();
    }

}

