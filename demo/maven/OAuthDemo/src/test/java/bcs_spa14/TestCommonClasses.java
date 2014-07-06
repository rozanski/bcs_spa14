package oauth_demo;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.logging.Level;
import java.io.*;
import java.nio.file.*;
import java.net.*;
import java.awt.Desktop;

/**
 * This class implements unit tests for the CommonClasses class.
 *
 * Run unit tests from the command line as follows:
 *    mvn test                 # print INFO messages only
 *    mvn test -Ddebug='debug' # print INFO and DEBUG messages
 *
 */
public class TestCommonClasses {

    /*
     * =============
     * TEST FIXTURES
     * =============
     */
    @BeforeClass
        public static void setUpClass() {
        try {
            HttpdServer.startServer();
        }   
        catch (IOException e) {
            ConsoleLogger.info("fatal error: failed to start HTTP server, error='%s'", e.getMessage());
        }   
    }   

    @AfterClass
    public static void tearDownClass() throws IOException {
        // I don't think there is anything to do here
    }

    @Before
    public void setUp() {
        ConsoleLogger.setLevel((System.getProperty("debug") == null)?Level.INFO:Level.FINE);
        commonTest = new CommonTest();
        commonTest.deleteOauthFiles();
    }

    @After
    public void tearDown() {
        /* comment this out if you want to see what files have been created during the tests */
        commonTest.deleteOauthFiles();
    }

    /*
       ======================
       TESTS FOR CommonConfig
       ======================
    */
    @Test
    public void demoDirectoryWriteable() {
        File f = new File(CommonConfig.DEMO_DIRECTORY);
        assertTrue("DEMO_DIRECTORY not writeable", (f.exists() && f.isDirectory()));
    }

    @Test
    public void filesDirectoryWriteable() {
        File f = new File(CommonConfig.FILES_DIRECTORY);
        assertTrue("FILES_DIRECTORY not writeable", (f.exists() && f.isDirectory()));
    }

    /*
       =================
       TESTS FOR AppData
       =================
    */
    @Test
    public void checkAppData() {
        assertTrue("AppData is empty", AppData.APP_KEY.length() >0);
        assertTrue("AppData is empty", AppData.APP_SECRET.length() >0);
    }

    /*
       ====================
       TESTS FOR AccessData
       ====================
    */
    @Test
    public void accessDataSaveLoad() throws IOException {
        AccessData save_access_data = commonTest.createAndSaveAccessData("accessDataSaveLoad");
        AccessData load_access_data = new AccessData();
        load_access_data.load();
        CommonTest.assertAccessDataEqual(save_access_data, "saved", load_access_data, "loaded");
    }

    @Test
    public void accessTokenFileExists() throws IOException {
        AccessData save_access_data = commonTest.createAndSaveAccessData("accessTokenFileExists");
        assertTrue("accessTokenFileExists() does not think access data exists", AccessData.accessTokenFileExists(true));
        File f = new File(AccessData.ACCESS_TOKEN_FILE);
        f.delete();
        assertFalse("accessTokenFileExists() misreporting", AccessData.accessTokenFileExists(true));
    }

    @Test
    public void deleteAccessTokenFile() throws IOException {
        AccessData save_access_data = commonTest.createAndSaveAccessData("deleteAccessTokenFile");
        assertTrue("accessTokenFileExists() does not think access data exists", AccessData.accessTokenFileExists(true));
        AccessData.deleteAccessTokenFile();
        assertFalse("deleteAccessTokenFile() did not delete token file", AccessData.accessTokenFileExists(true));
    }

    /*
       ========================
       TESTS FOR CsrfTokenStore
       ========================
    */
    @Test
    public void sessionDataClear() {
        CommonTest.csrfTokenStore.clear();
        String csrfToken = CommonTest.csrfTokenStore.get();
        assertEquals("Session token '%s' not cleared", csrfToken, null);
    }

    @Test
    public void sessionDataSetGet() {
        String SESSION_TOKEN = "session-token";
        CommonTest.csrfTokenStore.set(SESSION_TOKEN);
        String csrfToken = CommonTest.csrfTokenStore.get();
        assertEquals("Loaded session token '%s' is not equal to saved session token '%s'", csrfToken, SESSION_TOKEN);
    }

    /*
       ====================
       TESTS FOR HttpConfig
       ====================
    */
    @Test
        public void httpConfigUrls() {
            CommonTest.assertUrlValid(HttpConfig.HOME_URL.toString()); // these are pointless...
            // CommonTest.assertUrlValid(HttpConfig.START_URL.toString());
            CommonTest.assertUrlValid(HttpConfig.FINISH_URL.toString());
        }

    @Test
    public void httpUrlReachable() throws MalformedURLException, IOException {
        CommonTest.assertUrlReachable(HttpConfig.HOME_URL.toString());
    }

    @Test
    public void openBrowserWindow() throws IOException, URISyntaxException  {
        int waitSeconds = 10;
        Browser.openBrowserWindow(HttpConfig.HOME_URL.toString());
        boolean pageRequested = HttpLatestUrlFile.waitForLatestUrlFile(waitSeconds);
        assertTrue(String.format("Failed to open browser window after %d seconds,ensure HTTP server is running!", 
                    waitSeconds), pageRequested);
    }

    private CommonTest commonTest;

}

