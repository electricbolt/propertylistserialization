package nz.co.electricbolt.propertylistserialization.internal;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class ISO8601DateFormatterTest {

    @Test
    public void format() throws Exception {
        Date date = new Date();
        date.setTime(0L);
        String result = ISO8601DateFormatter.format(date);
        assertEquals("1970-01-01T00:00:00Z", result);
    }

    @Test
    public void format1() throws Exception {
        Date date = new Date();
        date.setTime(1521503927000L);
        String result = ISO8601DateFormatter.format(date);
        assertEquals("2018-03-19T23:58:47Z", result);
    }

    @Test
    public void parse() throws Exception {
        Date date = ISO8601DateFormatter.parse("2018-03-19T23:58:47Z");
        assertEquals(1521503927000L, date.getTime());
    }

    @Test
    public void parse1() throws Exception {
        Date date = ISO8601DateFormatter.parse("1970-01-01T00:00:00Z");
        assertEquals(0L, date.getTime());
    }

    @Test
    public void parseFailure() throws Exception {
        try {
            Date date = ISO8601DateFormatter.parse("1970-01-01X00:00:00+13:00Z");
            fail("Should have thrown parse exception");
        } catch(ParseException pe) {
        }
    }

}