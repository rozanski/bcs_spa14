package uk.org.rozanski.oauth_demo.testlib;

import java.io.*;
import static org.junit.Assert.*;

import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.Locale;
import java.util.UUID;

import com.dropbox.core.*;

// import uk.org.rozanski.oauth_demo.*;
import uk.org.rozanski.oauth_demo.lib.*;

public class CommonTestDropbox {

    public class DropboxFile {
        public String filepath;
        public String fileContentsString;
        public String[] fileContentsArray;
        public DropboxFile(String dropboxFilepath, String dropboxFileContents) {
            filepath = dropboxFilepath;
            fileContentsString = dropboxFileContents;
            fileContentsArray = fileContentsString.split("\\r?\\n");;
            }
        }

    public static String TEST_DROPBOX_FILEPATH = "/unittest.%s.%s.java.txt";

    public static AccessData createTestTokenFile(String testName) throws IOException {
        AccessData saveAccessData = new AccessData(CommonTest.TEST_ACCESS_TOKEN, CommonTest.TEST_USER_ID, "TEST " + testName);
        saveAccessData.save();
        return saveAccessData;
    }

    public DbxClient getDropboxClient() throws IOException, DbxException {
        AccessData accessData = new AccessData();
        accessData.load();
        DbxRequestConfig config = new DbxRequestConfig(AppData.APP_NAME_VERSION,  Locale.getDefault().toString());
        return new DbxClient(config, accessData.accessToken);
        }

    public DropboxFile createTestDropboxFile(int numberOfLines, String testName) throws IOException, DbxException {
        String fileContents = "";
        String tmpFilePath = File.createTempFile(AppData.APP_NAME, ".txt").getPath();
        String dropboxPath = String.format(TEST_DROPBOX_FILEPATH, testName, UUID.randomUUID());
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFilePath));
        for (int i=1; i<11; i++) {
            String line = String.format(dropboxPath + " line %d", i) + "\n";
            fileContents += line;
            bw.write(line);
        }
        bw.close();
        File f = new File(tmpFilePath);
        DbxClient client = getDropboxClient();
        client.uploadFile(dropboxPath, DbxWriteMode.force(), f.length(), new FileInputStream(f));
        f.delete();
        return new DropboxFile(String.format(dropboxPath, testName), fileContents);
    }

    public String getDropboxFileContents(String dropboxFilePath) throws IOException, DbxException {
        DbxClient client = getDropboxClient();
        ByteArrayOutputStream out = new ByteArrayOutputStream(10000);
        try {
            client = getDropboxClient();
            DbxEntry.File md = client.getFile(dropboxFilePath, null, out);
            return out.toString();
        }
        finally {
            out.close();
        }
    }

    public void deleteDropboxFile(String dropboxFilePath) throws IOException, DbxException {
        DbxClient client = getDropboxClient();
        client.delete(dropboxFilePath);
    }

    public boolean dropboxPathExists(String dropboxFilePath) throws IOException, DbxException {
        DbxClient client = getDropboxClient();
        DbxEntry entry = client.getMetadata(dropboxFilePath);
        return (entry != null);
    }

    private DropboxFile dropboxFile;

}

