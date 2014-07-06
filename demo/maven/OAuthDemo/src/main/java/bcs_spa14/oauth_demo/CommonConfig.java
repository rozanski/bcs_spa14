package oauth_demo;

import java.io.File;

/**
 * Common configuration data (file paths)
 *
 */
public class CommonConfig {

    /**
     * Pathname of demo directory (the directory containing this file).
     *
     * <p>You should not need to change this (although you can if you want files to be stored elsewhere).
     */
    public static final String DEMO_DIRECTORY = (new File(System.getProperty("user.dir"))).getParent();

    /**
     * Pathname of files directory (which stores the access token file and session file)
     *
     */
    public static final String FILES_DIRECTORY = DEMO_DIRECTORY + File.separator + "files";

    /**
     * Pathname of doc directory (which stores the documentation)
     *
     */
    public static final String DOC_DIRECTORY = DEMO_DIRECTORY + File.separator + "doc";

}

