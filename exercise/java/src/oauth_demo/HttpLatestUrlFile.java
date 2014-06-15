package oauth_demo;

import java.io.*;

/**
 * URL handler for the HTTP server.
 *
 * <p>This class performs the necessary actions when the server receives a request.
 *
 */
public class HttpLatestUrlFile {

    /** write the given URL to the latest URL file */
    public static void saveLatestUrl(String url) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(HttpConfig.LATEST_URL_FILE));
        bw.write(url+"\n");
        bw.close();
    }

    public static boolean latestUrlFileExists(boolean silent) {
        File f = new File(HttpConfig.LATEST_URL_FILE);
        return f.exists();
    }

    /**
     * Wait indefinitely until the latest URL file exists.
     *
     * @param timeout if not found after this number of seconds, give up
     *
     * @return true if file found, false if times out
     */
    public static boolean waitForLatestUrlFile(int timeout) {
        int SLEEP_TIME = 1; // second
        int timeOfFirstCheck = TimeNow.timeNowInt();
        try {
            ConsoleLogger.info("waiting for latest URL file %s...", HttpConfig.LATEST_URL_FILE);
            while(true) {
                if(HttpLatestUrlFile.latestUrlFileExists(true)) {
                    ConsoleLogger.info("latest URL file %s present", HttpConfig.LATEST_URL_FILE);
                    break;
                }
                else {
                    if((TimeNow.timeNowInt() - timeOfFirstCheck) > timeout) {
                        ConsoleLogger.error("latest URL file not found");
                        return false;
                    }
                    Thread.sleep(1000 * SLEEP_TIME);
                }
            }
        }
        catch (InterruptedException e) {
            ConsoleLogger.error("latest URL file not found");
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     */
    public static boolean waitForLatestUrlFile() {
        return HttpLatestUrlFile.waitForLatestUrlFile(10);
    }

}

