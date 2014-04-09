package org.apache.lucene.search;

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
        Query query = queryParser.parse(QUERY_KEYWORD);
        ScoreDoc[] hits = indexSearcher.search(query, null, Integer.MAX_VALUE).scoreDocs;
        Assert.assertEquals(416, hits.length);
    }


    @Test
    public void searchWithAccessControlThenReturnsOneHit() throws Exception {
        Grants grants = new Grants();
        grants
                .in("A").add("0")
                .in("B").add("0")
                .in("C").add("0")
                .in("D").add("400");
        Filter filter = new AccessControlFilter("perm", grants.getMap());

        Query query = queryParser.parse(QUERY_KEYWORD);
        ScoreDoc[] hits = indexSearcher.search(query, filter, Integer.MAX_VALUE).scoreDocs;
        Assert.assertEquals(1, hits.length);
    }

    @Test
    public void searchWithAccessControlThenReturnsTwoHits() throws Exception {
        Grants grants = new Grants();
        grants
                .in("A").add("5")
                .in("B").add("105")
                .in("C").add("105")
                .in("D").add("5");
        Filter filter = new AccessControlFilter("perm", grants.getMap());

        Query query = queryParser.parse(QUERY_KEYWORD);
        ScoreDoc[] hits = indexSearcher.search(query, filter, Integer.MAX_VALUE).scoreDocs;
        Assert.assertEquals(2, hits.length);
    }

    @Test
    public void searchWithAccessControlThenReturnsThreeHitsByMultiDimensionIds() throws Exception {
        Grants grants = new Grants();
        grants
                .in("A").add("0").add("1")
                .in("B").add("0").add("1")
                .in("C").add("0").add("1")
                .in("D").add("400").add("800").add("601");
        Filter filter = new AccessControlFilter("perm", grants.getMap());

        Query query = queryParser.parse(QUERY_KEYWORD);
        ScoreDoc[] hits = indexSearcher.search(query, filter, Integer.MAX_VALUE).scoreDocs;
        Assert.assertEquals(3, hits.length);
    }

    @Test
    public void searchWithAccessControlThenReturnsZeroHit() throws Exception {
        Grants grants = new Grants();
        grants
                .in("A").add("0")
                .in("B").add("0")
                .in("C").add("0")
                .in("D").add("0");
        Filter filter = new AccessControlFilter("perm", grants.getMap());

        Query query = queryParser.parse(QUERY_KEYWORD);
        ScoreDoc[] hits = indexSearcher.search(query, filter, Integer.MAX_VALUE).scoreDocs;
        Assert.assertEquals(0, hits.length);
    }

    @Test
    public void searchWithAccessControlThenReturnsZeroHitByMultiDimensionIds() throws Exception {
        Grants grants = new Grants();
        grants
                .in("A").add("0").add("1").add("2")
                .in("B").add("0").add("1").add("2")
                .in("C").add("0").add("1").add("2")
                .in("D").add("0").add("1").add("2");
        Filter filter = new AccessControlFilter("perm", grants.getMap());

        Query query = queryParser.parse(QUERY_KEYWORD);
        ScoreDoc[] hits = indexSearcher.search(query, filter, Integer.MAX_VALUE).scoreDocs;
        Assert.assertEquals(0, hits.length);
    }
}
