package oauth_demo;

import java.net.URL;
import java.net.MalformedURLException;

/**
 * This class contains status information used after completion of a workflow task.
 *
 * <p>It includes an HTTP status, message and an optional redirect URL.
 *
 * <p>Construct an object as follows:
 *
 * <p>{@code DropboxStatus dropboxStatus = new DropboxStatus(200, pageBody)};
 * <p>{@code DropboxStatus dropboxStatus = new DropboxStatus(301, DropboxStatus.makeUrl(redirectUrl))};
 * <p>{@code DropboxStatus dropboxStatus = new DropboxStatus(400, &quot;Forbidden&quot;);};
 */
public class DropboxStatus {

    /**
     * HTTP status following an authorisation action.
     *
     * <p>Valid values include:
     * <ul>
     * <li><b>200</b> - success
     * <li><b>301</b> - URL redirect required
     * <li><b>4xx, 5xx</b> - an error has occurred
     * </ul>
     *
     * @see <a href="https://www.dropbox.com/developers/core/docsauthorize">Dropbox Standard API errors</a>
     */
    public int httpStatus;

    /**
     * HTTP status message.
     *
     * <p>For httpStatus = 200, this is the entire HTML page being served
     *
     */
    public String message;

    /**
     * Time at which action was performed (eg authorisation completed).
     *
     */
    public String timeNow;

    /**
     * If redirection is required, the URL to redirect to.
     *
     * <p>Redirection is indicated by an httpStatus of 301.
     * It occurs in the second step of the redirect workflow.
     *
     * If httpStatus is not 301, this field is ignored.
     *
     */
    public URL redirectUrl;

    /**
     * Constructor when an HTTP status is needed.
     *
     * @param httpStatus HTTP status
     */
    public DropboxStatus(int httpStatus) {
        this.__DropboxStatus(httpStatus, "", null);
    }
    /**
     * Constructor when an HTTP status and message are needed.
     * @param message message to be displayed - if status is 200, then the entire HTML page,
     *        otherwise (for an error status) the error text (eg 'Forbidden' for 400)
     *
     * @param httpStatus HTTP status
     */
    public DropboxStatus(int httpStatus, String message) {
        this.__DropboxStatus(httpStatus, message, null);
    }
    /**
     * Constructor when a redirect URL is needed.
     *
     * @param httpStatus HTTP status (will normally be 301)
     * @param redirectUrl URL to redirect to
     */
    public DropboxStatus(int httpStatus, URL redirectUrl) {
        this.__DropboxStatus(httpStatus, "redirecting to " + redirectUrl, redirectUrl);
    }
    /**
     * Constructor when a message and redirect URL are needed.
     *
     * @param httpStatus HTTP status (will normally be 301)
     * @param message message to be displayed for redirect
     * @param redirectUrl URL to redirect to
     */
    public DropboxStatus(int httpStatus, String message, URL redirectUrl) {
        this.__DropboxStatus(httpStatus, message, redirectUrl);
    }

    /**
     * Construct a URL object from a string.
     *
     * <p>This method is provided to simplify constructing objects of this class.
     *
     * @param urlString the string representation of a URL, eg {@code http://www.example.com/home}
     *
     * @return Java URL object representing the URL
     *
     */
    public static URL makeUrl(String urlString) {
        try {
            return new URL(urlString);
        }
        catch (MalformedURLException e) {
            // this is fatal
            ConsoleLogger.error("fatal error: failed to create Dropbox URLs, message='%s'", e.getMessage());
            return null;
        }
    }

    /**
     * Helper fucntion to construct an HTML page for a given page body
     *
     * @param body the &lt;BODY&gt; of the page.
     *
     * @return string containing all of the &lt;HTML&gt; of the page
     *
     */
    public static String makePage(String body) {
        return "<html>\n<head>\n<title>BCS SPA 2014 - OAuth</title>\n</head>\n<body>\n"+
             String.format("%s<p><i>%s</i></p>\n</body>\n</html>", body, TimeNow.timeNowString());
    }

    private void __DropboxStatus(int httpStatus, String message, URL redirectUrl) {
        this.httpStatus = httpStatus;
        this.timeNow = TimeNow.timeNowString();
        this.message = message;
        this.redirectUrl = redirectUrl;
    }

}

