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

    public static void main(String[] args) throws IOException {

        int SLEEP_TIME = 5; // how often to display a message to say the server is till running

        // run in debug mode if debug has been included in the command line
        for (String arg: args) {
            if (arg.toLowerCase().equals("debug")) {
                ConsoleLogger.enableDebug(true);
            }
        }

        InetSocketAddress inetSocketAddress = new InetSocketAddress(HttpConfig.HTTP_SERVER, HttpConfig.HTTP_PORT);
        HttpServer httpd = HttpServer.create(inetSocketAddress, 0);

        HttpdUrlHandler httpHandler = new HttpdUrlHandler();
        httpd.createContext("/", httpHandler);
        ConsoleLogger.debug("initialised HTTP server, finish URL is '%s'", HttpConfig.FINISH_URL.toString());

        ConsoleLogger.info("About to start the httpd server on '%s' listening on port %d...", HttpConfig.HTTP_SERVER, HttpConfig.HTTP_PORT);
        ConsoleLogger.info("Browse to the home page '%s' to test the server", HttpConfig.HOME_URL.toString());
        httpd.start();

        ConsoleLogger.info("Http server is running, press <Ctrl-C> to stop");
        BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
        try {
            int waitCount = 0;
            while(true) {
                Thread.sleep(1000);
                waitCount ++;
                if (!buffer.ready()) {
                    if (waitCount > SLEEP_TIME) {
                        ConsoleLogger.info("Http server is still running, press Enter to stop");
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

        ConsoleLogger.info("\nStopping httpd server...");
        httpd.stop(0);
    }

}

