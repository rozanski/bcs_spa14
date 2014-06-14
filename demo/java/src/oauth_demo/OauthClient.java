package oauth_demo;

import java.io.*;
import java.net.*;
// import java.awt.Desktop;

import com.dropbox.core.DbxException;

/**
 * This is the class which runs the Oauth demo client.
 *
 * <p>It checks whether the access token file exists, and presents the user with a menu of relevant options.
 *
 * <p>If the client has previously been authorised with Dropbox (ie the Dropbox token file exists),
 * the options allow the user to manipulate files in the Dropbox app directory using Dropbox calls
 * (not using filesystem I/O).
 *
 * <p>If the client has not yet been authorised with Dropbox (ie the Dropbox token file does not exist),
 * the options allow the user to run either the no-redirect or redirect workflows.
 *
 */
public class OauthClient {

    public static void main(String[] args) {

        // run in debug mode if debug has been included in the command line
        for (String arg: args) {
            if (arg.toLowerCase().equals("debug")) {
                ConsoleLogger.enableDebug(true);
            }
        }
        outerloop:
        while(true) {
            if (AccessData.accessTokenFileExists()) {
                System.out.println("\nThe client is authorised with Dropbox (Dropbox token file exists)");
                System.out.println(" e. Run accessTokenFileExists() (should return true)");
                System.out.println(" d. List contents of directory");
                System.out.println(" p. Print file");
                System.out.println(" c. Create text file");
                System.out.println(" t. Print access token file");
                System.out.println(" z. Remove access token file (re-authorisation will be required)");
                System.out.println(" x. EXIT");
            }
            else {
                System.out.println("\nThe client is not yet authorised with Dropbox (the Dropbox token file does not exist)");
                // System.out.println("d. Enable / disable printing of log messages");
                System.out.println(" e. Run accessTokenFileExists() (should return False)");
                System.out.println(" n. run noredirect authorisation workflow (requires auth code to be entered)");
                System.out.println(" r. run redirect authorisation workflow (uses HTTP web server to simulate URL schema)");
                System.out.println(" x. EXIT");
            }

            try {
                char option = ' ';
                try {
                    option = readLine("Select an option").charAt(0);
                }
                catch (IndexOutOfBoundsException e) { }
                switch(option) {
                    case 'c':
                        String createpath = readDropboxPath("Enter Dropbox file path using forward-slashes");
                        DropboxTools.createFile(createpath);
                        break;
                    case 'd':
                        String directory = readDropboxPath("Enter Dropbox directory path using forward-slashes", "/");
                        DropboxTools.printDirectoryContents(directory);
                        break;
                    case 'e':
                        System.out.println("\n  accessTokenFileExists() returned " +
                                (AccessData.accessTokenFileExists() ? "TRUE" : "FALSE"));
                        break;
                    case 'n':
                        oauthAuthoriseNoRedirect();
                        break;
                    case 'p':
                        String printpath = readDropboxPath("Enter Dropbox file path using forward-slashes");
                        DropboxTools.printFile(printpath);
                        break;
                    case 'r':
                        oauthAuthoriseRedirect();
                        break;
                    case 't':
                        AccessData.printAccessTokenFile();
                        break;
                    case 'z':
                        AccessData.deleteAccessTokenFile();
                        break;
                    case 'x':
                        break outerloop;
                }
            }
            catch(Exception e) {
                ConsoleLogger.debug("FAILED, message is %s", e.getMessage());
                e.printStackTrace();
            }

        }
    }

    /**
     * Run the Oauth no-redirect workflow.
     *
     * <p>This consists of the following steps:
     * <ol>
     *    <li>Delete the access token file if it exists.
     *    <li>Start the workflow by calling {@code DropboxWorkflowNoRedirect.noRedirectClientStart()}.
     *    <li>Open a browser window at the URL returned by {@code noRedirectClientStart()}.
     *    <li>Prompt the user to enter the authorisation code displayed by Dropbox in this window.
     *    <li>Finish the workflow, save the access token and generate the sample files
     *    by calling {@code DropboxWorkflowNoRedirect.noRedirectClientFinishAndSave()}.
     * </ol>
     *
     * @throws IOException if there is an error saving the access token file
     * @throws URISyntaxException if there is an error opening the start URL
     * @throws DbxException if a Dropbox error occurs
     *
     * @see DropboxWorkflowNoRedirect
     */
    public static void oauthAuthoriseNoRedirect() throws IOException, URISyntaxException, DbxException {
        AccessData.deleteAccessTokenFile();
        DropboxStatus dropboxStatus = DropboxWorkflowNoRedirect.noRedirectClientStart();

        printWorkflowPrompts( "4. Copy the provided authorisation code to the clipboard");
        readLine("Press Enter to start no-redirect authorisation");

        Browser.openBrowserWindow(dropboxStatus.redirectUrl.toString());
        String securityCode = readLine("Enter the Dropbox security code");
        AccessData accessData = DropboxWorkflowNoRedirect.noRedirectClientFinishAndSave(securityCode);
        System.out.println("\nNO-REDIRECT AUTHORISATION COMPLETED SUCCESSFULLY\n");
    }

    /**
     * Start the Oauth no-redirect workflow.
     *
     * <p>This consists of the following steps:
     * <ol>
     *    <li>Delete the access token file if it exists.
     *    <li>Start the workflow by calling {@code DropboxWorkflowRedirect.redirectClientStart()}.
     *    <li>Open a browser window at the URL returned by {@code redirectClientStart()}.
     *    <li>Wait for the access token file to be created by the HTTP server (which is running the
     *    finish step of the workflow).
     * </ol>
     *
     * @throws IOException if there is an error saving the access token file
     * @throws URISyntaxException if there is an error opening the start URL
     *
     * @see DropboxWorkflowNoRedirect
     */
    public static void oauthAuthoriseRedirect() throws IOException, URISyntaxException {
        AccessData.deleteAccessTokenFile();
        DropboxStatus dropboxStatus = DropboxWorkflowRedirect.redirectClientStart();

        printWorkflowPrompts("");
        readLine("Ensure the HTTP server is running and press enter to start redirect authorisation");

        Browser.openBrowserWindow(dropboxStatus.redirectUrl.toString());
        if (AccessData.waitForAccessTokenFile()) {
            AccessData accessData = new AccessData();
            accessData.load();
            System.out.println("\nREDIRECT AUTHORISATION COMPLETED SUCCESSFULLY\n");
        }
    }

    /** print prompts for the user telling them what to do next */
    private static void printWorkflowPrompts(String additionalInstructions) {
        System.out.println("When the browser window opens:");
        System.out.println("1. Log in to Dropbox if necessary (your user name and password are NOT visible to the client)");
        System.out.println(
                "2. If you have enabled two-factor authentication with Dropbox," +
                "enter the Dropbox security code which will have been sent to your mobile phone" +
                "(this is not part of the OAuth workflow");
        System.out.println("3. Click 'Allow' to authorise the client with Dropbox");
        if (additionalInstructions.length() > 0) { System.out.println(additionalInstructions); }
    }

    /** read input from the console */
    private static String readLine(String prompt) {
        System.out.println(prompt + ": ");
        return System.console().readLine();
    }

    /** read a Dropbox path from the console */
    private static String readDropboxPath(String prompt) {
        return fixDropboxPath(readLine(prompt));
    }
    /** read a Dropbox path from the console */
    private static String readDropboxPath(String prompt, String defaultPath) {
        return fixDropboxPath(readLine(prompt + " (default='" + defaultPath + "')"));
    }
    /** read a Dropbox path from the console */
    private static String fixDropboxPath(String dropboxPath) {
        if (!dropboxPath.startsWith("/")) {
            dropboxPath = "/" + dropboxPath;
        }
        if ((dropboxPath.length() > 1) && (dropboxPath.endsWith("/"))) {
            dropboxPath = dropboxPath.substring(0,dropboxPath.length()-1);
        }
        return dropboxPath;
    }

}
