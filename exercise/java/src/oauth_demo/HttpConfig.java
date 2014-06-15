package oauth_demo;

import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Configuration for the local httpd server (for the redirect authorisation workflow).
 *
 * <p><b>This must match the information  entered at the Dropbox developer console for the app.</b></p>
 *
 */
public class HttpConfig {

    /** hostname on which the local HTTP server is listening */
    public static String HTTP_SERVER = "localhost";
    /** port on which the local HTTP server is listening */
    public static int HTTP_PORT = 55520;

    /** index into the session structure which contains the session key */
    public static final String CSRF_SESSION_KEY = "csrf_token_session_key";

    /** home page served by the HTTP server */
    public static final String HOME_PAGE = "home";
    /** URL of home page */
    public static URL HOME_URL = null;

    /** start page for the redirect flow (not used in this demo) */
    public static final String START_PAGE = "dropbox-auth-start";
    /** URL of start page (not used in this demo) */
    public static URL START_URL = null;

    /** finish page for the redirect flow (opened in step 2) */
    public static final String FINISH_PAGE = "dropbox-auth-finish";
    /** URL of finish page */
    public static URL FINISH_URL = null;

    /** contains the latest URL requested of the HTTP server (used for testing) */
    public static String LATEST_URL_FILE = String.format("%s%shttpd_latest_url.log", CommonConfig.FILES_DIRECTORY, File.separator);;

    static {
        try {
            HOME_URL      = new URL("http", HTTP_SERVER, HTTP_PORT, "/"+HOME_PAGE);
            URL START_URL = new URL("http", HTTP_SERVER, HTTP_PORT, "/"+START_PAGE);
            FINISH_URL    = new URL("http", HTTP_SERVER, HTTP_PORT, "/"+FINISH_PAGE);
        }
        catch (MalformedURLException e) {
            // this is fatal
            ConsoleLogger.error("fatal error: failed to create Dropbox URLs, message='%s'", e.getMessage());
        }
    }
}

