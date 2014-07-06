package oauth_demo;

import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.net.*;
import com.sun.net.httpserver.*;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * URL handler for the HTTP server.
 *
 * <p>This class performs the necessary actions when the server receives a request.
 *
 */
public class HttpdUrlHandler implements HttpHandler {

    /**
     * Handle a GET request sent to the local HTTP server. (POSTs are ignored.)
     *
     * <p>The method sends the appropriate HTTP headers and message body back to the browser.
     *
     * @param exchange contains the request URL
     *
     * @throws IOException if there is an error reading the file
     *
     */
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();

        if (requestMethod.equalsIgnoreCase("GET")) {
            URI uri = exchange.getRequestURI();
            String uriPath = uri.getPath();
            String queryString = uri.getQuery();
            HttpLatestUrlFile.saveLatestUrl(uri.toString());
            ConsoleLogger.debug("handling GET request '%s', query string '%s'", uriPath, queryString);

            DropboxStatus statusAndResponse = getStatusAndResponse(uriPath, queryString);
            ConsoleLogger.debug("HTTP status is %d", statusAndResponse.httpStatus);

            // exchange.sendResponseHeaders(statusAndResponse.httpStatus, statusAndResponse.message.length());
            exchange.sendResponseHeaders(statusAndResponse.httpStatus, 0);
            OutputStream os = exchange.getResponseBody();
            os.write(statusAndResponse.message.getBytes());
            os.close();
        }
    }

    /**
     * Processes a request sent to the HTTP server.
     *
     * <p>This will be one of:
     *
     * <p><b>{@code HttpConfig.HOME_PAGE}</b>
     * <p>The method displays a home page (use this to test that the server is running ok)
     *
     * <p><b>{@code HttpConfig.FINISH_PAGE}</b>
     * <p>The user has been redirected here by the Dropbox website. The redirection URL includes the authorisation code
     * which will be used to generate the Dropbox token.
     * The method runs the finish step of the Dropbox redirect workflow by calling {@code DropboxWorkflowRedirect.httpdHandleFinishAndSave()}.
     *
     * @param uriPath The URL sent to the HTTP server
     * @param queryString The query string in the URL
     *
     * @return a DropboxStatus containing the HTTP status code and other information
     *
     * @throws IOException if there is an error reading the file
     *
     * @see DropboxStatus
     */
    DropboxStatus getStatusAndResponse(String uriPath, String queryString) throws IOException, MalformedURLException {
        ConsoleLogger.debug("In getStatusAndResponse, uriPath is %s", uriPath);
        if (uriPath.endsWith(HttpConfig.FINISH_PAGE)) {
            ConsoleLogger.debug("handling OAuth finish page");
            return DropboxWorkflowRedirect.httpdHandleFinishAndSave(uriPath, queryString);
        }
        else if (uriPath.endsWith(HttpConfig.HOME_PAGE)) {

            ConsoleLogger.debug("handling home page");
            String response = String.format(
                    "<h1>BCS SPA 2014 OAuth Demo</h1>\n" +
                    "<p>This is the local home page for the OAuth Demo.\n" +
                    "If you can read this your Java HTTP server is running successfully.</p>\n" +
                    "<h2>Configuration</h2>\n" +
                    "APP_NAME: <code>%s</code><br>\n" +
                    "DEMO_DIRECTORY: <code>%s</code><br>\n" +
                    "FILES_DIRECTORY: <code>%s</code><br>\n" +
                    "APP_KEY: <code>%s</code><br>\n" +
                    "APP_SECRET: <code>%s</code><br>\n" +
                    "ACCESS_TOKEN_FILE: <code>%s</code>\n",
                   AppData.APP_NAME, CommonConfig.DEMO_DIRECTORY, CommonConfig.FILES_DIRECTORY,
                   AppData.APP_KEY, AppData.APP_SECRET, AccessData.ACCESS_TOKEN_FILE);
            response += String.format(
                    "<h2>Help</h2>\n" +
                    "<dl>\n" +
                    "<dt>SPA conference session page:</dt><dd>%s</dd>\n" +
                    "<dt>Dropbox Java SDK:</dt><dd>%s</dd>\n" +
                    "<dt>Java language reference:</dt><dd>%s</dd>\n" +
                    "<dt>Dropbox developer page for demo app:</dt><dd>%s</dd>\n" +
                    "</dl>\n",
                   makeAnchor("http://spaconference.org/spa2014/sessions/session576.html"),
                   makeAnchor("http://dropbox.github.io/dropbox-sdk-java/api-docs/v1.7.x/"),
                   makeAnchor("http://docs.oracle.com/javase/8/docs/api/index.html"),
                   makeAnchor(AppData.APP_WEBSITE));
            response += String.format(
                    "<h2>Documentation</h2>\n" +
                    "<p>The javadoc files for the demo classes are here: <code>%s</code>\n" +
                    "<p>Click this link to view the javadoc in your browser: %s\n",
                   CommonConfig.DOC_DIRECTORY + File.separator + "oauth_demo" + File.separator,
                   makeAnchor(getDocumentationRootUrl(), true));
            return new DropboxStatus(200,DropboxStatus.makePage(response));

        }
        else if (uriPath.startsWith("/doc/")) {
            // chop off the initial '/doc/' and turn slashes into \ for Windows
            String docRelativePath = uriPath.substring(5).replace("/",File.separator);
            String docFullPath = CommonConfig.DOC_DIRECTORY + File.separator + docRelativePath;
            File docFile = new File(docFullPath);

            if (docFile.exists()) {
                ConsoleLogger.debug("serving contents of %s (URL is %s)", docFullPath, uriPath);
                String docFileContents = new String(Files.readAllBytes(Paths.get(docFullPath)), StandardCharsets.UTF_8);
                return new DropboxStatus(200, docFileContents);
            }
            else {
                ConsoleLogger.debug("DOCUMENTATION FILE NOT FOUND - URL=%s, file path=%s", uriPath, docFullPath);
                return new DropboxStatus(404, "Not Found");

            }

        }
        else if (uriPath.endsWith("favicon.ico")) {
            ConsoleLogger.debug("handling favicon.ico");
            return new DropboxStatus(404, "Not Found");
        }
        else {
            ConsoleLogger.debug("handling invalid page %s", uriPath);
            return new DropboxStatus(404, "Not Found");
        }
    }

    /** return an HTML anchor tag for the given URL */
    private static String makeAnchor(String url, boolean newWindow) {
        return String.format("<a href='%s' %s>%s</a>", url, (newWindow? "target='_blank'": ""), url);
    }

    /** return an HTML anchor tag for the given URL */
    private static String makeAnchor(String url) { return makeAnchor(url, true); }

    /** return the URL for the documentation page */
    private static String getDocumentationRootUrl()  throws MalformedURLException {
        String rootUrl = new URL("http", HttpConfig.HTTP_SERVER, HttpConfig.HTTP_PORT, "/doc/index.html").toExternalForm();
        ConsoleLogger.info("documentation URL is %s", rootUrl);
        return rootUrl;
    }
}

