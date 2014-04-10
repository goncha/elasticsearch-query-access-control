package org.apache.lucene.search;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class LuceneAccessControlFilterTest extends BaseLuceneAccessControlFilterTest {


    @Before
    public void setUpLucene() throws IOException {
        setUpLuceneIndexer();
        index(1);
        tearDownLuceneIndexer();

        setUpLuceneSearcher();
    }

    @Test
    public void searchWithoutAccessControl() throws Exception {
        Assert.assertEquals(416, search(null));
    }


    @Test
    public void searchWithAccessControlThenReturnsOneHit() throws Exception {
        Grants grants = new Grants();
        grants
                .in("A").add("0")
                .in("B").add("0")
                .in("C").add("0")
                .in("D").add("400");
        Assert.assertEquals(1, search(grants));
    }

    @Test
    public void searchWithAccessControlThenReturnsTwoHits() throws Exception {
        Grants grants = new Grants();
        grants
                .in("A").add("5")
                .in("B").add("105")
                .in("C").add("105")
                .in("D").add("5");
        Assert.assertEquals(2, search(grants));
    }

    @Test
    public void searchWithAccessControlThenReturnsThreeHitsByMultiDimensionIds() throws Exception {
        Grants grants = new Grants();
        grants
                .in("A").add("0").add("1")
                .in("B").add("0").add("1")
                .in("C").add("0").add("1")
                .in("D").add("400").add("800").add("601");
        Assert.assertEquals(3, search(grants));
    }

    @Test
    public void searchWithAccessControlThenReturnsZeroHit() throws Exception {
        Grants grants = new Grants();
        grants
                .in("A").add("0")
                .in("B").add("0")
                .in("C").add("0")
                .in("D").add("0");
        Assert.assertEquals(0, search(grants));
    }

    @Test
    public void searchWithAccessControlThenReturnsZeroHitByMultiDimensionIds() throws Exception {
        Grants grants = new Grants();
        grants
                .in("A").add("0").add("1").add("2")
                .in("B").add("0").add("1").add("2")
                .in("C").add("0").add("1").add("2")
                .in("D").add("0").add("1").add("2");
        Assert.assertEquals(0, search(grants));
    }

}
