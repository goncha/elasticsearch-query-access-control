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
        Assert.assertEquals("A=1;B=2;C=3", p.toString());
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
    public void testFromEmptyString() {
        Permission p  = new Permission();
        Assert.assertEquals("New permission has no permission entry", 0, p.getMap().size());
        p.fromString(null);
        Assert.assertEquals("fromString(null) not load any permission entries", 0, p.getMap().size());
        p.fromString("");
        Assert.assertEquals("fromString(\"\") not load any permission entries", 0, p.getMap().size());
    }

   @Test
    public void testSingleEntryFromString() {
        Permission p = new Permission();
        p.fromString("A=1");
        Assert.assertEquals(1, p.getMap().size());
        Assert.assertEquals(1, p.getMap().get("A").size());
        Assert.assertTrue(p.getMap().get("A").contains("1"));
    }

    @Test
    public void testTwoEntriesFromString() {
        Permission p = new Permission();
        p.fromString("A=1;B=2");
        Assert.assertEquals(2, p.getMap().size());
        Assert.assertEquals(1, p.getMap().get("A").size());
        Assert.assertEquals(1, p.getMap().get("B").size());
        Assert.assertTrue(p.getMap().get("A").contains("1"));
        Assert.assertTrue(p.getMap().get("B").contains("2"));
    }

    @Test
    public void testMultiValueEntryFromString() {
        Permission p = new Permission();
        p.fromString("A=1,2,3");
        Assert.assertEquals(1, p.getMap().size());
        Assert.assertEquals(3, p.getMap().get("A").size());
        Assert.assertTrue(p.getMap().get("A").contains("1"));
        Assert.assertTrue(p.getMap().get("A").contains("2"));
        Assert.assertTrue(p.getMap().get("A").contains("3"));
    }

    @Test
    public void testEndWithSeparatorFromString() {
        Permission p = new Permission();
        p.fromString("A=1;");
        Assert.assertEquals(1, p.getMap().size());
        Assert.assertEquals(1, p.getMap().get("A").size());
        Assert.assertTrue(p.getMap().get("A").contains("1"));
    }

    @Test
    public void testIgnoreEmptyStringValueFromString() {
        Permission p = new Permission();
        p.fromString("A=1,");
        Assert.assertEquals(1, p.getMap().size());
        Assert.assertEquals(1, p.getMap().get("A").size());
        Assert.assertTrue(p.getMap().get("A").contains("1"));
    }

    @Test
    public void testIgnoreAllEmptyStringValuesFromString() {
        Permission p = new Permission();
        p.fromString("A=,1,;B=,,1");
        Assert.assertEquals(2, p.getMap().size());
        Assert.assertEquals(1, p.getMap().get("A").size());
        Assert.assertEquals(1, p.getMap().get("B").size());
        Assert.assertTrue(p.getMap().get("A").contains("1"));
        Assert.assertTrue(p.getMap().get("B").contains("1"));
    }

    @Test(expected = IllegalStateException.class)
    public void testNoDimensionFromString() {
        new Permission().fromString("1,2");
    }


}
