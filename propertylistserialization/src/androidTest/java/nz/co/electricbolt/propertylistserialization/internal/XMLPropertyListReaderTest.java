/*******************************************************************************
 * XMLPropertyListReaderTest.java                                              *
 * propertylistserialization Copyright (c) 2018; Electric Bolt Limited.        *
 ******************************************************************************/

package nz.co.electricbolt.propertylistserialization.internal;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class XMLPropertyListReaderTest {

    // Plist

    @Test
    @SuppressWarnings("unchecked")
    public void emptyPlist() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        try {
            p.parse();
            // <!ENTITY % plistObject "(array | data | date | dict | real | integer | string | true | false )" >
            fail("Should have thrown an exception - plist requires array,data,date,dict,real,integer,string,true,false");
        } catch(Exception e) {
        }
    }

    // Array

    @Test
    @SuppressWarnings("unchecked")
    public void plistWithEmptyArray() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "\t<array>\n" +
                "\t</array>\n" +
                "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        Object obj = p.parse();
        assertNotNull(obj);
        assertTrue(obj instanceof ArrayList);
        ArrayList<Object> list = (ArrayList<Object>) obj;
        assertEquals(list.size(), 0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void plistWithEmptyArrayElement() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "\t<array/>\n" +
                "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        Object obj = p.parse();
        assertNotNull(obj);
        assertTrue(obj instanceof ArrayList);
        ArrayList<Object> list = (ArrayList<Object>) obj;
        assertEquals(list.size(), 0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void plistWithArray() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "\t<array>\n" +
                "\t\t<string>abc</string>\n" +
                "\t\t<string></string>\n" +
                "\t\t<string/>\n" +
                "\t\t<integer>1</integer>\n" +
                "\t\t<real>1.0</real>\n" +
                "\t\t<true/>\n" +
                "\t\t<false/>\n" +
                "\t</array>\n" +
                "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        Object obj = p.parse();
        assertNotNull(obj);
        assertTrue(obj instanceof ArrayList);
        ArrayList<Object> list = (ArrayList<Object>) obj;
        assertEquals(list.size(), 7);
        assertEquals(list.get(0), "abc");
        assertEquals(list.get(1), "");
        assertEquals(list.get(2), "");
        assertEquals(list.get(3), 1);
        assertEquals(list.get(4), (double) 1.0d);
        assertEquals(list.get(5), true);
        assertEquals(list.get(6), false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void plistWithInvalidArray() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "\t<array>\n" +
                "\t\t<key>fail</key>\n" + // will fail - key is not valid
                "\t</array>\n" +
                "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        try {
            p.parse();
            fail("Should have thrown an exception - key is not valid for an array");
        } catch(Exception e) {
        }
    }

    // Dict

    @Test
    @SuppressWarnings("unchecked")
    public void plistWithEmptyDict() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "\t<dict>\n" +
                "\t</dict>\n" +
                "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        Object obj = p.parse();
        assertNotNull(obj);
        assertTrue(obj instanceof HashMap);
        HashMap<String, Object> dict = (HashMap<String, Object>) obj;
        assertEquals(dict.size(), 0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void plistWithEmptyDictElement() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                        "<plist version=\"1.0\">\n" +
                        "\t<dict/>\n" +
                        "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        Object obj = p.parse();
        assertNotNull(obj);
        assertTrue(obj instanceof HashMap);
        HashMap<String, Object> dict = (HashMap<String, Object>) obj;
        assertEquals(dict.size(), 0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void plistWithDict() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<dict>\n" +
                "\t<key>Array</key>\n" +
                "\t<array>\n" +
                "\t\t<string>String1</string>\n" +
                "\t\t<string>String2</string>\n" +
                "\t</array>\n" +
                "\t<key>Dict</key>\n" +
                "\t<dict>\n" +
                "\t\t<key>DictKey1</key>\n" +
                "\t\t<string>String3</string>\n" +
                "\t</dict>\n" +
                "\t<key>String</key>\n" +
                "\t<string>String4</string>\n" +
                "\t<key>Data</key>\n" +
                "\t<data>U3RyaW5nNQ==</data>\n" + // String5
                "\t<key>Date</key>\n" +
                "\t<date>2018-03-17T15:53:00Z</date>\n" +
                "\t<key>Integer</key>\n" +
                "\t<integer>1</integer>\n" +
                "\t<key>Real</key>\n" +
                "\t<real>1.0</real>\n" +
                "\t<key>True</key>\n" +
                "\t<true/>\n" +
                "\t<key>False</key>\n" +
                "\t<false/>\n" +
                "</dict>\n" +
                "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof HashMap);
        HashMap<String, Object> dict = (HashMap<String, Object>) obj;
        assertEquals(dict.size(), 9);

        ArrayList list = (ArrayList) dict.get("Array");
        assertEquals(list.size(), 2);
        assertEquals(list.get(0), "String1");
        assertEquals(list.get(1), "String2");

        HashMap dict2 = (HashMap) dict.get("Dict");
        assertEquals(dict2.size(), 1);
        assertEquals(dict2.get("DictKey1"), "String3");

        assertEquals(dict.get("String"), "String4");

        assertEquals(new String((byte[]) dict.get("Data")), "String5");

        assertEquals(dict.get("Integer"), 1);

        assertEquals(dict.get("Real"), 1.0d);

        assertEquals(dict.get("True"), true);
        assertEquals(dict.get("False"), false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void plistWithInvalidDict() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "\t<dict>\n" +
                "\t\t<integer>1</integer>\n" + // will fail - integer is not valid, expecting key
                "\t</dict>\n" +
                "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        try {
            p.parse();
            fail("Should have thrown an exception - integer is not valid for a dict");
        } catch(Exception e) {
        }
    }

    // String

    @Test
    public void testPlistWithEmptyString() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<string></string>\n" +
                "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof String);
        String s = (String) obj;
        assertEquals(s, "");
    }

    @Test
    public void testPlistWithEmptyStringElement() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<string/>\n" +
                "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof String);
        String s = (String) obj;
        assertEquals(s, "");
    }

    @Test
    public void testPlistWithString() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<string>Some text in this string</string>\n" +
                "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof String);
        String s = (String) obj;
        assertEquals(s, "Some text in this string");
    }

    // Integer

    @Test
    public void testPlistWithInteger() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<integer>1</integer>\n" +
                "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof Integer);
        Integer i = (Integer) obj;
        assertEquals(i, Integer.valueOf(1));
    }

    // Real

    @Test
    public void testPlistWithReal() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<real>1</real>\n" +
                "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof Double);
        Double f = (Double) obj;
        assertEquals(f, Double.valueOf(1.0d));
    }

    // True

    @Test
    public void testPlistWithTrue() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<true></true>\n" +
                "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof Boolean);
        Boolean b = (Boolean) obj;
        assertEquals(b, true);
    }

    @Test
    public void testPlistWithTrueElement() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<true/>\n" +
                "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof Boolean);
        Boolean b = (Boolean) obj;
        assertEquals(b, true);
    }

    // False

    @Test
    public void testPlistWithFalse() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<false></false>\n" +
                "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof Boolean);
        Boolean b = (Boolean) obj;
        assertEquals(b, false);
    }

    @Test
    public void testPlistWithFalseElement() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<false/>\n" +
                "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof Boolean);
        Boolean b = (Boolean) obj;
        assertEquals(b, false);
    }

    // Date

    @Test
    public void testPlistWithDate() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<date>2018-03-17T15:53:00Z</date>\n" +
                "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof Date);
        Date d = (Date) obj;
        assertEquals(d, ISO8601DateFormatter.parse("2018-03-17T15:53:00Z"));
    }

    // Data

    @Test
    public void testPlistWithData() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                        "<plist version=\"1.0\">\n" +
                        "<data>U3RyaW5nNQ==</data>\n" +
                        "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof byte[]);
        byte[] d = (byte[]) obj;
        assertEquals(new String(d), "String5");
    }

    // Comments and whitespace

    @Test
    public void testCommentsAndWhitespace() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                        "\n\n\t\t\n\n\n<plist version=\"1.0\">\n\n\n     \n" +
                        "\t   <!-- A comment -->" +
                        "<string><!-- Another comment -->My string\twith a tab</string>\n\t\t   \n" +
                        "\t" +
                        "<!-- A multiline\n" +
	                    "  comment -->\n" +
                        "</plist>\n";

        XMLPropertyListReader p = new XMLPropertyListReader(new ByteArrayInputStream(template.getBytes()));
        Object obj = p.parse();

        assertNotNull(obj);
        assertTrue(obj instanceof String);
        String s = (String) obj;
        assertEquals(s, "My string\twith a tab");
    }


}
