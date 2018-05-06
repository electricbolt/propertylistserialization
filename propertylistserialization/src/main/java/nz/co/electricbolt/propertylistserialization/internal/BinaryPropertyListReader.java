/*******************************************************************************
 * BinaryPropertyListReader.java                                               *
 * propertylistserialization Copyright (c) 2018; Electric Bolt Limited.        *
 ******************************************************************************/

package nz.co.electricbolt.propertylistserialization.internal;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements a subset of Apple property list (plist) parser - binary format version "bplist00" only.
 * <p>
 * Property list elements are parsed as follows:
 * string (NSString) -&gt; java.lang.String
 * integer (NSInteger) -&gt; java.lang.Long
 * real (double) -&gt; java.lang.Double
 * dict (NSDictionary) -&gt; java.util.HashMap&lt;String, Object&gt;
 * array (NSArray) -&gt; java.util.ArrayList
 * date (NSDate) -&gt; java.util.Date
 * true (BOOL) -&gt; Boolean.valueOf(true)
 * false (BOOL) -&gt; Boolean.valueOf(false)
 * data (NSData) -&gt; byte[]
 * </p>
 */


public class BinaryPropertyListReader {

    private byte objectRefSize;
    private int[] offsetTable;
    private byte[] buf;

    public BinaryPropertyListReader(byte[] buf) {
        this.buf = buf;
    }

    public Object parse() throws IOException, UnsupportedOperationException, ParseException {
        // CFBinaryPlistHeader
        if (!(new String(buf, 0, 8).equals("bplist00")))
            throw new UnsupportedOperationException("File is not binary plist or supported version");

        // CFBinaryPlistTrailer
        int offsetIntSize = readByte(buf.length - 32 + 6);
        objectRefSize = readByte(buf.length - 32 + 7);
        int numObjects = (int) readLong(buf.length - 32 + 8);
        int rootObjectId = (int) readLong(buf.length - 32 + 16);
        int offsetTableOffset = (int) readLong(buf.length - 32 + 24);

        // Offset table
        offsetTable = new int[numObjects];
        for (int i = 0; i < numObjects; i++)
            offsetTable[i] = (int) readLong((offsetIntSize * i) + offsetTableOffset, offsetIntSize);

        return readObject(rootObjectId);
    }

    private Object readObject(int objectId) throws UnsupportedOperationException, UnsupportedEncodingException, ParseException {
        int offset = offsetTable[objectId];
        int objectType = (buf[offset] & 0xF0) >> 4; // high nibble
        int objectInfo = buf[offset] & 0x0F; // low nibble
        switch (objectType) {
            case 0x0: {
                switch (objectInfo) {
                    case 0x8: // boolean false
                        return false;
                    case 0x9: // boolean true
                        return true;
                    default:
                        throw new UnsupportedOperationException("Unsupported objectInfo " + objectInfo);
                }
            }
            case 0x1: {
                // integer
                return (long) readLong(offset + 1, (int) Math.pow(2, objectInfo));
            }
            case 0x2: {
                // real
                int size = (int) Math.pow(2, objectInfo);
                if (size == 4)
                    return Float.intBitsToFloat((int) readLong(offset + 1, 4));
                else if (size == 8)
                    return Double.longBitsToDouble(readLong(offset + 1, 8));
                else
                    throw new UnsupportedOperationException("Unsupported real size");
            }
            case 0x3: {
                // date
                if (objectInfo != 0x3)
                    throw new UnsupportedOperationException("Unsupported date format" + objectInfo);
                double millisSinceEpoch = Double.longBitsToDouble(readLong(offset + 1, 8));
                return DateUtil.parseBinary(millisSinceEpoch);
            }
            case 0x4: {
                // data
                LengthOffset lo = readLengthOffset(offset, objectInfo);
                byte[] data = new byte[lo.length];
                System.arraycopy(buf, lo.offset, data, 0, lo.length);
                return data;
            }
            case 0x5: {
                // ascii string
                LengthOffset lo = readLengthOffset(offset, objectInfo);
                return new String(buf, lo.offset, lo.length, "ascii");
            }
            case 0x6: {
                // utf16 string
                LengthOffset lo = readLengthOffset(offset, objectInfo);
                return new String(buf, lo.offset, lo.length*2, "utf-16be"); // 2 bytes per character
            }
            case 0xA: {
                // array
                LengthOffset lo = readLengthOffset(offset, objectInfo);
                List<Object> array = new ArrayList<>();
                for (int i = 0; i < lo.length; i++) {
                    int arrayObjectId = (int) readLong(lo.offset + (i * objectRefSize), objectRefSize);
                    array.add(readObject(arrayObjectId));
                }
                return array;
            }
            case 0xD: {
                // dict
                LengthOffset lo = readLengthOffset(offset, objectInfo);
                Map<String, Object> dict = new HashMap<String, Object>();
                for (int i = 0; i < lo.length; i++) {
                    int keyObjectId = (int) readLong(lo.offset + (i * objectRefSize), objectRefSize);
                    int valueObjectId = (int) readLong(lo.offset + (i * objectRefSize) + (lo.length * objectRefSize), objectRefSize);
                    dict.put((String) readObject(keyObjectId), readObject(valueObjectId));
                }
                return dict;
            }
            default:
                throw new UnsupportedOperationException("Unsupported plist objectType " + (objectType));
        }
    }

    private byte readByte(int offset) {
        return (byte) readLong(offset, 1);
    }

    private long readLong(int offset) {
        return (long) readLong(offset, 8);
    }

    /**
     * Reads 'length' bytes in host order from the buf and converts to a long.
     *
     * @param offset offset into the buf to read from.
     * @param length count of bytes to read from the buf.
     * @return long value.
     */
    private long readLong(int offset, int length) {
        long value = 0;
        for (int i = 0; i < length; i++) {
            value <<= 8;
            value |= (buf[offset + i] & 0xFF);
        }
        return value;
    }

    private static class LengthOffset {
        int length;
        int offset;
    }

    private LengthOffset readLengthOffset(int offset, int objectInfo) {
        LengthOffset result = new LengthOffset();
        if (objectInfo == 0xF) {
            // Length values >= 15 are stored in the bytes following.
            int intType = (buf[offset+1] & 0xF0) >> 4; // high nibble
            int intInfo = buf[offset+1] & 0x0F; // low nibble
            int size = (int) Math.pow(2, intInfo);
            result.offset = offset + 2 + size;
            result.length = (int) readLong(offset + 2, size);
        } else {
            // Length values 0..14 are stored directly in the low nibble.
            result.offset = offset + 1;
            result.length = objectInfo;
        }
        return result;
    }

}
