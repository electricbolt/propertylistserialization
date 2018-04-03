/*******************************************************************************
 * DateUtilTest.java                                                           *
 * propertylistserialization Copyright (c) 2018; Electric Bolt Limited.        *
 ******************************************************************************/

package nz.co.electricbolt.propertylistserialization.internal;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class DateUtilTest {

    @Test
    public void formatXML() throws Exception {
        Date date = new Date();
        date.setTime(0L);
        String result = DateUtil.formatXML(date);
        assertEquals("1970-01-01T00:00:00Z", result);

        date.setTime(1521503927000L);
        result = DateUtil.formatXML(date);
        assertEquals("2018-03-19T23:58:47Z", result);
    }

    @Test
    public void parseXML() throws Exception {
        Date date = DateUtil.parseXML("2018-03-19T23:58:47Z");
        assertEquals(1521503927000L, date.getTime());

        date = DateUtil.parseXML("1970-01-01T00:00:00Z");
        assertEquals(0L, date.getTime());
    }

    @Test
    public void parseXMLFailure() throws Exception {
        try {
            Date date = DateUtil.parseXML("1970-01-01X00:00:00+13:00Z");
            fail("Should have thrown parse exception");
        } catch(ParseException pe) {
        }
    }

    @Test
    public void formatBinary() throws Exception {
        Date date = DateUtil.parseXML("2018-03-19T23:58:47Z");
        double val = DateUtil.formatBinary(date);
        assertEquals(5.43196727E8d, val, 0.01);

        date = DateUtil.parseXML("1970-01-01T00:00:00Z");
        val = DateUtil.formatBinary(date);
        assertEquals(-9.783072E8d, val, 0.01);
    }

    @Test
    public void parseBinary() throws Exception {
        Date date = DateUtil.parseBinary(5.43196727E8d);
        Date expected = DateUtil.parseXML("2018-03-19T23:58:47Z");
        assertEquals(expected, date);

        date = DateUtil.parseBinary(-9.783072E8d);
        expected = DateUtil.parseXML("1970-01-01T00:00:00Z");
        assertEquals(expected, date);
    }
}