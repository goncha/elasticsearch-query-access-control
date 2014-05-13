package org.apache.lucene.search;


import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class GrantsTest {

    @Test
    public void emptyGrants() {
        Assert.assertEquals(0, new Grants().getMap().size());
    }

    @Test
    public void grantAll() {
        Grants grants = new Grants();
        grants.allIn("X");
        Assert.assertEquals(Boolean.TRUE, grants.getMap().get("X"));
    }

    @Test
    public void grantItemsAfterGrantingAll() {
        Grants grants = new Grants();
        grants.allIn("X");

        grants.in("X").add("1");
        Assert.assertEquals("must TRUE in X after adding one item", Boolean.TRUE, grants.getMap().get("X"));

        grants.in("X").add("2");
        Assert.assertEquals("must TRUE in X after adding two items", Boolean.TRUE, grants.getMap().get("X"));
    }

    @Test
    public void grantAllAfterGrantingItems() {
        Grants grants = new Grants();
        grants.in("X").add("1");
        grants.allIn("X");
        Assert.assertEquals(Boolean.TRUE, grants.getMap().get("X"));
    }

    @Test
    public void grantItems() {
        Grants grants = new Grants();
        grants.in("X").add("1");
        Assert.assertEquals("must one item in X", 1, ((Set<String>) grants.getMap().get("X")).size());
        Assert.assertTrue("must contains \"1\" in X", ((Set<String>) grants.getMap().get("X")).contains("1"));
        grants.add("2");
        Assert.assertEquals("must two items in X", 2, ((Set<String>) grants.getMap().get("X")).size());
        Assert.assertTrue("must contains \"1\" in X", ((Set<String>) grants.getMap().get("X")).contains("1"));
        Assert.assertTrue("must contains \"2\" in X", ((Set<String>) grants.getMap().get("X")).contains("2"));
    }

    @Test
    public void grantTwoDimension() {
        Grants grants = new Grants();

        grants.allIn("X");
        grants.in("Y").add("1");

        Assert.assertEquals("must two dimension", 2, grants.getMap().size());
        Assert.assertEquals("must TRUE in X", Boolean.TRUE, grants.getMap().get("X"));
        Assert.assertEquals("must one item in Y", 1, ((Set<String>) grants.getMap().get("Y")).size());
        Assert.assertTrue("must contains \"1\"in Y", ((Set<String>) grants.getMap().get("Y")).contains("1"));
    }

    @Test
    public void removeItems() {
        Grants grants = new Grants();

        grants.in("X").add("1").remove("1");
        Assert.assertEquals(0, ((Set<String>) grants.getMap().get("X")).size());
    }

    @Test
    public void removeItemsAfterGrantingAll() {
        Grants grants = new Grants();

        grants.allIn("X").in("X").remove("1");
        Assert.assertEquals(Boolean.TRUE, grants.getMap().get("X"));
    }

    @Test(expected = IllegalStateException.class)
    public void notCallInBeforeAdd() {
        new Grants().add("1");
    }

    @Test(expected = IllegalStateException.class)
    public void notCallInBeforeRemove() {
        new Grants().remove("1");
    }

}
