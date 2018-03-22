package nz.co.electricbolt.propertylistserialization.internal;

import android.util.Base64;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Property list elements are written as follows:
 * java.lang.String ->  string (NSString)
 * java.lang.Integer -> integer (NSInteger)
 * java.lang.Float -> real (float)
 * java.lang.Double -> real (double)
 * java.util.HashMap<String, Object> -> dict (NSDictionary)
 * java.util.ArrayList -> array (NSArray)
 * java.util.Date -> date (NSDate)
 * Boolean.valueOf(true) -> true (BOOL)
 * Boolean.valueOf(false) -> false (BOOL)
 * byte[] -> data (NSData)
 */

public class XMLPropertyListWriter {

    private Object obj;
    private OutputStream os;

    public XMLPropertyListWriter(Object obj, OutputStream os) {
        this.obj = obj;
        this.os = os;
    }

    public void write() throws IOException, IllegalStateException {
        write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE plist PUBLIC \"-//Apple//DTD " +
            "PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n<plist version=\"1.0\">\n", 0);
        writeObject(obj, 0);
        write("</plist>\n", 0);
    }

    @SuppressWarnings("unchecked")
    private void writeObject(Object obj, int indent) throws IOException, IllegalStateException {
        if (obj == null)
            throw new IllegalStateException("Object cannot be null");
        if (obj instanceof Map) {
            Map dict = (Map) obj;
            if (dict.size() == 0)
                write("<dict/>\n", indent);
            else {
                write("<dict>\n", indent);
                // CFPropertyList.c sorts by key before outputting dictionaries
                TreeMap sortedDict = new TreeMap(dict);
                for (Object key : sortedDict.keySet()) {
                    write("<key>" + escape((String) key) + "</key>\n", indent + 1);
                    Object value = sortedDict.get(key);
                    writeObject(value, indent + 1);
                }
                write("</dict>\n", indent);
            }
        } else if (obj instanceof List) {
            List list = (List) obj;
            if (list.size() == 0)
                write("<array/>\n", indent);
            else {
                write("<array>\n", indent);
                for (int i = 0; i < list.size(); i++) {
                    Object value = list.get(i);
                    writeObject(value, indent + 1);
                }
                write("</array>\n", indent);
            }
        } else if (obj instanceof String) {
            String value = (String) obj;
            value = escape(value);
            write("<string>" + value + "</string>\n", indent);
        } else if (obj instanceof Float) {
            Float value = (Float) obj;
            String s = Float.toString(value);
            // Remove .0 at end of string to match output of CFPropertylist.c
            if (s.endsWith(".0"))
                s = s.substring(0, s.length() - 2);
            write("<real>" + s + "</real>\n", indent);
        } else if (obj instanceof Double) {
            Double value = (Double) obj;
            String s = Double.toString(value);
            // Remove .0 at end of string to match output of CFPropertylist.c
            if (s.endsWith(".0"))
                s = s.substring(0, s.length() - 2);
            write("<real>" + s + "</real>\n", indent);
        } else if (obj instanceof Integer) {
            Integer value = (Integer) obj;
            write("<integer>" + value + "</integer>\n", indent);
        } else if (obj instanceof byte[]) {
            writeData((byte[]) obj, indent);
        } else if (obj instanceof Date) {
            Date value = (Date) obj;
            write("<date>" + ISO8601DateFormatter.format(value) + "</date>\n", indent);
        } else if (obj instanceof Boolean) {
            Boolean value = (Boolean) obj;
            if (value)
                write("<true/>\n", indent);
            else
                write("<false/>\n", indent);
        } else {
            throw new IllegalStateException("Incompatible object " + obj + " found");
        }
    }

    private String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    private byte[] tab(int indent) {
        byte[] buf = new byte[indent];
        Arrays.fill(buf, (byte) '\t');
        return buf;
    }

    private void write(String s, int indent) throws IOException {
        os.write(tab(indent > 8 ? 8 : indent));
        os.write(s.getBytes("utf8"));
    }

    private void writeData(byte[] value, int indent) throws IOException {
        if (indent > 8)
            indent = 8;
        int lineLength = 76 - (indent * 8); // assume tab is 8 characters.
        byte[] tabBuf = tab(indent);
        byte[] encodedBuf = Base64.encode(value, Base64.NO_WRAP);

        os.write(tabBuf);
        os.write("<data>\n".getBytes("utf8"));

        int i = 0;
        for (int l = 0; l < (encodedBuf.length / lineLength); l++) {
            os.write(tabBuf);
            os.write(encodedBuf, i, lineLength);
            os.write('\n');
            i += lineLength;
        }
        if (i < encodedBuf.length) {
            os.write(tabBuf);
            os.write(encodedBuf, i, encodedBuf.length - i);
            os.write('\n');
        }

        os.write(tabBuf);
        os.write("</data>\n".getBytes("utf8"));
    }

}
