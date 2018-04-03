/*******************************************************************************
 * XMLPropertyListReader.java                                                  *
 * propertylistserialization Copyright (c) 2018; Electric Bolt Limited.        *
 ******************************************************************************/

package nz.co.electricbolt.propertylistserialization.internal;

import android.util.Base64;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Implements an Apple compatible property list (plist) parser - XML style only.
 * Uses Android's XmlPullParser for lower memory consumption than a DOM parser.
 *
 * Property list elements are parsed as follows:
 * string (NSString) -> java.lang.String
 * integer (NSInteger) -> java.lang.Integer
 * real (double) -> java.lang.Double
 * dict (NSDictionary) -> java.util.HashMap<String, Object>
 * array (NSArray) -> java.util.ArrayList
 * date (NSDate) -> java.util.Date
 * true (BOOL) -> Boolean.valueOf(true)
 * false (BOOL) -> Boolean.valueOf(false)
 * data (NSData) -> byte[]
 */

public class XMLPropertyListReader {

    private InputStream is;
    private XmlPullParser parser;

    public XMLPropertyListReader(InputStream is) {
        this.is = is;
        this.parser = Xml.newPullParser();
    }

    public Object parse() throws XmlPullParserException, IOException, ParseException {
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            parser.nextTag(); // advance past XmlPullparser.START_DOCUMENT state
            return readPlist();
        } finally {
            is.close();
        }
    }

    private Object readPlist() throws XmlPullParserException, IOException, ParseException {
        parser.require(XmlPullParser.START_TAG, null, "plist");

        parser.nextTag();
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new XmlPullParserException("START_TAG expected", parser, null);
        }
        String name = parser.getName(); // dict,array,string,real,date,real,integer,true,false
        Object value = readObject(name);

        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, null, "plist");

        return value;
    }

    private HashMap<String, Object> readDict() throws XmlPullParserException, IOException, ParseException {
        parser.nextTag();

        HashMap<String, Object> dict = new HashMap<>();
        while (parser.getEventType() != XmlPullParser.END_TAG) {

            parser.require(XmlPullParser.START_TAG, null, "key");

            parser.next();
            if (parser.getEventType() != XmlPullParser.TEXT) {
                throw new XmlPullParserException("TEXT expected", parser, null);
            }
            String key = parser.getText(); // key: always a string

            parser.nextTag();
            parser.require(XmlPullParser.END_TAG, null, "key");

            parser.nextTag();
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new XmlPullParserException("START_TAG expected", parser, null);
            }
            String name = parser.getName(); // value: dict,array,string,real,date,real,integer,true,false
            dict.put(key, readObject(name));
            parser.nextTag();
        }
        return dict;
    }

    private ArrayList readArray() throws XmlPullParserException, IOException, ParseException {
        parser.nextTag();

        ArrayList<Object> list = new ArrayList<>();

        while (parser.getEventType() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new XmlPullParserException("START_TAG expected", parser, null);
            }

            String name = parser.getName();
            list.add(readObject(name));
            parser.nextTag();
        }

        return list;
    }

    private Object readObject(String tagName) throws ParseException, XmlPullParserException, IOException {
        switch(tagName) {
            case "array":
                return readArray();
            case "dict":
                return readDict();
            case "string":
                return readString("string");
            case "data":
                return Base64.decode(readString("data"), Base64.DEFAULT);
            case "date":
                return DateUtil.parseXML(readString("date"));
            case "integer":
                return Integer.valueOf(readString("integer"));
            case "real":
                return Double.valueOf(readString("real"));
            case "true": {
                parser.nextTag();
                parser.require(XmlPullParser.END_TAG, null, "true");
                return true;
            }
            case "false": {
                parser.nextTag();
                parser.require(XmlPullParser.END_TAG, null, "false");
                return false;
            }
            default:
                throw new XmlPullParserException("unexpected tag " + tagName, parser, null);
        }
    }

    private String readString(String tagName) throws XmlPullParserException, IOException {
        parser.next();

        if (parser.getEventType() == XmlPullParser.END_TAG) {
            // Handle empty string. e.g. <string></string>
            parser.require(XmlPullParser.END_TAG, null, tagName);
            return "";
        }

        if (parser.getEventType() != XmlPullParser.TEXT) {
            throw new XmlPullParserException("TEXT expected", parser, null);
        }
        String result = parser.getText();
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, null, tagName);
        return result;
    }

}