package oauth_demo;

import java.io.*;
import java.net.*;
import java.awt.Desktop;

/**
 * open a window in the default web browser
 */
public class Browser {

    /** open a window in the default web browser */
    public static void openBrowserWindow(String url) throws IOException, URISyntaxException {
        ConsoleLogger.debug("about to open url %s in default browser", url);
        // thanks to http://stackoverflow.com/questions/5226212/how-to-open-the-default-webbrowser-using-java
        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(url));
        }
        else {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("xdg-open " + url);
        }
    }
}

