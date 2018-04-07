/*******************************************************************************
 * BinaryPropertyListWriter.java                                               *
 * propertylistserialization Copyright (c) 2018; Electric Bolt Limited.        *
 ******************************************************************************/

package nz.co.electricbolt.propertylistserialization.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Property list elements are written as follows:
 * java.lang.String ->  string (NSString)
 * java.lang.Integer -> integer (NSInteger)
 * java.lang.Long -> integer (NSInteger)
 * java.lang.Float -> real (float)
 * java.lang.Double -> real (double)
 * java.util.HashMap<String, Object> -> dict (NSDictionary)
 * java.util.ArrayList -> array (NSArray)
 * java.util.Date -> date (NSDate)
 * Boolean.valueOf(true) -> true (BOOL)
 * Boolean.valueOf(false) -> false (BOOL)
 * byte[] -> data (NSData)
 */

public class BinaryPropertyListWriter {

    private Object rootObj;
    private Map<Object, Integer> objectIdMap;
    private byte objectRefSize;
    private ByteArrayOutputStream os;
    private CharsetEncoder asciiEncoder;
    private CharsetEncoder utf16Encoder;

    public BinaryPropertyListWriter(Object rootObj) {
        this.rootObj = rootObj;
        this.objectIdMap = new LinkedHashMap<>(); // maintains insertion order
        this.os = new ByteArrayOutputStream();
        this.asciiEncoder = Charset.forName("ascii").newEncoder();
        this.utf16Encoder = Charset.forName("utf-16be").newEncoder();
    }

    public byte[] write() throws IOException, IllegalStateException, ParseException {
        // CFBinaryPlistHeader
        os.write("bplist00".getBytes());

        // Assign objects unique id
        mapObject(rootObj);

        if (objectIdMap.size() < 256)
            objectRefSize = 1;
        else if (objectIdMap.size() < 65536)
            objectRefSize = 2;
        else
            objectRefSize = 4;

        int[] offsetTable = new int[objectIdMap.size()];

        // Write objects and save each byte offset into offsetTable
        for (Map.Entry<Object, Integer> entry : objectIdMap.entrySet()) {
            Object obj = entry.getKey();
            offsetTable[entry.getValue()] = os.size();

            if (obj instanceof Map) {
                Map dict = (Map) obj;
                writeLength(0xD, dict.size());
                Set<Map.Entry<String, Object>> de = dict.entrySet();
                for (Map.Entry<String, Object> e : de)
                    writeLong(objectIdMap.get(e.getKey()), objectRefSize);
                for (Map.Entry<String, Object> e : de)
                    writeLong(objectIdMap.get(e.getValue()), objectRefSize);
            } else if (obj instanceof List) {
                List array = (List) obj;
                writeLength(0xA, array.size());
                for (Object value : array)
                    writeLong(objectIdMap.get(value), objectRefSize);
            } else if (obj instanceof String) {
                CharBuffer charBuf = CharBuffer.wrap((String) obj);
                ByteBuffer byteBuf;
                int intType;
                if (asciiEncoder.canEncode(charBuf)) {
                    asciiEncoder.reset();
                    byteBuf = asciiEncoder.encode(charBuf);
                    intType = 0x5;
                } else {
                    utf16Encoder.reset();
                    byteBuf = utf16Encoder.encode(charBuf);
                    intType = 0x6;
                }
                byte[] buf = new byte[byteBuf.remaining()];
                byteBuf.get(buf);
                writeLength(intType, ((String) obj).length());
                os.write(buf);
            } else if (obj instanceof Float) {
                os.write(0x22);
                writeLong(Float.floatToRawIntBits((float) obj), 4);
            } else if (obj instanceof Double) {
                os.write(0x23);
                writeLong(Double.doubleToRawLongBits((double) obj), 8);
            } else if (obj instanceof Integer || obj instanceof Long) {
                long value;
                if (obj instanceof Integer)
                    value = (Integer) obj;
                else
                    value = (Long) obj;
                if (value < 0) {
                    // All negative integers are stored as long
                    os.write(0x13);
                    writeLong(value, 8);
                } else if (value < 256) {
                    // byte
                    os.write(0x10);
                    writeLong(value, 1);
                } else if (value < 65536) {
                    // short
                    os.write(0x11);
                    writeLong(value, 2);
                } else if (value < 4294967296L){
                    // int
                    os.write(0x12);
                    writeLong(value, 4);
                } else {
                    // long
                    os.write(0x13);
                    writeLong(value, 8);
                }
            } else if (obj instanceof Date) {
                os.write(0x33);
                double value = DateUtil.formatBinary((Date) obj);
                writeLong(Double.doubleToRawLongBits(value), 8);
            } else if (obj instanceof Boolean) {
                if (!((Boolean) obj))
                    os.write(0x08);
                else
                    os.write(0x09);
            } else if (obj instanceof byte[]) {
                byte[] buf = (byte[]) obj;
                writeLength(0x4, buf.length);
                os.write(buf);
            }
        }

        // Write offsetTable
        long offsetTableOffset = os.size();
        byte offsetIntSize = 4;
        if (os.size() < 256)
            offsetIntSize = 1;
        else if (os.size() < 65536)
            offsetIntSize = 2;
        for (int offset : offsetTable)
            writeLong(offset, offsetIntSize);

        // CFBinaryPlistTrailer
        os.write(new byte[6]);
        os.write(offsetIntSize);
        os.write(objectRefSize);
        writeLong(objectIdMap.size(), 8);
        writeLong(objectIdMap.get(rootObj), 8);
        writeLong(offsetTableOffset, 8);

        os.flush();
        return os.toByteArray();
    }

    /**
     * For each unique object, assigns an object id.
     */
    private void mapObject(Object obj) {
        if (!objectIdMap.containsKey(obj))
            objectIdMap.put(obj, objectIdMap.size());
        if (obj instanceof Map) {
            Map dict = (Map) obj;
            Set<Map.Entry<String, Object>> de = dict.entrySet();
            for (Map.Entry<String, Object> e : de)
                mapObject(e.getKey());
            for (Map.Entry<String, Object> e : de)
                mapObject(e.getValue());
        } else if (obj instanceof List) {
            List list = (List) obj;
            for (int i = 0; i < list.size(); i++)
                mapObject(list.get(i));
        } else if (obj instanceof String || obj instanceof Float || obj instanceof Double ||
                obj instanceof Integer || obj instanceof Long || obj instanceof byte[] ||
                obj instanceof Date || obj instanceof Boolean) {
            // do nothing.
        } else
            throw new IllegalStateException("Incompatible object " + obj + " found");
    }

    private void writeLong(long value, int length) throws IOException {
        for (int i = length - 1; i >= 0; i--)
            os.write((int) (value >> (8 * i)));
    }

    private void writeLength(int intType, int length) throws IOException {
        if (length < 15) {
            os.write((intType << 4) + length);
        } else if (length < 256) {
            os.write((intType << 4) + 0xF);
            os.write(0x10);
            writeLong(length, 1);
        } else if (length < 65536) {
            os.write((intType << 4) + 0xF);
            os.write(0x11);
            writeLong(length, 2);
        } else {
            os.write((intType << 4) + 0xF);
            os.write(0x12);
            writeLong(length, 4);
        }
    }

}