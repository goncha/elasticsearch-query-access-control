package org.apache.lucene.search;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AccessControlFilterPermissionTest {

    @Test
    public void testEmptyPermissionToString() {
        Assert.assertEquals("", new Permission().toString());
    }

    @Test
    public void testSingleEntryToString() {
        Permission p = new Permission();
        p.in("A").add("1");
        Assert.assertEquals("A=1", p.toString());
    }

    @Test
    public void testTwoEntriesToString() {
        Permission p = new Permission();
        p.in("A").add("1");
        p.in("B").add("2");
        Assert.assertEquals("A=1;B=2", p.toString());
    }

    @Test
    public void testTwoEntriesInReverseOrderToString() {
        Permission p = new Permission();
        p.in("B").add("2");
        p.in("A").add("1");
        Assert.assertEquals("A=1;B=2", p.toString());
    }

    @Test
    public void testThreeEntriesInReverseOrderToString() {
        Permission p = new Permission();
        p.in("C").add("3");
        p.in("B").add("2");
        p.in("A").add("1");
        Assert.assertEquals("A=1;B=2;C=3", p);
    }

    @Test
    public void testMultiItemsInOneEntryToString() {
        Permission p = new Permission();
        p.in("A").add("3").add("2").add("1");
        Assert.assertEquals("A=1,2,3", p.toString());
    }

    @Test
    public void testMultiItemsInMultiEntriesToString() {
        Permission p = new Permission();
        p.in("B").add("2");
        p.in("A").add("1");
        p.in("B").add("3");
        p.in("A").add("3");
        p.in("B").add("1");
        p.in("A").add("2");
        Assert.assertEquals("A=1,2,3;B=1,2,3", p.toString());
    }

    @Test
    public void testNullToMap() {
        Map<String,String> p = AccessControlFilter.toMap(null);
        Assert.assertEquals(0, p.size());
    }

    @Test
    public void testEmptyStringToMap() {
        Map<String,String> p = AccessControlFilter.toMap("");
        Assert.assertEquals(0, p.size());
    }

   @Test
    public void testSingleEntryToMap() {
        Map<String,String> p = AccessControlFilter.toMap("A=1");
        Assert.assertEquals(1, p.size());
        Assert.assertEquals("1", p.get("A"));
    }

    @Test
    public void testTwoEntriesToMap() {
        Map<String,String> p = AccessControlFilter.toMap("A=1,B=2");
        Assert.assertEquals(2, p.size());
        Assert.assertEquals("1", p.get("A"));
        Assert.assertEquals("2", p.get("B"));
    }

    @Test
    public void testEmptyEntryValueToMap() {
        Map<String,String> p = AccessControlFilter.toMap("A=1,B=");
        Assert.assertEquals(2, p.size());
        Assert.assertEquals("1", p.get("A"));
        Assert.assertEquals("", p.get("B"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalPermStrInOneEntryToMap() {
        AccessControlFilter.toMap("A");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalPermStrInTowEntryToMap() {
        AccessControlFilter.toMap("A,B");
    }


}
