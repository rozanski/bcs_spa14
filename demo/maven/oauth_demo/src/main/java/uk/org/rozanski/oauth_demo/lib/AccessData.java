package uk.org.rozanski.oauth_demo.lib;

import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import org.json.simple.*;

/**
 * Contains the Dropbox Oauth access token and related information.
 *
 * <p>The class includes methods to save the information to disk, and load
 * previously-saved access data.
 *
 * It also includes methods to check for the presence of the file on disk.
 *
 */
public class AccessData {

    /**
     * Dropbox app key and secret for the demo app.
     *
     * @see <a href="https://www.dropbox.com/developers/apps">Access Token</a>
     */
    public static final String ACCESS_TOKEN_FILE = String.format("%s%saccess_token.json", CommonConfig.FILES_DIRECTORY, File.separator);

    /** access data */
    public String accessToken = "";
    public String userId = "";
    public String saveMessage = "";
    public String creationTime = "";

    public AccessData(String accessToken, String userId, String saveMessage) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.saveMessage = saveMessage;
        ConsoleLogger.debug("Access token file is %s", AccessData.ACCESS_TOKEN_FILE);
    }
    public AccessData() {
        this.accessToken = "";
        this.userId = "";
        this.saveMessage = "";
        ConsoleLogger.debug("Access token file is %s", AccessData.ACCESS_TOKEN_FILE);
    }

    /**
     * Load access data from file into the object.
     *
     * @throws IOException if there is an error reading the file
     */
    public void load() throws IOException {
        ConsoleLogger.debug("Attempting to load access data from token file %s", AccessData.ACCESS_TOKEN_FILE);
        byte[] encoded = Files.readAllBytes(Paths.get(AccessData.ACCESS_TOKEN_FILE));
        String jsonFileContents = new String(encoded, StandardCharsets.UTF_8);
        ConsoleLogger.debug("Access data successfully loaded from token file %s", AccessData.ACCESS_TOKEN_FILE);
        // @see https://code.google.com/p/json-simple/wiki/DecodingExamples
        JSONObject jsonObject = (JSONObject) JSONValue.parse(jsonFileContents);
        this.accessToken = (String) jsonObject.get("access_token");
        this.userId      = (String) jsonObject.get("user_id");
        this.saveMessage = (String) jsonObject.get("message");
        this.creationTime = (String) jsonObject.get("creation_time");
        ConsoleLogger.debug("Access token is %s, user id is %s, message is %s", this.accessToken, this.userId, this.saveMessage);
    }


    /**
     * Save access data from the object to file.
     *
     * @throws IOException if there is an error saving the file
     *
     * @see <a href="http://stackoverflow.com/questions/197986/what-causes-javac-to-issue-the-uses-unchecked-or-unsafe-operations-warning">@SuppressWarnings</a>
     */
    @SuppressWarnings("unchecked")
    public void save() throws IOException {
        ConsoleLogger.debug("Attempting to save access data to token file %s", AccessData.ACCESS_TOKEN_FILE);
        JSONObject obj=new JSONObject();
        obj.put("access_token",this.accessToken);
        obj.put("user_id",this.userId);
        obj.put("message",this.saveMessage);
        obj.put("creation_time",TimeNow.timeNowString());
        StringWriter stringWriter = new StringWriter();
        obj.writeJSONString(stringWriter);
        String jsonText = stringWriter.toString();
        // do not put commas or braces in the JSON data
        jsonText = jsonText.replace("{", "{\n    ");
        jsonText = jsonText.replace(",", ",\n    ");
        jsonText = jsonText.replace("}", "\n}\n");
        // ConsoleLogger.debug("string representation of access data is %s", jsonText);
        ConsoleLogger.debug("About to save access data to token file %s", AccessData.ACCESS_TOKEN_FILE);
        PrintWriter printWriter = new PrintWriter(AccessData.ACCESS_TOKEN_FILE);
        printWriter.println(jsonText);
        printWriter.close();
        ConsoleLogger.debug("Access data successfully saved to token file %s", AccessData.ACCESS_TOKEN_FILE);
    }

    /**
     * Check if the access token file exists.
     *
     * @param silent if true, do not display debug message
     * @return true if the access token file exists
     */
    public static boolean accessTokenFileExists(boolean silent) {
        if (!silent) {
            ConsoleLogger.debug("Checking to see if token file %s exists", AccessData.ACCESS_TOKEN_FILE);
        }
        File f = new File(AccessData.ACCESS_TOKEN_FILE);
        boolean exists = f.exists();
        if (!silent) {
            if (exists) {
                ConsoleLogger.debug("Token file %s exists", AccessData.ACCESS_TOKEN_FILE);
            }
            else {
                ConsoleLogger.debug("Token file %s does not exist", AccessData.ACCESS_TOKEN_FILE);
            }
        }
        return exists;
    }
    /**
     * {@inheritDoc}
     *
     */
    public static boolean accessTokenFileExists() {
        return AccessData.accessTokenFileExists(true);
    }

    /**
     * Print the access token file.
     *
     * @throws IOException if there is an error reading the file
     */
    public static void printAccessTokenFile() throws IOException {
        if(AccessData.accessTokenFileExists(true)) {
            System.out.println(String.format("\nCONTENTS OF TOKEN FILE %s:", AccessData.ACCESS_TOKEN_FILE));
            BufferedReader br = new BufferedReader(new FileReader(AccessData.ACCESS_TOKEN_FILE));
            int lineCount = 1;
            String line = null;
            while ((line = br.readLine()) != null) {
                String printLine = String.format("%3d: %s", lineCount, line);
                System.out.println(printLine);
                lineCount++;
            }
            br.close();
        }
        else {
            System.out.println(String.format("token file %s does not exist", AccessData.ACCESS_TOKEN_FILE));
        }
    }

    /**
     * Delete the access token file if it exists.
     *
     * @throws IOException if there is an error deleting the file
     */
    public static void deleteAccessTokenFile() throws IOException {
        if(AccessData.accessTokenFileExists(true)) {
            File file = new File(AccessData.ACCESS_TOKEN_FILE);
            if(file.delete()){
                ConsoleLogger.info("deleted file %s", AccessData.ACCESS_TOKEN_FILE);
            }
            else {
                throw new IOException(String.format("failed to delete token file %s", AccessData.ACCESS_TOKEN_FILE));
            }
        }
    }

    /**
     * Wait indefinitely until the access token file exists.
     *
     * @param messageInterval how often to print waiting message
     *
     * @return true if file found, false if interrupted
     */
    public static boolean waitForAccessTokenFile(int messageInterval) {
        int SLEEP_TIME = 1; // second
        int timeOfLastCheck = TimeNow.timeNowInt();
        try {
            ConsoleLogger.info("waiting for access token file %s...", AccessData.ACCESS_TOKEN_FILE);
            while(true) {
                if(AccessData.accessTokenFileExists(true)) {
                    ConsoleLogger.info("access token file %s present", AccessData.ACCESS_TOKEN_FILE);
                    break;
                }
                else {
                    if((TimeNow.timeNowInt() - timeOfLastCheck) > messageInterval) {
                        ConsoleLogger.info("still waiting for access token file...");
                        timeOfLastCheck = TimeNow.timeNowInt();
                    }
                    Thread.sleep(1000 * SLEEP_TIME);
                }
            }
        }
        catch (InterruptedException e) {
            ConsoleLogger.error("access token file not found");
            return false;
        }
        return true;
    }
    /**
     * {@inheritDoc}
     *
     */
    public static boolean waitForAccessTokenFile() {
        return AccessData.waitForAccessTokenFile(5);
    }

}
