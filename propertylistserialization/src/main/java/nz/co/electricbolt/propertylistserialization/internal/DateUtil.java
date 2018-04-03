/*******************************************************************************
 * DateUtil.java                                                               *
 * propertylistserialization Copyright (c) 2018; Electric Bolt Limited.        *
 ******************************************************************************/

package nz.co.electricbolt.propertylistserialization.internal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Thread safe date formatter for parsing ISO8601 xml plist and binary plist dates.
 */
public class DateUtil {

    private static ThreadLocal<SimpleDateFormat> plistDateFormat = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            return df;
        }
    };

    private static Date parse(String s) throws ParseException {
        return plistDateFormat.get().parse(s);
    }

    /**
     * Binary plist dates have an epoch of 01 January 2001.
     *
     * @param d date in milliseconds from epoch
     * @return Date object
     * @throws ParseException if the date couldn't be converted.
     */

    public static Date parseBinary(double d) throws ParseException {
        Date binaryPlistEpoch = parse("2001-01-01T00:00:00Z");
        return new Date(binaryPlistEpoch.getTime() + (long) (d * 1000.0));
    }

    public static Date parseXML(String s) throws ParseException {
        return plistDateFormat.get().parse(s);
    }

    public static String formatXML(Date d) {
        return plistDateFormat.get().format(d);
    }

    public static double formatBinary(Date d) throws ParseException {
        Date binaryPlistEpoch = parse("2001-01-01T00:00:00Z");
        return (d.getTime() - binaryPlistEpoch.getTime()) / 1000.0;
    }

}

