package oauth_demo;

import java.io.*;
import java.util.Locale;
import java.util.LinkedHashMap;
import java.util.Map;
import java.net.*;

import com.dropbox.core.*;

/**
 * This class provides methods which implement the Dropbox <i>Redirect</i> workflow:
 *
 * <ul>
 *    <li>{@code redirectClientStart} implements the <b>start</b> step.
 *    <li>{@code httpdHandleFinishAndSave} implements the <b>finish</b> step,
 *    saves the access token, and creates some sample files to show everything is working.
 * </ul>
 *
 * <p>{@code redirectClientStart} is called by the client.
 * {@code httpdHandleFinishAndSave} is called by the local HTTP server.
 *
 */
public class DropboxWorkflowRedirect {

    /**
     * CSRF token
     * @see CsrfTokenStore
     */
    public static CsrfTokenStore csrfTokenStore = new CsrfTokenStore();

    /** we don't ever instantiate this class but just call its methods statically */
    private DropboxWorkflowRedirect() {}

    /**
     * This method implements the <b>start</b> step of the Dropbox redirect workflow.
     *
     * It passes the necessary parameters to {@code DbxWebAuthNoRedirect.start()}
     * and returns the generated URL to the caller.
     *
     * @return DropboxStatus object which is initialised with the URL to which the user must be redirected to authorise with Dropbox.
     *
     * @see DropboxStatus
     */
    public static DropboxStatus redirectClientStart() {
        ConsoleLogger.debug("starting Dropbox authorisation (redirect mode)");
        ConsoleLogger.debug("creating DbxWebAuth client for app %s with key %s and secret %s",
                AppData.APP_NAME, AppData.APP_KEY, AppData.APP_SECRET);

        // EXERCISE:
        //  - create a Dropbox redirect client object with which to execute the Dropbox no-redirect workflow
        //    hint: class is DbxWebAuth()
        //  - this needs to be supplied with:
        //    - the app information (hint: get this from AppData())
        //    - a Dropbox DbxRequestConfig() object (hint: use the app name/version from AppData and the default Locale)
        //    - the URL to which the user will be redirected for the finish step (hint: get this from HttpConfig)
        //    - the CSRF token store used to prevent CSRF attacks (hint: this is a public variable to this class)
        //  - call start() to start the redirect workflow
        //  - store the authorise URL returned by start() in authoriseUrl
        //    @see http://dropbox.github.io/dropbox-sdk-java/api-docs/v1.7.x/com/dropbox/core/DbxWebAuth.html
// TODO ==> INSERT CODE HERE <==

        ConsoleLogger.info("Dropbox authorisation start successful, got authorisation URL %s", authoriseUrl);
        return new DropboxStatus(301, DropboxStatus.makeUrl(authoriseUrl));
    }

    /**
     * This method implements the <b>finish</b> step of the Dropbox redirect workflow.
     *
     * It calls {@code DbxWebAuth.finish()} to finish the Oauth workflow.
     * It then saves the access token returned by {@code finish()} and
     * creates some sample files in the Dropbox app folder.
     *
     * @param uriPath the local URL to which the user has been redirected by Dropbox (not actually needed, just for info)
     * @param queryString the query string from that local URL (this will be used to get the session token and authorsiation code)
     *
     * @return AccessData object containing the access token returned by {@code finish()}
     *
     * @throws IOException if there is an error creating the token file
     *
     * @see AccessData
     */
    public static DropboxStatus httpdHandleFinishAndSave(String uriPath, String queryString) throws IOException {
        try {
            ConsoleLogger.debug("finishing Dropbox authorisation (redirect mode), uri=%s, query='%s'", uriPath, queryString);

            Map<String, String[]> queryParams = new LinkedHashMap<String, String[]>();
            for (String query : queryString.split("&")) {
                int idx = query.indexOf("=");
                String queryParam = URLDecoder.decode(query.substring(0, idx), "UTF-8");
                String queryValue = URLDecoder.decode(query.substring(idx + 1), "UTF-8");
                // the URL won't ever have the same query multiple times
                queryParams.put(queryParam, new String[]{queryValue});
                ConsoleLogger.debug("URL query parameter %s='%s'", queryParam, queryValue);
            }

            // EXERCISE:
            //  - create a Dropbox redirect client object with which to execute the Dropbox no-redirect workflow
            //    hint: class is DbxWebAuth()
            //  - this needs to be supplied with various parameters (see start() above))
            //    @see http://dropbox.github.io/dropbox-sdk-java/api-docs/v1.7.x/com/dropbox/core/DbxWebAuth.html
            //  - call finish() to finish the redirect workflow
            //  - clear the CSRF token store
            //  - create an AccessData object from the token and user id returned by finish()
            //  - save() this AccessData
// TODO ==> INSERT CODE HERE <==

            DropboxTools.createSampleFiles();
            return new DropboxStatus(200, DropboxStatus.makePage(
                        "<h1>Congratulations!</h1><p>The Dropbox access token was created successfully.<p>You may return to your client."));
        }
        catch (DbxWebAuth.BadRequestException ex) {
            ConsoleLogger.error("Bad request on finish, error=" + ex.getMessage());
            csrfTokenStore.clear();
            return new DropboxStatus(400, "Bad request");
        }
        catch (DbxWebAuth.BadStateException ex) {
            // Send them back to the start of the auth flow.
            ConsoleLogger.error("Bad state on finish, error=" + ex.getMessage());
            csrfTokenStore.clear();
            return new DropboxStatus(301, "Bad request", HttpConfig.START_URL);
        }
        catch (DbxWebAuth.CsrfException ex) {
            ConsoleLogger.error("CSRF mismatch on finish, error=" + ex.getMessage());
            csrfTokenStore.clear();
            return new DropboxStatus(400, "Bad request");
        }
        catch (DbxWebAuth.NotApprovedException ex) {
            // When Dropbox asked "Do you want to allow this app to access your Dropbox account?", the user clicked "No".
            ConsoleLogger.error("user declined Dropbox authorisation");
            csrfTokenStore.clear();
            return new DropboxStatus(301, "Bad request", HttpConfig.START_URL);
        }
        catch (DbxWebAuth.ProviderException ex) {
            ConsoleLogger.error("Dropbox authorisation failed, error=" + ex.getMessage());
            csrfTokenStore.clear();
            return new DropboxStatus(503, "Service unavailable");
       }
       catch (DbxException ex) {
            ConsoleLogger.error("Dropbox authorisation failed, error=" + ex.getMessage());
            csrfTokenStore.clear();
            return new DropboxStatus(503, "Service unavailable");
       }
    }

}

