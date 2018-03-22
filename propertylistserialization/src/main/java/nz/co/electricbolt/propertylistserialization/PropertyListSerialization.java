package nz.co.electricbolt.propertylistserialization;

import android.support.annotation.NonNull;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;

import nz.co.electricbolt.propertylistserialization.internal.XMLPropertyListReader;
import nz.co.electricbolt.propertylistserialization.internal.XMLPropertyListWriter;

public class PropertyListSerialization {

    /**
     * For the object graph provided, returns a property list as byte[].
     * Equivalent to iOS method `[NSPropertyList dataWithPropertyList:format:options:error]`
     * @param obj The object graph to write out as a property list. The object graph may only
     *            contain the following types: String, Integer, Float, Double, Map<String, Object>,
     *            List, Date, Boolean or byte[]
     * @return byte[] of the property list.
     * @throws PropertyListWriteStreamException if the object graph is incompatible.
     */
    public static @NonNull byte[] dataWithPropertyList(@NonNull Object obj) throws PropertyListWriteStreamException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        writePropertyList(obj, os);
        return os.toByteArray();
    }

    /**
     * For the object graph provided, writes the property list to the output stream.
     * Equivalent to iOS method `[NSPropertyList writePropertyList:toStream:format:options:error]`
     * @param obj The object graph to write out as a property list. The object graph may only
     *            contain the following types: String, Integer, Float, Double, Map<String, Object>,
     *            List, Date, Boolean or byte[]
     * @param os The output stream to write the property list to.
     * @throws PropertyListWriteStreamException if the object graph is incompatible.
     */
    public static void writePropertyList(@NonNull Object obj, @NonNull OutputStream os) throws PropertyListWriteStreamException {
        try {
            XMLPropertyListWriter p = new XMLPropertyListWriter(obj, os);
            p.write();
        } catch(NullPointerException npe) {
            // null objects are not compatible with plist.
            throw new PropertyListWriteStreamException(npe);
        } catch(IllegalStateException ise) {
            // Incompatible Object attempting to be output
            throw new PropertyListWriteStreamException(ise);
        } catch(IOException ioe) {
            // Error writing to output stream - unlikely to occur, utf8 encoding missing - unlikely to occur
            throw new PropertyListWriteStreamException(ioe);
        }
    }

    /**
     * Creates and returns a property list from the specified byte[].
     * Equivalent to iOS method `[NSPropertyList propertyListWithData:options:format:error]`
     * @param data byte[] of plist
     * @return Returns one of String, Integer, Double, HashMap<String, Object>, ArrayList, Date,
     * Boolean or byte[].
     * @throws  PropertyListReadStreamException if the plist is corrupt, values could not be
     * converted or the input stream is EOF.
     */
    public static @NonNull Object propertyListWithData(@NonNull byte[] data) throws PropertyListReadStreamException {
        return propertyListWithData(new ByteArrayInputStream(data));
    }

    /**
     * Creates and returns a property list by reading from the specified input stream.
     * Equivalent to iOS method `[NSPropertyList propertyListWithStream:options:format:error]`
     * @param is InputStream containing the plist
     * @return Returns one of String, Integer, Double, hMap<String, Object>, List, Date, Boolean
     * or byte[].
     * @throws PropertyListReadStreamException if the plist is corrupt, values could not be
     * converted or the input stream is EOF.
     */
    public static @NonNull Object propertyListWithData(@NonNull InputStream is) throws PropertyListReadStreamException {
        try {
            XMLPropertyListReader p = new XMLPropertyListReader(is);
            return p.parse();
        } catch(ParseException pe) {
            // Error converting String to integer, float value.
            throw new PropertyListReadStreamException(pe);
        } catch(IOException ioe) {
            // Error reading input stream
            throw new PropertyListReadStreamException(ioe);
        } catch(XmlPullParserException xppe) {
            // XML parsing error - unexpected tokens etc
            throw new PropertyListReadStreamException(xppe);
        }
    }

}
