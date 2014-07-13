package uk.org.rozanski.oauth_demo.lib;

/**
 * Contains app key and secret for the demo app.
 *
 * <p>Also defines some other useful information and URLs.
 *
 */

public class AppData {

    /**
     * Dropbox app key for the demo app.
     *
     * @see <a href="https://www.dropbox.com/developers/apps">Dropbox apps</a>
     */
    public static final String APP_KEY = "3i8xil7ewl5d4el";
    /**
     * Dropbox app secret for the demo app.
     *
     * @see <a href="https://www.dropbox.com/developers/apps">Dropbox apps</a>
     */
    public static final String APP_SECRET = "0cf79q7jwrp5sjx";

    /** Application name (used in some API calls) */
    public static final String APP_NAME = "bcs_spa_oauth_demo";
    /** Application version (used in some API calls) */
    public static final String APP_VERSION = "bcs_spa_oauth_demo";
    /** Application name and version (used in some API calls) */
    public static final String APP_NAME_VERSION = String.format("%s/%s", APP_NAME, APP_VERSION);
    /** Application website (for info) */
    public static final String APP_WEBSITE = "https://www.dropbox.com/developers/apps/info/3i8xil7ewl5d4el";

    /** AppData is just used for data */
    private AppData() {};

    static {
        ConsoleLogger.info("Dropbox app key is %s, app secret is %s", APP_KEY, APP_SECRET);
    }

}

