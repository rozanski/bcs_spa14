package uk.org.rozanski.oauth_demo.lib;

import java.io.*;

import java.net.*;
import com.sun.net.httpserver.*;

/**
 * This library implements a simple HTTP server on the computer running the demo.
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
 * see http://www.java2s.com/Tutorial/Java/0320__Network/LightweightHTTPServer.htm
 */
public class HttpdServer {

    /**
     *
     * This function starts the HTTP server (in another thread) and then returns.
     *
     * @throws IOException if server cannot start
     */
    public static void startServer() throws IOException {

        InetSocketAddress inetSocketAddress = new InetSocketAddress(HttpConfig.HTTP_SERVER, HttpConfig.HTTP_PORT);
        httpd = HttpServer.create(inetSocketAddress, 0);

        HttpdUrlHandler httpHandler = new HttpdUrlHandler();
        httpd.createContext("/", httpHandler);
        ConsoleLogger.debug("initialised HTTP server, finish URL is '%s'", HttpConfig.FINISH_URL.toString());

        ConsoleLogger.info("About to start the httpd server on '%s' listening on port %d...", HttpConfig.HTTP_SERVER, HttpConfig.HTTP_PORT);
        ConsoleLogger.info("Browse to the home page '%s' to test the server", HttpConfig.HOME_URL.toString());
        httpd.start();

        ConsoleLogger.info("Http server is running, press <Ctrl-C> to stop");
    }

    /**
     *
     * This function waits for the user to press Ctrl-C, after which it stops the HTTP server and returns.
     *
     * @throws IOException if a console I/O error occurs
     */
    public static void watchServer() throws IOException {

        int SLEEP_TIME = 5; // how often to display a message to say the server is till running

        BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
        try {
            int waitCount = 0;
            while(true) {
                Thread.sleep(1000);
                waitCount ++;
                if (!buffer.ready()) {
                    if (waitCount > SLEEP_TIME) {
                        ConsoleLogger.info("Http server is still running, press <Ctrl-C> to stop");
                        waitCount = 0;
                    }
                }
                else {
                    String line=buffer.readLine();
                    break;
                }
            }
        }
        catch (InterruptedException e) { }

    }

    /**
     *
     * This function stops the HTTP server and returns.
     *
     * @throws IOException if a console I/O error occurs
     */
    public static void stopServer() throws IOException {

        ConsoleLogger.info("\nStopping httpd server...");
        httpd.stop(0);
    }

    private static HttpServer httpd;

}

