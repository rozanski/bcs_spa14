package uk.org.rozanski.oauth_demo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Return the current time as a string or integer.
 *
 */
public class TimeNow {

    /**
     * Return the current time as a string.
     *
     * @return current time as a string
     */
    public static String timeNowString() {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy H:mm:ss 'UTC'");
        return sdf.format(cal.getTime());
    }

    /**
     * Return the current time as an integer.
     *
     * @return the number of seconds since January 1, 1970, 00:00:00 GMT
     */
    public static int timeNowInt() {
        Date date = new Date();
        return (int) (date.getTime() / 1000);
    }

}

