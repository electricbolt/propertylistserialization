package nz.co.electricbolt.propertylistserialization.internal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Thread safe date formatter for parsing ISO8601 style property list dates.
 */
public class ISO8601DateFormatter {

    private static ThreadLocal<SimpleDateFormat> plistDateFormat = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            return df;
        }
    };

    public static Date parse(String s) throws ParseException {
        return plistDateFormat.get().parse(s);
    }

    public static String format(Date d) {
        return plistDateFormat.get().format(d);
    }

}

