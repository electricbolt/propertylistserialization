/*******************************************************************************
 * BinaryPropertyListReaderTest.java                                           *
 * propertylistserialization Copyright (c) 2018; Electric Bolt Limited.        *
 ******************************************************************************/

package nz.co.electricbolt.propertylistserialization.internal;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class BinaryPropertyListReaderTest {

    private static byte[] bytes(String template) {
        int len = template.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(template.charAt(i), 16) << 4) + Character.digit(template.charAt(i + 1), 16));
        }
        return data;
    }

    private byte[] makeData(int len) {
        byte[] gen = new byte[len];
        int v = 0;
        for (int i = 0; i < len; i++) {
            gen[i] = (byte) v;
            v++;
            if (v == 256)
                v = 0;
        }
        return gen;
    }

    // Array

    @Test
    @SuppressWarnings("unchecked")
    public void testEmptyArray() throws Exception {
        String template = "62706c6973743030a0080000000000000101000000000000000100000000000000000000000000000009";

        BinaryPropertyListReader p = new BinaryPropertyListReader(bytes(template));
        Object obj = p.parse();
        assertNotNull(obj);
        assertTrue(obj instanceof ArrayList);
        ArrayList<Object> list = (ArrayList<Object>) obj;
        assertEquals(0, list.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFilledArray() throws Exception {
        String template = "62706c6973743030aa0102030405060708090a1000223fc0000023400400000000000009084500010203044f1014000102030405060708090a0b0c0d0e0f1011121333c1e9fc3af0e000005f101b54686520636f77206a756d706564206f7665722074686520646f676f101f0100010100540068006500200063006f00770020006a0075006d0070006500640020006f007600650072002000740068006500200064006f0067010201030813151a2324252b424b690000000000000101000000000000000b000000000000000000000000000000aa";

        BinaryPropertyListReader p = new BinaryPropertyListReader(bytes(template));
        Object obj = p.parse();
        assertNotNull(obj);
        assertTrue(obj instanceof ArrayList);
        ArrayList<Object> list = (ArrayList<Object>) obj;
        assertEquals(list.size(), 10);
        assertEquals(0, (long) list.get(0));
        assertEquals(1.5f, (float) list.get(1), 0.1);
        assertEquals(2.5d, (double) list.get(2), 0.1);
        assertTrue((boolean) list.get(3));
        assertFalse((boolean) list.get(4));
        assertArrayEquals(makeData(5), (byte[]) list.get(5));
        assertArrayEquals(makeData(20), (byte[]) list.get(6));
        assertEquals(DateUtil.parseXML("1890-06-25T06:45:13Z"), (Date) list.get(7));
        assertEquals("The cow jumped over the dog", (String) list.get(8));
        assertEquals("\u0100\u0101The cow jumped over the dog\u0102\u0103", (String) list.get(9));
    }

    // Dict

    @Test
    @SuppressWarnings("unchecked")
    public void testEmptyDict() throws Exception {
        String template = "62706c6973743030d0080000000000000101000000000000000100000000000000000000000000000009";

        BinaryPropertyListReader p = new BinaryPropertyListReader(bytes(template));
        Object obj = p.parse();
        assertNotNull(obj);
        assertTrue(obj instanceof Map);
        Map<String, Object> dict = (Map<String, Object>) obj;
        assertEquals(0, dict.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFilledDict() throws Exception {
        String template = "62706c6973743030da0102030405060708090a0b0c0d0e0f10111213145664617461323056646f75626c6553696e745566616c73655575746631365464617465547472756555666c6f61745564617461355561736369694f1014000102030405060708090a0b0c0d0e0f101112132340040000000000001000086f101f0100010100540068006500200063006f00770020006a0075006d0070006500640020006f007600650072002000740068006500200064006f00670102010333c1e9fc3af0e0000009223fc000004500010203045f101b54686520636f77206a756d706564206f7665722074686520646f67081d242b2f353b40454b51576e77797abbc4c5cad000000000000001010000000000000015000000000000000000000000000000ee";

        BinaryPropertyListReader p = new BinaryPropertyListReader(bytes(template));
        Object obj = p.parse();
        assertNotNull(obj);
        assertTrue(obj instanceof Map);
        Map<String, Object> dict = (Map<String, Object>) obj;
        assertEquals(10, dict.size());
        assertEquals(0, (long) dict.get("int"));
        assertEquals(1.5f, (float) dict.get("float"), 0.1);
        assertEquals(2.5d, (double) dict.get("double"), 0.1);
        assertTrue((boolean) dict.get("true"));
        assertFalse((boolean) dict.get("false"));
        assertArrayEquals(makeData(5), (byte[]) dict.get("data5"));
        assertArrayEquals(makeData(20), (byte[]) dict.get("data20"));
        assertEquals(DateUtil.parseXML("1890-06-25T06:45:13Z"), (Date) dict.get("date"));
        assertEquals("The cow jumped over the dog", (String) dict.get("ascii"));
        assertEquals("\u0100\u0101The cow jumped over the dog\u0102\u0103", (String) dict.get("utf16"));
    }

    // String

    @Test
    public void testAsciiString() throws Exception {
        testAsciiString("", "62706c697374303050080000000000000101000000000000000100000000000000000000000000000009");
        testAsciiString(" ", "62706c6973743030512008000000000000010100000000000000010000000000000000000000000000000a");
        testAsciiString("The dog jumped over the moon", "62706c69737430305f101c54686520646f67206a756d706564206f76657220746865206d6f6f6e080000000000000101000000000000000100000000000000000000000000000027");
    }

    private static void testAsciiString(String actual, String template) throws Exception {
        BinaryPropertyListReader p = new BinaryPropertyListReader(bytes(template));
        Object obj = p.parse();
        assertNotNull(obj);
        assertTrue(obj instanceof String);
        String s = (String) obj;
        assertEquals(actual, s);
    }

    @Test
    public void testUnicodeString() throws Exception {
        testUnicodeString("Ā", "62706c697374303061010008000000000000010100000000000000010000000000000000000000000000000b");
        testUnicodeString("Āā", "62706c6973743030620100010108000000000000010100000000000000010000000000000000000000000000000d");
        testUnicodeString("ĀāThe cow jumped over the dogĂă", "62706c69737430306f101f0100010100540068006500200063006f00770020006a0075006d0070006500640020006f007600650072002000740068006500200064006f006701020103080000000000000101000000000000000100000000000000000000000000000049");
    }

    private static void testUnicodeString(String actual, String template) throws Exception {
        BinaryPropertyListReader p = new BinaryPropertyListReader(bytes(template));
        Object obj = p.parse();
        assertNotNull(obj);
        assertTrue(obj instanceof String);
        String s = (String) obj;
        assertEquals(actual, s);
    }

    // Integer

    @Test
    public void testPlistWithInteger() throws Exception {
        // positive
        testInteger(0, "62706c6973743030100008000000000000010100000000000000010000000000000000000000000000000a");
        testInteger(1, "62706c6973743030100108000000000000010100000000000000010000000000000000000000000000000a");
        testInteger(126, "62706c6973743030107e08000000000000010100000000000000010000000000000000000000000000000a");
        testInteger(127, "62706c6973743030107f08000000000000010100000000000000010000000000000000000000000000000a");
        testInteger(128, "62706c6973743030108008000000000000010100000000000000010000000000000000000000000000000a");
        testInteger(254, "62706c697374303010fe08000000000000010100000000000000010000000000000000000000000000000a");
        testInteger(255, "62706c697374303010ff08000000000000010100000000000000010000000000000000000000000000000a");
        testInteger(256, "62706c697374303011010008000000000000010100000000000000010000000000000000000000000000000b");
        testInteger(32766, "62706c6973743030117ffe08000000000000010100000000000000010000000000000000000000000000000b");
        testInteger(32767, "62706c6973743030117fff08000000000000010100000000000000010000000000000000000000000000000b");
        testInteger(32768, "62706c697374303011800008000000000000010100000000000000010000000000000000000000000000000b");
        testInteger(65534, "62706c697374303011fffe08000000000000010100000000000000010000000000000000000000000000000b");
        testInteger(65535, "62706c697374303011ffff08000000000000010100000000000000010000000000000000000000000000000b");
        testInteger(65536, "62706c6973743030120001000008000000000000010100000000000000010000000000000000000000000000000d");
        testInteger(2147483646, "62706c6973743030127ffffffe08000000000000010100000000000000010000000000000000000000000000000d");
        testInteger(2147483647, "62706c6973743030127fffffff08000000000000010100000000000000010000000000000000000000000000000d");
        testInteger(2147483648L, "62706c6973743030128000000008000000000000010100000000000000010000000000000000000000000000000d");
        testInteger(9223372036854775806L, "62706c6973743030137ffffffffffffffe080000000000000101000000000000000100000000000000000000000000000011");
        testInteger(9223372036854775807L, "62706c6973743030137fffffffffffffff080000000000000101000000000000000100000000000000000000000000000011");

        // negative
        testInteger(-1, "62706c697374303013ffffffffffffffff080000000000000101000000000000000100000000000000000000000000000011");
        testInteger(-127, "62706c697374303013ffffffffffffff81080000000000000101000000000000000100000000000000000000000000000011");
        testInteger(-128, "62706c697374303013ffffffffffffff80080000000000000101000000000000000100000000000000000000000000000011");
        testInteger(-129, "62706c697374303013ffffffffffffff7f080000000000000101000000000000000100000000000000000000000000000011");
        testInteger(-255, "62706c697374303013ffffffffffffff01080000000000000101000000000000000100000000000000000000000000000011");
        testInteger(-256, "62706c697374303013ffffffffffffff00080000000000000101000000000000000100000000000000000000000000000011");
        testInteger(-257, "62706c697374303013fffffffffffffeff080000000000000101000000000000000100000000000000000000000000000011");
        testInteger(-32767, "62706c697374303013ffffffffffff8001080000000000000101000000000000000100000000000000000000000000000011");
        testInteger(-32768, "62706c697374303013ffffffffffff8000080000000000000101000000000000000100000000000000000000000000000011");
        testInteger(-32769, "62706c697374303013ffffffffffff7fff080000000000000101000000000000000100000000000000000000000000000011");
        testInteger(-65534, "62706c697374303013ffffffffffff0002080000000000000101000000000000000100000000000000000000000000000011");
        testInteger(-65535, "62706c697374303013ffffffffffff0001080000000000000101000000000000000100000000000000000000000000000011");
        testInteger(-65536, "62706c697374303013ffffffffffff0000080000000000000101000000000000000100000000000000000000000000000011");
        testInteger(-2147483647, "62706c697374303013ffffffff80000001080000000000000101000000000000000100000000000000000000000000000011");
        testInteger(-2147483648, "62706c697374303013ffffffff80000000080000000000000101000000000000000100000000000000000000000000000011");
        testInteger( -2147483649L, "62706c697374303013ffffffff7fffffff080000000000000101000000000000000100000000000000000000000000000011");
        testInteger( -9223372036854775807L, "62706c6973743030138000000000000001080000000000000101000000000000000100000000000000000000000000000011");
        testInteger( -9223372036854775808L, "62706c6973743030138000000000000000080000000000000101000000000000000100000000000000000000000000000011");
    }

    private void testInteger(long value, String template) throws Exception {
        BinaryPropertyListReader p = new BinaryPropertyListReader(bytes(template));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof Long);
        Long l = (Long) obj;
        assertEquals(value, l.longValue());
    }

    // Real

    @Test
    public void testPlistWithRealFloat() throws Exception {
        testFloat(0.0f, "62706c6973743030220000000008000000000000010100000000000000010000000000000000000000000000000d");
        testFloat(1.0f, "62706c6973743030223f80000008000000000000010100000000000000010000000000000000000000000000000d");
        testFloat(2.5f, "62706c6973743030224020000008000000000000010100000000000000010000000000000000000000000000000d");
        testFloat(987654321.12345f, "62706c6973743030224e6b79a308000000000000010100000000000000010000000000000000000000000000000d");
        testFloat(-1.0f, "62706c697374303022bf80000008000000000000010100000000000000010000000000000000000000000000000d");
        testFloat(-2.5f, "62706c697374303022c020000008000000000000010100000000000000010000000000000000000000000000000d");
        testFloat(-987654321.12345f, "62706c697374303022ce6b79a308000000000000010100000000000000010000000000000000000000000000000d");

        testDouble(0.0d, "62706c6973743030230000000000000000080000000000000101000000000000000100000000000000000000000000000011");
        testDouble(1.0d, "62706c6973743030233ff0000000000000080000000000000101000000000000000100000000000000000000000000000011");
        testDouble(2.5d, "62706c6973743030234004000000000000080000000000000101000000000000000100000000000000000000000000000011");
        testDouble(987654321.12345d, "62706c69737430302341cd6f34588fcd36080000000000000101000000000000000100000000000000000000000000000011");
        testDouble(-1.0d, "62706c697374303023bff0000000000000080000000000000101000000000000000100000000000000000000000000000011");
        testDouble(-2.5d, "62706c697374303023c004000000000000080000000000000101000000000000000100000000000000000000000000000011");
        testDouble(-987654321.12345d, "62706c697374303023c1cd6f34588fcd36080000000000000101000000000000000100000000000000000000000000000011");
    }

    private void testFloat(float value, String template) throws Exception {
        BinaryPropertyListReader p = new BinaryPropertyListReader(bytes(template));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof Float);
        Float f = (Float) obj;
        assertEquals(value, f.floatValue(), 0.1);
    }

    private void testDouble(double value, String template) throws Exception {
        BinaryPropertyListReader p = new BinaryPropertyListReader(bytes(template));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof Double);
        Double d = (Double) obj;
        assertEquals(value, d.doubleValue(), 0.1);
    }

    // True

    @Test
    public void testPlistWithTrue() throws Exception {
        String template =
                "62706c697374303009080000000000000101000000000000000100000000000000000000000000000009";

        BinaryPropertyListReader p = new BinaryPropertyListReader(bytes(template));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof Boolean);
        Boolean b = (Boolean) obj;
        assertEquals(true, b);
    }

    // False

    @Test
    public void testPlistWithFalse() throws Exception {
        String template = "62706c697374303008080000000000000101000000000000000100000000000000000000000000000009";

        BinaryPropertyListReader p = new BinaryPropertyListReader(bytes(template));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof Boolean);
        Boolean b = (Boolean) obj;
        assertEquals(false, b);
    }

    // Date

    @Test
    public void testPlistWithDate() throws Exception {
        testDate("1970-01-01T12:00:00Z", "62706c697374303033c1cd278fe0000000080000000000000101000000000000000100000000000000000000000000000011");
        testDate("1890-06-25T06:45:13Z", "62706c697374303033c1e9fc3af0e00000080000000000000101000000000000000100000000000000000000000000000011");
        testDate("2019-11-04T14:22:59Z", "62706c69737430303341c1b835e1800000080000000000000101000000000000000100000000000000000000000000000011");
    }

    private void testDate(String dateStr, String template) throws Exception {
        BinaryPropertyListReader p = new BinaryPropertyListReader(bytes(template));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof Date);
        Date d = (Date) obj;
        assertEquals(d, DateUtil.parseXML(dateStr));
    }

    // Data

    @Test
    public void testPlistWithData() throws Exception {
        testData(0, "62706c697374303040080000000000000101000000000000000100000000000000000000000000000009");
        testData(1, "62706c6973743030410008000000000000010100000000000000010000000000000000000000000000000a");
        testData(2, "62706c697374303042000108000000000000010100000000000000010000000000000000000000000000000b");
        testData(14, "62706c69737430304e000102030405060708090a0b0c0d080000000000000101000000000000000100000000000000000000000000000017");
        testData(15, "62706c69737430304f100f000102030405060708090a0b0c0d0e08000000000000010100000000000000010000000000000000000000000000001a");
        testData(16, "62706c69737430304f1010000102030405060708090a0b0c0d0e0f08000000000000010100000000000000010000000000000000000000000000001b");
        testData(100, "62706c69737430304f1064000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f202122232425262728292a2b2c2d2e2f303132333435363738393a3b3c3d3e3f404142434445464748494a4b4c4d4e4f505152535455565758595a5b5c5d5e5f6061626308000000000000010100000000000000010000000000000000000000000000006f");
        testData(1000, "62706c69737430304f1103e8000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f202122232425262728292a2b2c2d2e2f303132333435363738393a3b3c3d3e3f404142434445464748494a4b4c4d4e4f505152535455565758595a5b5c5d5e5f606162636465666768696a6b6c6d6e6f707172737475767778797a7b7c7d7e7f808182838485868788898a8b8c8d8e8f909192939495969798999a9b9c9d9e9fa0a1a2a3a4a5a6a7a8a9aaabacadaeafb0b1b2b3b4b5b6b7b8b9babbbcbdbebfc0c1c2c3c4c5c6c7c8c9cacbcccdcecfd0d1d2d3d4d5d6d7d8d9dadbdcdddedfe0e1e2e3e4e5e6e7e8e9eaebecedeeeff0f1f2f3f4f5f6f7f8f9fafbfcfdfeff000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f202122232425262728292a2b2c2d2e2f303132333435363738393a3b3c3d3e3f404142434445464748494a4b4c4d4e4f505152535455565758595a5b5c5d5e5f606162636465666768696a6b6c6d6e6f707172737475767778797a7b7c7d7e7f808182838485868788898a8b8c8d8e8f909192939495969798999a9b9c9d9e9fa0a1a2a3a4a5a6a7a8a9aaabacadaeafb0b1b2b3b4b5b6b7b8b9babbbcbdbebfc0c1c2c3c4c5c6c7c8c9cacbcccdcecfd0d1d2d3d4d5d6d7d8d9dadbdcdddedfe0e1e2e3e4e5e6e7e8e9eaebecedeeeff0f1f2f3f4f5f6f7f8f9fafbfcfdfeff000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f202122232425262728292a2b2c2d2e2f303132333435363738393a3b3c3d3e3f404142434445464748494a4b4c4d4e4f505152535455565758595a5b5c5d5e5f606162636465666768696a6b6c6d6e6f707172737475767778797a7b7c7d7e7f808182838485868788898a8b8c8d8e8f909192939495969798999a9b9c9d9e9fa0a1a2a3a4a5a6a7a8a9aaabacadaeafb0b1b2b3b4b5b6b7b8b9babbbcbdbebfc0c1c2c3c4c5c6c7c8c9cacbcccdcecfd0d1d2d3d4d5d6d7d8d9dadbdcdddedfe0e1e2e3e4e5e6e7e8e9eaebecedeeeff0f1f2f3f4f5f6f7f8f9fafbfcfdfeff000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f202122232425262728292a2b2c2d2e2f303132333435363738393a3b3c3d3e3f404142434445464748494a4b4c4d4e4f505152535455565758595a5b5c5d5e5f606162636465666768696a6b6c6d6e6f707172737475767778797a7b7c7d7e7f808182838485868788898a8b8c8d8e8f909192939495969798999a9b9c9d9e9fa0a1a2a3a4a5a6a7a8a9aaabacadaeafb0b1b2b3b4b5b6b7b8b9babbbcbdbebfc0c1c2c3c4c5c6c7c8c9cacbcccdcecfd0d1d2d3d4d5d6d7d8d9dadbdcdddedfe0e1e2e3e4e5e6e7000800000000000002010000000000000001000000000000000000000000000003f4");
    }

    private void testData(int len, String template) throws Exception {
        BinaryPropertyListReader p = new BinaryPropertyListReader(bytes(template));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof byte[]);
        byte[] d = (byte[]) obj;
        assertArrayEquals(makeData(len), d);
    }
}
