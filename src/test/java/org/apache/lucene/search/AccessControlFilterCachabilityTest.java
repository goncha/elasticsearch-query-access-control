package org.apache.lucene.search;


import org.junit.Test;

import java.util.HashSet;

import org.junit.Assert;

public class AccessControlFilterCachabilityTest {

    @Test
    public void testCachability() {
        Grants grants = new Grants();
        grants.in("X").add("1").add("2");
        grants.in("Y").add("1");
        AccessControlFilter filter = new AccessControlFilter("field1", grants.getMap());

        HashSet<Filter> cachedFilters = new HashSet<Filter>();
        cachedFilters.add(filter);

        Assert.assertTrue("Must be cached",
                cachedFilters.contains(new AccessControlFilter("field1", grants.getMap())));
        Assert.assertFalse("Must not be cached",
                cachedFilters.contains(new AccessControlFilter("field2", grants.getMap())));
        Assert.assertFalse("Must not be cached",
                cachedFilters.contains(new AccessControlFilter("field1", grants.in("C").add("1").getMap())));
    }

}
