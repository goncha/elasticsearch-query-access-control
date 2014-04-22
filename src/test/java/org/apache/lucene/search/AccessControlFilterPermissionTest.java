package org.apache.lucene.search;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class AccessControlFilterPermissionTest {

    @Test
    public void testSingleEntryToString() {
        Map<String,String> p = new HashMap<String,String>();
        p.put("A", "1");
        Assert.assertEquals("A=1", AccessControlFilter.toString(p));
    }

    @Test
    public void testTwoEntriesToString() {
        Map<String,String> p = new HashMap<String,String>();
        p.put("A", "1");
        p.put("B", "2");
        Assert.assertEquals("A=1,B=2", AccessControlFilter.toString(p));
    }

    @Test
    public void testTwoEntriesInReverseOrderToString() {
        Map<String,String> p = new HashMap<String,String>();
        p.put("B", "2");
        p.put("A", "1");
        Assert.assertEquals("A=1,B=2", AccessControlFilter.toString(p));
    }

    @Test
    public void testThreeEntriesInReverseOrderToString() {
        Map<String,String> p = new HashMap<String,String>();
        p.put("C", "3");
        p.put("B", "2");
        p.put("A", "1");
        Assert.assertEquals("A=1,B=2,C=3", AccessControlFilter.toString(p));
    }

    @Test
    public void testZeroEntryFromString() {
        Map<String,String> p = AccessControlFilter.toMap("");
        Assert.assertEquals(0, p.size());
    }

   @Test
    public void testSingleEntryFromString() {
        Map<String,String> p = AccessControlFilter.toMap("A=1");
        Assert.assertEquals(1, p.size());
        Assert.assertEquals("1", p.get("A"));
    }

    @Test
    public void testTwoEntriesFromString() {
        Map<String,String> p = AccessControlFilter.toMap("A=1,B=2");
        Assert.assertEquals(2, p.size());
        Assert.assertEquals("1", p.get("A"));
        Assert.assertEquals("2", p.get("B"));
    }

    @Test
    public void testEmptyEntryValueFromStr() {
        Map<String,String> p = AccessControlFilter.toMap("A=1,B=");
        Assert.assertEquals(2, p.size());
        Assert.assertEquals("1", p.get("A"));
        Assert.assertEquals("", p.get("B"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalPermStrInOneEntry() {
        AccessControlFilter.toMap("A");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalPermStrInTowEntry() {
        AccessControlFilter.toMap("A,B");
    }


}
