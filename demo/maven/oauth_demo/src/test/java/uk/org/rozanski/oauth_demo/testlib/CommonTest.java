package uk.org.rozanski.oauth_demo;

import java.io.*;
import static org.junit.Assert.*;

import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import uk.org.rozanski.oauth_demo.*;
import uk.org.rozanski.oauth_demo.lib.*;

// import com.dropbox.core.*;

public class CommonTest {

    public static String TEST_USER_ID = "1014682";
    public static String TEST_ACCESS_TOKEN = "9gu6UWQLjvEAAAAAAAAGBBfj-0YRipPAIzkElaJwh1HncZDUF4wodCIH3yfK7b7l";

    public static CsrfTokenStore csrfTokenStore = new CsrfTokenStore();

    /*
     * =================
     * CUSTOM ASSERTIONS
     * =================
     */

    public static void assertAccessDataEqual(AccessData accessData1, String description1,
            AccessData accessData2, String description2) {
        assertEquals(String.format("%s accessToken '%s' does not equal %s accessToken '%s",
                    accessData1.accessToken, description1, accessData2.accessToken, description2),
                accessData1.accessToken, accessData2.accessToken);
        assertEquals(String.format("%s userId '%s' does not equal %s userId '%s",
                    accessData1.userId, description1, accessData2.userId, description2),
                accessData1.userId, accessData2.userId);
        assertEquals(String.format("%s saveMessage '%s' does not equal %s saveMessage '%s",
                    accessData1.saveMessage, description1, accessData2.saveMessage, description2),
                accessData1.saveMessage, accessData2.saveMessage);
    }

    public static void assertUrlValid(String url) {
        try {
            URL u = new URL(url);
        }
        catch (MalformedURLException e) {
            assertTrue(String.format("URL %s is not valid",url), false);
        }
    }

    public static void assertUrlReachable(String url) throws MalformedURLException, IOException, ProtocolException {
        try{
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(10);
            connection.setReadTimeout(10);
            connection.setRequestMethod("HEAD");
            connection.connect();
        } catch(final IOException e){
            assertTrue(String.format("URL %s is not reachable",url), false);
        }
    }

    /*
     *  ==============
     *  CAPTURE STDOUT
     *  ==============
     */
    public void captureStdout(boolean captureOn) {
        // @see http://fw-geekycoder.blogspot.co.uk/2011/04/how-to-capture-stdout-into-variable.html
        if (captureOn) {
            saveStdout = System.out;
            baos = new ByteArrayOutputStream(10000);
            PrintStream out = new PrintStream(baos);
            System.setOut(out);
        }
        else {
            if (saveStdout != null) {
                System.setOut(saveStdout);
                saveStdout = null;
            }
        }
    }

    public String getStdoutContents() {
        return new String(baos.toByteArray());
    }

    /*
     *  ========================
     *  MANAGE ACCESS TOKEN FILE
     *  ========================
     */
    public AccessData createAndSaveAccessData(String testName) throws IOException {
        AccessData saveAccessData = new AccessData("TOKEN " + testName, "12345678", "TEST " + testName);
        saveAccessData.save();
        File f = new File(AccessData.ACCESS_TOKEN_FILE);
        assertTrue("ACCESS_TOKEN_FILE not created by save()", f.canRead());
        return saveAccessData;
    }

    public void deleteAccessData(String testName) throws IOException {
        deleteFile(AccessData.ACCESS_TOKEN_FILE);
    }

    /*
     *  ====================
     *  MANAGE SESSION STORE
     *  ====================
     */
    public String createAndSaveSessionData(String testName) throws IOException {
        String csrfSessionKey = "SESSION " + testName;
        csrfTokenStore.set(csrfSessionKey);
        File f = new File(CsrfTokenStore.HTTPD_SESSION_FILE);
        assertTrue("HTTPD_SESSION_FILE not created by set()", f.canRead());
        return csrfSessionKey;
    }

    /*
     *  =================
     *  UTILITY FUNCTIONS
     *  =================
     */
    public void deleteOauthFiles() {
        deleteFile(AccessData.ACCESS_TOKEN_FILE);
        deleteFile(CsrfTokenStore.HTTPD_SESSION_FILE);
        deleteFile(CsrfTokenStore.HTTPD_SESSION_FILE_EXPIRED);
        deleteFile(HttpConfig.LATEST_URL_FILE);
    }

    private static void deleteFile(String filepath) {
        File f = new File(filepath);
        if(f.exists()) { f.delete(); }
    }

    private PrintStream saveStdout = null;
    private ByteArrayOutputStream baos;

}

