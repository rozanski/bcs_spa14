package uk.org.rozanski.oauth_demo.lib;

import java.io.*;
import java.util.Locale;

import com.dropbox.core.*;

/**
 * This class provides methods which implement the Dropbox <i>No-Redirect</i> workflow:
 *
 * <ul>
 *    <li>{@code noRedirectClientStart} implements the <b>start</b> step.
 *    <li>{@code noRedirectClientFinishAndSave} implements the <b>finish</b> step,
 *    saves the access token, and creates some sample files to show everything is working.
 * </ul>
 *
 * These methods are both called by the client.
 *
 */
public class DropboxWorkflowNoRedirect {

    /** Dropbox client object used to call authorisation functions */
    public static DbxWebAuthNoRedirect noredirectClient;

    /** we don't ever instantiate this class but just call its methods statically */
    private DropboxWorkflowNoRedirect() {}

    /**
     * This method implements the <b>start</b> step of the Dropbox no-redirect workflow.
     *
     * It passes the necessary parameters to {@code DbxWebAuthNoRedirect.start()}
     * and returns the generated URL to the caller.
     *
     * @return DropboxStatus object which is initialised with the URL to which
     *         the user must be redirected to authorise with Dropbox.
     *
     * @see <a href="https://www.dropbox.com/developers/core/start/java">Using the Core API in Java</a>
     * @see DropboxStatus
     */
    public static DropboxStatus noRedirectClientStart() {
        ConsoleLogger.debug("starting Dropbox authorisation (no-redirect mode)");
        ConsoleLogger.debug("creating DbxWebAuthNoRedirect client for app %s with key %s and secret %s",
                AppData.APP_NAME, AppData.APP_KEY, AppData.APP_SECRET);

        // EXERCISE:
        // - create a Dropbox no-redirect client object with which to execute the Dropbox no-redirect workflow
        //   hint: class is DbxWebAuthNoRedirect()
        // - this needs to be supplied with the app information
        //   hint: get this from AppData()
        // - it also needs to be supplied with a Dropbox DbxRequestConfig() object
        //   hint: use the app name/version from AppData and the default Locale
        // - call start() to start the no-redirect workflow
        //   @see http://dropbox.github.io/dropbox-sdk-java/api-docs/v1.7.x/com/dropbox/core/DbxWebAuthNoRedirect.html
        // - store the authorise URL returned by start() in authoriseUrl
        // SPA14_OAUTH_START
        DbxAppInfo appInfo = new DbxAppInfo(AppData.APP_KEY, AppData.APP_SECRET);
        DbxRequestConfig config = new DbxRequestConfig(AppData.APP_NAME_VERSION, Locale.getDefault().toString());
        noredirectClient = new DbxWebAuthNoRedirect(config, appInfo);
        ConsoleLogger.debug("created DbxWebAuthNoRedirect client, running start() to generate Dropbox authorisation URL");
        String authoriseUrl = noredirectClient.start();
        // SPA14_OAUTH_FINISH

        ConsoleLogger.info("Dropbox authorisation start successful, got authorisation URL %s", authoriseUrl);
        return new DropboxStatus(301, DropboxStatus.makeUrl(authoriseUrl));
    }

    /**
     * This method implements the <b>finish</b> step of the Dropbox no-redirect workflow.
     *
     * It calls {@code DbxWebAuthNoRedirect.finish()} to finish the Oauth workflow.
     * It then saves the access token returned by {@code finish()} and
     * creates some sample files in the Dropbox app folder.
     *
     * @param securityCode the security code which was generated by Dropbox and pasted into the client
     *
     * @return AccessData object containing the access token returned by {@code finish()}
     *
     * @throws IOException if there is an error creating the token file
     * @throws DbxException if a Dropbox error occurs
     *
     * @see <a href="https://www.dropbox.com/developers/core/start/java">Using the Core API in Java</a>
     * @see AccessData
     */
    public static AccessData noRedirectClientFinishAndSave(String securityCode) throws IOException, DbxException {
        // this function implements the finish() portion of the Dropbox OAuth no_redirect flow
        ConsoleLogger.debug("finishing Dropbox authorisation (no-redirect mode), security code=%s", securityCode);

        // EXERCISE:
        // - call finish() to start the no-redirect workflow
        //   @see http://dropbox.github.io/dropbox-sdk-java/api-docs/v1.7.x/com/dropbox/core/DbxWebAuthNoRedirect.html
        // - create an AccessData object from the token and user id returned by finish()
        // - save() this AccessData
        // SPA14_OAUTH_START
        DbxAuthFinish authFinish = noredirectClient.finish(securityCode);
        ConsoleLogger.info("Dropbox authorisation finish successful, access token=%s, user id=%s",
                authFinish.accessToken, authFinish.userId);
        AccessData accessData = new AccessData(authFinish.accessToken, authFinish.userId,
                "created using Java dropbox.client.DbxWebAuthNoRedirect()");
        accessData.save();
        // SPA14_OAUTH_FINISH

        DropboxTools.createSampleFiles();
        return accessData;
    }

}
