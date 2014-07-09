package oauth_demo;

import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import org.json.simple.*;

import com.dropbox.core.*;

/**
 * Store session data in a file for use between steps of the redirect authorisation workflow.
 *
 * <p>The first step takes place in the client, and the second step is done in the HTTP server.
 * A session token must be persisted between the steps in order to prevent CSRF
 * (cross-site request forgery) exploits. The session token ensures that the redirect to the
 * Dropbox website has come from a legitimate authorisation page.
 *
 * @see <a href='http://dropbox.github.io/dropbox-sdk-java/api-docs/v1.7.x/com/dropbox/core/DbxSessionStore.html'>DbxSessionStore</a>
 *
 */
public class CsrfTokenStore implements DbxSessionStore {

    /**
     * Full pathname of the JSON file in which the session token is stored.
     */
    public static String HTTPD_SESSION_FILE = String.format("%s%shttpd_session.json", CommonConfig.FILES_DIRECTORY, File.separator);

    /**
     * Once the session token is cleared, the session file is renamed to this pathname.
     */
    public static String HTTPD_SESSION_FILE_EXPIRED = String.format("%s%shttpd_session.EXPIRED.json", CommonConfig.FILES_DIRECTORY, File.separator);

    public CsrfTokenStore() {
        ConsoleLogger.debug("created token store object, session file = '%s'", HTTPD_SESSION_FILE);
    }

    /**
     * {@inheritDoc}
     *
     */
    public void clear() {
        File sessionFile = new File(HTTPD_SESSION_FILE);
        if (sessionFile.exists()) {
            File sessionFileExpired = new File(HTTPD_SESSION_FILE_EXPIRED);
            if(sessionFileExpired.delete()) {
                ConsoleLogger.debug("deleted expired session file %s", HTTPD_SESSION_FILE_EXPIRED);
            }
            else {
                ConsoleLogger.debug("warning: failed to delete expired session file %s", HTTPD_SESSION_FILE_EXPIRED);
                return; // no way to return an error in this interface
            }
            if (sessionFile.renameTo(sessionFileExpired)) {
                ConsoleLogger.debug("renamed session file '%s' to '%s'", HTTPD_SESSION_FILE, HTTPD_SESSION_FILE_EXPIRED);
            }
            else {
                ConsoleLogger.error("failed to rename session file '%s'", HTTPD_SESSION_FILE);
                return; // no way to return an error in this interface
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     */
    public String get() {
        ConsoleLogger.debug("Attempting to load CSRF token from session file %s", HTTPD_SESSION_FILE);
        String jsonFileContents = "";
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(HTTPD_SESSION_FILE));
            jsonFileContents = new String(encoded, StandardCharsets.UTF_8);
            ConsoleLogger.debug("CSRF token successfully loaded from token file %s", HTTPD_SESSION_FILE);
        }
        catch (IOException e) {
            // depending on context, this may or may not be an error
            ConsoleLogger.info("possible error: failed to load CSRF token from token file %s, error='%s'", HTTPD_SESSION_FILE, e.getMessage());
            return null; // no way to return an error in this interface
        }
        // @see https://code.google.com/p/json-simple/wiki/DecodingExamples
        JSONObject jsonObject = (JSONObject) JSONValue.parse(jsonFileContents);
        String CsrfToken = (String) jsonObject.get(HttpConfig.CSRF_SESSION_KEY);
        ConsoleLogger.debug("CSRF token is %s", CsrfToken);
        return CsrfToken;
    }

    /**
     * {@inheritDoc}
     *
     */
    @SuppressWarnings("unchecked")
    public void set(String value) {
        ConsoleLogger.debug("Attempting to save CSRF token to session file %s", HTTPD_SESSION_FILE);
        JSONObject obj=new JSONObject();
        obj.put(HttpConfig.CSRF_SESSION_KEY,value);
        StringWriter stringWriter = new StringWriter();
        try {
            obj.writeJSONString(stringWriter);
            String jsonText = stringWriter.toString();
            jsonText = jsonText.replace("{", "{\n    ");
            jsonText = jsonText.replace(",", ",\n    ");
            jsonText = jsonText.replace("}", "\n}\n");
            ConsoleLogger.debug("About to save CSRF token to session file %s", HTTPD_SESSION_FILE);
            PrintWriter printWriter = new PrintWriter(HTTPD_SESSION_FILE);
            printWriter.println(jsonText);
            printWriter.close();
            ConsoleLogger.debug("CSRF token successfully saved to session file %s", HTTPD_SESSION_FILE);
        }
        catch (IOException e) {
            ConsoleLogger.info("failed to save CSRF token to token file %s, error='%s'", HTTPD_SESSION_FILE, e.getMessage());
            return; // no way to return an error in this interface
        }
    }

}

