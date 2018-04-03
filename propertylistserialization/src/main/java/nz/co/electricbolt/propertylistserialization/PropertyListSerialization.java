/*******************************************************************************
 * PropertyListSerialization.java                                              *
 * propertylistserialization Copyright (c) 2018; Electric Bolt Limited.        *
 ******************************************************************************/

package nz.co.electricbolt.propertylistserialization;

import android.support.annotation.NonNull;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;

import nz.co.electricbolt.propertylistserialization.internal.BinaryPropertyListReader;
import nz.co.electricbolt.propertylistserialization.internal.BinaryPropertyListWriter;
import nz.co.electricbolt.propertylistserialization.internal.XMLPropertyListReader;
import nz.co.electricbolt.propertylistserialization.internal.XMLPropertyListWriter;

public class PropertyListSerialization {

    public static enum Format {
        XML,
        Binary
    }

    /**
     * For the object graph provided, returns a property list as byte[].
     * Equivalent to iOS method `[NSPropertyList dataWithPropertyList:format:options:error]`
     *
     * @param obj    The object graph to write out as a property list. The object graph may only
     *               contain the following types: String, Integer, Float, Double, Map<String, Object>,
     *               List, Date, Boolean or byte[]
     * @param format The format of the property list. Specify either XML or Binary.
     * @return byte[] of the property list.
     * @throws PropertyListWriteStreamException if the object graph is incompatible.
     */
    public static @NonNull
    byte[] dataWithPropertyList(@NonNull Object obj, Format format) throws PropertyListWriteStreamException {
        if (format == Format.Binary) {
            try {
                BinaryPropertyListWriter p = new BinaryPropertyListWriter(obj);
                return p.write();
            } catch (ParseException pe) {
                // Error converting String to integer, float value.
                throw new PropertyListWriteStreamException(pe);
            } catch (IOException ioe) {
                // Error writing to output stream - unlikely to occur, utf8 encoding missing - unlikely to occur
                throw new PropertyListWriteStreamException(ioe);
            }
        } else {
            // Format.XML
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            writePropertyList(obj, os, format);
            return os.toByteArray();
        }
    }

    /**
     * For the object graph provided, writes the property list to the output stream.
     * Equivalent to iOS method `[NSPropertyList writePropertyList:toStream:format:options:error]`
     *
     * @param obj    The object graph to write out as a property list. The object graph may only
     *               contain the following types: String, Integer, Float, Double, Map<String, Object>,
     *               List, Date, Boolean or byte[]
     * @param format The format of the property list. Specify either XML or Binary.
     * @param os     The output stream to write the property list to.
     * @throws PropertyListWriteStreamException if the object graph is incompatible.
     */
    public static void writePropertyList(@NonNull Object obj, @NonNull OutputStream os, Format format) throws PropertyListWriteStreamException {
        if (format == Format.XML) {
            try {
                XMLPropertyListWriter p = new XMLPropertyListWriter(obj, os);
                p.write();
            } catch (NullPointerException npe) {
                // null objects are not compatible with plist.
                throw new PropertyListWriteStreamException(npe);
            } catch (IllegalStateException ise) {
                // Incompatible Object attempting to be output
                throw new PropertyListWriteStreamException(ise);
            } catch (IOException ioe) {
                // Error writing to output stream - unlikely to occur, utf8 encoding missing - unlikely to occur
                throw new PropertyListWriteStreamException(ioe);
            }
        } else {
            // Format.Binary
            try {
                os.write(dataWithPropertyList(obj, Format.Binary));
            } catch (IOException ioe) {
                // Error writing to output stream - unlikely to occur, utf8 encoding missing - unlikely to occur
                throw new PropertyListWriteStreamException(ioe);
            }
        }
    }

    /**
     * Creates and returns a property list from the specified byte[].
     * Equivalent to iOS method `[NSPropertyList propertyListWithData:options:format:error]`
     *
     * @param data   byte[] of plist
     * @param format The format of the property list. Specify either XML or Binary.
     * @return Returns one of String, Integer, Double, HashMap<String, Object>, ArrayList, Date,
     * Boolean or byte[].
     * @throws PropertyListReadStreamException if the plist is corrupt, values could not be
     *                                         converted or the input stream is EOF.
     */
    public static @NonNull
    Object propertyListWithData(@NonNull byte[] data, Format format) throws PropertyListReadStreamException {
        if (format == Format.Binary) {
            try {
                BinaryPropertyListReader p = new BinaryPropertyListReader(data);
                return p.parse();
            } catch (UnsupportedOperationException uoe) {
                // Binary plist format contains features we don't support.
                throw new PropertyListReadStreamException(uoe);
            } catch (ParseException pe) {
                // Error converting String to integer, float value.
                throw new PropertyListReadStreamException(pe);
            } catch (IOException ioe) {
                // Error reading input stream
                throw new PropertyListReadStreamException(ioe);
            }
        } else {
            // Format.XML
            return propertyListWithData(new ByteArrayInputStream(data), format);
        }
    }

    /**
     * Creates and returns a property list by reading from the specified input stream.
     * Equivalent to iOS method `[NSPropertyList propertyListWithStream:options:format:error]`
     *
     * @param is     InputStream containing the plist
     * @param format The format of the property list. Specify either XML or Binary.
     * @return Returns one of String, Integer, Double, hMap<String, Object>, List, Date, Boolean
     * or byte[].
     * @throws PropertyListReadStreamException if the plist is corrupt, values could not be
     *                                         converted or the input stream is EOF.
     */
    public static @NonNull
    Object propertyListWithData(@NonNull InputStream is, Format format) throws PropertyListReadStreamException {
        if (format == Format.XML) {
            try {
                XMLPropertyListReader p = new XMLPropertyListReader(is);
                return p.parse();
            } catch (ParseException pe) {
                // Error converting String to integer, float value.
                throw new PropertyListReadStreamException(pe);
            } catch (IOException ioe) {
                // Error reading input stream
                throw new PropertyListReadStreamException(ioe);
            } catch (XmlPullParserException xppe) {
                // XML parsing error - unexpected tokens etc
                throw new PropertyListReadStreamException(xppe);
            }
        } else {
            // Format.Binary
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                int read;
                byte[] buf = new byte[1024];
                while ((read = is.read(buf, 0, 1024)) != -1) {
                    baos.write(buf, 0, read);
                }
                baos.flush();
                return propertyListWithData(baos.toByteArray(), Format.Binary);
            } catch (IOException ioe) {
                // Error reading input stream
                throw new PropertyListReadStreamException(ioe);
            }
        }
    }

}
