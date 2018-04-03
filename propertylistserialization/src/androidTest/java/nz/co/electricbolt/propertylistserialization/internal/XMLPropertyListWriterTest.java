/*******************************************************************************
 * XMLPropertyListWriterTest.java                                              *
 * propertylistserialization Copyright (c) 2018; Electric Bolt Limited.        *
 ******************************************************************************/

package nz.co.electricbolt.propertylistserialization.internal;

import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class XMLPropertyListWriterTest {

    // Plist

    @Test
    public void nullGraph() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLPropertyListWriter p = new XMLPropertyListWriter(null, baos);
        try {
            p.write();
            fail("Should have thrown an exception - object graph cannot contain null");
        } catch (Exception e) {
        }
    }

    // String

    @Test
    public void string() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<string>String1</string>\n" +
                "</plist>\n";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLPropertyListWriter p = new XMLPropertyListWriter("String1", baos);
        p.write();
        String result = baos.toString("utf8");
        assertEquals(template, result);
    }

    @Test
    public void emptyString() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<string></string>\n" +
                "</plist>\n";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLPropertyListWriter p = new XMLPropertyListWriter("", baos);
        p.write();
        String result = baos.toString("utf8");
        assertEquals(template, result);
    }

    // Integer

    @Test
    public void integer() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<integer>42</integer>\n" +
                "</plist>\n";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLPropertyListWriter p = new XMLPropertyListWriter(42, baos);
        p.write();
        String result = baos.toString("utf8");
        assertEquals(template, result);
    }

    // Real

    @Test
    public void real() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<real>42.5</real>\n" +
                "</plist>\n";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLPropertyListWriter p = new XMLPropertyListWriter(42.5f, baos); // float
        p.write();
        String result = baos.toString("utf8");
        assertEquals(template, result);

        baos = new ByteArrayOutputStream();
        p = new XMLPropertyListWriter(42.5d, baos); // double
        p.write();
        result = baos.toString("utf8");
        assertEquals(template, result);
    }

    // Date

    @Test
    public void date() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<date>2018-03-17T15:53:00Z</date>\n" +
                "</plist>\n";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLPropertyListWriter p = new XMLPropertyListWriter(DateUtil.parseXML("2018-03-17T15:53:00Z"), baos);
        p.write();
        String result = baos.toString("utf8");
        assertEquals(template, result);
    }

    // True

    @Test
    public void booleanTrue() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<true/>\n" +
                "</plist>\n";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLPropertyListWriter p = new XMLPropertyListWriter(true, baos);
        p.write();
        String result = baos.toString("utf8");
        assertEquals(template, result);
    }

    // False

    @Test
    public void booleanFalse() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<false/>\n" +
                "</plist>\n";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLPropertyListWriter p = new XMLPropertyListWriter(false, baos);
        p.write();
        String result = baos.toString("utf8");
        assertEquals(template, result);
    }

    // Array

    @Test
    public void array() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<array>\n" +
                "\t<array>\n" +
                "\t\t<string>String1</string>\n" +
                "\t\t<string>String2</string>\n" +
                "\t\t<string></string>\n" +
                "\t</array>\n" +
                "\t<dict>\n" +
                "\t\t<key>Key</key>\n" +
                "\t\t<string>Value</string>\n" +
                "\t</dict>\n" +
                "\t<integer>5</integer>\n" +
                "\t<real>42.5</real>\n" +
                "\t<true/>\n" +
                "\t<false/>\n" +
                "</array>\n" +
                "</plist>\n";

        ArrayList graph1 = new ArrayList();
        ArrayList graph2 = new ArrayList();
        graph2.add("String1");
        graph2.add("String2");
        graph2.add("");
        graph1.add(graph2);
        HashMap dict = new HashMap();
        dict.put("Key", "Value");
        graph1.add(dict);
        graph1.add(5);
        graph1.add(42.5d);
        graph1.add(true);
        graph1.add(false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLPropertyListWriter p = new XMLPropertyListWriter(graph1, baos);
        p.write();
        String result = baos.toString("utf8");
        assertEquals(template, result);
    }

    // Dict

    @Test
    public void dict() throws Exception {
        // Dict's are sorted by key.
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<dict>\n" +
                "\t<key>Array</key>\n" +
                "\t<array>\n" +
                "\t\t<string>String1</string>\n" +
                "\t\t<string>String2</string>\n" +
                "\t\t<string></string>\n" +
                "\t</array>\n" +
                "\t<key>Dict</key>\n" +
                "\t<dict>\n" +
                "\t\t<key>Key</key>\n" +
                "\t\t<string>Value</string>\n" +
                "\t</dict>\n" +
                "\t<key>False</key>\n" +
                "\t<false/>\n" +
                "\t<key>Integer</key>\n" +
                "\t<integer>5</integer>\n" +
                "\t<key>Real</key>\n" +
                "\t<real>42.5</real>\n" +
                "\t<key>True</key>\n" +
                "\t<true/>\n" +
                "</dict>\n" +
                "</plist>\n";

        HashMap graph1 = new HashMap();
        graph1.put("True", true);
        HashMap dict = new HashMap();
        dict.put("Key", "Value");
        graph1.put("Dict", dict);
        graph1.put("Integer", 5);
        graph1.put("Real", 42.5d);
        ArrayList graph2 = new ArrayList();
        graph2.add("String1");
        graph2.add("String2");
        graph2.add("");
        graph1.put("Array", graph2);
        graph1.put("False", false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLPropertyListWriter p = new XMLPropertyListWriter(graph1, baos);
        p.write();
        String result = baos.toString("utf8");
        assertEquals(template, result);
    }

    // Data

    @Test
    public void dataNoIndent() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<data>\n" +
                "U3RyaW5nNQ==\n" +
                "</data>\n" +
                "</plist>\n";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLPropertyListWriter p = new XMLPropertyListWriter("String5".getBytes("utf8"), baos);
        p.write();
        String result = baos.toString("utf8");
        assertEquals(template, result);
    }

    @Test
    public void dataOneIndent() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<dict>\n" +
                "\t<key>Data</key>\n" +
                "\t<data>\n" +
                "\tU3RyaW5nNQ==\n" +
                "\t</data>\n" +
                "</dict>\n" +
                "</plist>\n";

        HashMap dict = new HashMap();
        dict.put("Data", "String5".getBytes("utf8"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLPropertyListWriter p = new XMLPropertyListWriter(dict, baos);
        p.write();
        String result = baos.toString("utf8");
        assertEquals(template, result);
    }

    @Test
    public void dataTwoIndent() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<dict>\n" +
                "\t<key>Data</key>\n" +
                "\t<dict>\n" +
                "\t\t<key>Data2</key>\n" +
                "\t\t<data>\n" +
                "\t\tU3RyaW5nNQ==\n" +
                "\t\t</data>\n" +
                "\t</dict>\n" +
                "</dict>\n" +
                "</plist>\n";

        HashMap dict1 = new HashMap();
        HashMap dict2 = new HashMap();
        dict1.put("Data", dict2);
        dict2.put("Data2", "String5".getBytes("utf8"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLPropertyListWriter p = new XMLPropertyListWriter(dict1, baos);
        p.write();
        String result = baos.toString("utf8");
        assertEquals(template, result);
    }

    @Test
    public void dataEightIndent() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<dict>\n" +
                "\t<key>Data</key>\n" +
                "\t<dict>\n" +
                "\t\t<key>Data2</key>\n" +
                "\t\t<dict>\n" +
                "\t\t\t<key>Data3</key>\n" +
                "\t\t\t<dict>\n" +
                "\t\t\t\t<key>Data4</key>\n" +
                "\t\t\t\t<dict>\n" +
                "\t\t\t\t\t<key>Data5</key>\n" +
                "\t\t\t\t\t<dict>\n" +
                "\t\t\t\t\t\t<key>Data6</key>\n" +
                "\t\t\t\t\t\t<dict>\n" +
                "\t\t\t\t\t\t\t<key>Data7</key>\n" +
                "\t\t\t\t\t\t\t<dict>\n" +
                "\t\t\t\t\t\t\t\t<key>Data8</key>\n" +
                "\t\t\t\t\t\t\t\t<data>\n" +
                "\t\t\t\t\t\t\t\tU3RyaW5nNQ==\n" +
                "\t\t\t\t\t\t\t\t</data>\n" +
                "\t\t\t\t\t\t\t</dict>\n" +
                "\t\t\t\t\t\t</dict>\n" +
                "\t\t\t\t\t</dict>\n" +
                "\t\t\t\t</dict>\n" +
                "\t\t\t</dict>\n" +
                "\t\t</dict>\n" +
                "\t</dict>\n" +
                "</dict>\n" +
                "</plist>\n";

        HashMap dict1 = new HashMap();
        HashMap dict2 = new HashMap();
        HashMap dict3 = new HashMap();
        HashMap dict4 = new HashMap();
        HashMap dict5 = new HashMap();
        HashMap dict6 = new HashMap();
        HashMap dict7 = new HashMap();
        HashMap dict8 = new HashMap();

        dict1.put("Data", dict2);
        dict2.put("Data2", dict3);
        dict3.put("Data3", dict4);
        dict4.put("Data4", dict5);
        dict5.put("Data5", dict6);
        dict6.put("Data6", dict7);
        dict7.put("Data7", dict8);
        dict8.put("Data8", "String5".getBytes("utf8"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLPropertyListWriter p = new XMLPropertyListWriter(dict1, baos);
        p.write();
        String result = baos.toString("utf8");
        assertEquals(template, result);
    }

    @Test
    public void dataNineIndentLimitedToEightIndent() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                        "<plist version=\"1.0\">\n" +
                        "<dict>\n" +
                        "\t<key>Data</key>\n" +
                        "\t<dict>\n" +
                        "\t\t<key>Data2</key>\n" +
                        "\t\t<dict>\n" +
                        "\t\t\t<key>Data3</key>\n" +
                        "\t\t\t<dict>\n" +
                        "\t\t\t\t<key>Data4</key>\n" +
                        "\t\t\t\t<dict>\n" +
                        "\t\t\t\t\t<key>Data5</key>\n" +
                        "\t\t\t\t\t<dict>\n" +
                        "\t\t\t\t\t\t<key>Data6</key>\n" +
                        "\t\t\t\t\t\t<dict>\n" +
                        "\t\t\t\t\t\t\t<key>Data7</key>\n" +
                        "\t\t\t\t\t\t\t<dict>\n" +
                        "\t\t\t\t\t\t\t\t<key>Data8</key>\n" +
                        "\t\t\t\t\t\t\t\t<dict>\n" +
                        "\t\t\t\t\t\t\t\t<key>Data9</key>\n" +
                        "\t\t\t\t\t\t\t\t<data>\n" +
                        "\t\t\t\t\t\t\t\tU3RyaW5nNQ==\n" +
                        "\t\t\t\t\t\t\t\t</data>\n" +
                        "\t\t\t\t\t\t\t\t</dict>\n" +
                        "\t\t\t\t\t\t\t</dict>\n" +
                        "\t\t\t\t\t\t</dict>\n" +
                        "\t\t\t\t\t</dict>\n" +
                        "\t\t\t\t</dict>\n" +
                        "\t\t\t</dict>\n" +
                        "\t\t</dict>\n" +
                        "\t</dict>\n" +
                        "</dict>\n" +
                        "</plist>\n";

        HashMap dict1 = new HashMap();
        HashMap dict2 = new HashMap();
        HashMap dict3 = new HashMap();
        HashMap dict4 = new HashMap();
        HashMap dict5 = new HashMap();
        HashMap dict6 = new HashMap();
        HashMap dict7 = new HashMap();
        HashMap dict8 = new HashMap();
        HashMap dict9 = new HashMap();

        dict1.put("Data", dict2);
        dict2.put("Data2", dict3);
        dict3.put("Data3", dict4);
        dict4.put("Data4", dict5);
        dict5.put("Data5", dict6);
        dict6.put("Data6", dict7);
        dict7.put("Data7", dict8);
        dict8.put("Data8", dict9);
        dict9.put("Data9", "String5".getBytes("utf8"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLPropertyListWriter p = new XMLPropertyListWriter(dict1, baos);
        p.write();
        String result = baos.toString("utf8");
        assertEquals(template, result);
    }

    @Test
    public void largeDataTwoIndentSpanning3Lines() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<dict>\n" +
                "\t<key>Data</key>\n" +
                "\t<dict>\n" +
                "\t\t<key>Data2</key>\n" +
                "\t\t<data>\n" +
                "\t\tVGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZy4g\n" +
                "\t\tRWxlcGhhbnRzIGFuZCBzaGVlcCBhcmUgdmlzdWFsbHkgcXVpdGUgZGlmZmVy\n" +
                "\t\tZW50Lg==\n" +
                "\t\t</data>\n" +
                "\t</dict>\n" +
                "</dict>\n" +
                "</plist>\n";

        String s = Base64.encodeToString("The quick brown fox jumps over the lazy dog. Elephants and sheep are visually quite different.".getBytes("utf8"), Base64.NO_WRAP);

        HashMap dict1 = new HashMap();
        HashMap dict2 = new HashMap();
        dict1.put("Data", dict2);
        dict2.put("Data2", "The quick brown fox jumps over the lazy dog. Elephants and sheep are visually quite different.".getBytes("utf8"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLPropertyListWriter p = new XMLPropertyListWriter(dict1, baos);
        p.write();
        String result = baos.toString("utf8");
        assertEquals(template, result);
    }

    @Test
    public void largeDataNineIndentSpanning5Lines() throws Exception {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                        "<plist version=\"1.0\">\n" +
                        "<dict>\n" +
                        "\t<key>Data</key>\n" +
                        "\t<dict>\n" +
                        "\t\t<key>Data2</key>\n" +
                        "\t\t<dict>\n" +
                        "\t\t\t<key>Data3</key>\n" +
                        "\t\t\t<dict>\n" +
                        "\t\t\t\t<key>Data4</key>\n" +
                        "\t\t\t\t<dict>\n" +
                        "\t\t\t\t\t<key>Data5</key>\n" +
                        "\t\t\t\t\t<dict>\n" +
                        "\t\t\t\t\t\t<key>Data6</key>\n" +
                        "\t\t\t\t\t\t<dict>\n" +
                        "\t\t\t\t\t\t\t<key>Data7</key>\n" +
                        "\t\t\t\t\t\t\t<dict>\n" +
                        "\t\t\t\t\t\t\t\t<key>Data8</key>\n" +
                        "\t\t\t\t\t\t\t\t<dict>\n" +
                        "\t\t\t\t\t\t\t\t<key>Data9</key>\n" +
                        "\t\t\t\t\t\t\t\t<data>\n" +
                        "\t\t\t\t\t\t\t\tVkdobElIRjFh\n" +
                        "\t\t\t\t\t\t\t\tV05ySUdKeWIz\n" +
                        "\t\t\t\t\t\t\t\tZHVJR1p2ZUNC\n" +
                        "\t\t\t\t\t\t\t\tcWRXMXdjeUJ2\n" +
                        "\t\t\t\t\t\t\t\tZG1WeUlIUm9a\n" +
                        "\t\t\t\t\t\t\t\tU0JzWVhwNUlH\n" +
                        "\t\t\t\t\t\t\t\tUnZaeTRnUld4\n" +
                        "\t\t\t\t\t\t\t\tbGNHaGhiblJ6\n" +
                        "\t\t\t\t\t\t\t\tSUdGdVpDQnph\n" +
                        "\t\t\t\t\t\t\t\tR1ZsY0NCaGNt\n" +
                        "\t\t\t\t\t\t\t\tVWdkbWx6ZFdG\n" +
                        "\t\t\t\t\t\t\t\tc2JIa2djWFZw\n" +
                        "\t\t\t\t\t\t\t\tZEdVZ1pHbG1a\n" +
                        "\t\t\t\t\t\t\t\tbVZ5Wlc1MExn\n" +
                        "\t\t\t\t\t\t\t\tPT0=\n" +
                        "\t\t\t\t\t\t\t\t</data>\n" +
                        "\t\t\t\t\t\t\t\t</dict>\n" +
                        "\t\t\t\t\t\t\t</dict>\n" +
                        "\t\t\t\t\t\t</dict>\n" +
                        "\t\t\t\t\t</dict>\n" +
                        "\t\t\t\t</dict>\n" +
                        "\t\t\t</dict>\n" +
                        "\t\t</dict>\n" +
                        "\t</dict>\n" +
                        "</dict>\n" +
                        "</plist>\n";

        HashMap dict1 = new HashMap();
        HashMap dict2 = new HashMap();
        HashMap dict3 = new HashMap();
        HashMap dict4 = new HashMap();
        HashMap dict5 = new HashMap();
        HashMap dict6 = new HashMap();
        HashMap dict7 = new HashMap();
        HashMap dict8 = new HashMap();
        HashMap dict9 = new HashMap();

        dict1.put("Data", dict2);
        dict2.put("Data2", dict3);
        dict3.put("Data3", dict4);
        dict4.put("Data4", dict5);
        dict5.put("Data5", dict6);
        dict6.put("Data6", dict7);
        dict7.put("Data7", dict8);
        dict8.put("Data8", dict9);

        String s = Base64.encodeToString("The quick brown fox jumps over the lazy dog. Elephants and sheep are visually quite different.".getBytes("utf8"), Base64.NO_WRAP);

        dict9.put("Data9", s.getBytes("utf8"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLPropertyListWriter p = new XMLPropertyListWriter(dict1, baos);
        p.write();
        String result = baos.toString("utf8");
        assertEquals(template, result);
    }

}