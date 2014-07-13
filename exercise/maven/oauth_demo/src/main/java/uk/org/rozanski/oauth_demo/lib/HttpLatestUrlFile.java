package uk.org.rozanski.oauth_demo.lib;

import java.io.*;

/**
 * Manage Latest URL File
 *
 * <p>This writes data to the latest URL file, which contains the latest URL that has been requested of the browser
 * It is primarily used for testing.
 *
 */
public class HttpLatestUrlFile {

    /**
     * write the given URL to the latest URL file
     *
     * @param url the function writes this URL to the file
     *
     * @throws IOException if the file cannot be written
     *
     */
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

