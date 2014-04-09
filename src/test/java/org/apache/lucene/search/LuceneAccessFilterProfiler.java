package org.apache.lucene.search;


import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

public class LuceneAccessFilterProfiler extends BaseLuceneAccessControlFilterTest {

    @Override
    protected boolean profiling() {
        return true;
    }

    @Override
    Directory createIndexDirectory() throws IOException {
        return FSDirectory.open(new File("data"));
    }

    void profile(Grants grants) throws IOException, ParseException {
        long beginMillis = System.currentTimeMillis();

        Filter filter = null;
        if (grants != null) {
            filter = new AccessControlFilter("perm", grants.getMap());
        }

        Query query = queryParser.parse(QUERY_KEYWORD);
        ScoreDoc[] hits = indexSearcher.search(query, filter, Integer.MAX_VALUE).scoreDocs;

        long endMillis = System.currentTimeMillis();
        System.out.printf("Found %d for keyword '%s' in %d ms%n", hits.length, QUERY_KEYWORD, endMillis-beginMillis);
    }

    void profile() throws IOException, ParseException {
        setUpLuceneIndexer();
        index(1000);
        tearDownLuceneIndexer();

        setUpLuceneSearcher();

        profile(null);

        Grants grants = new Grants();
        grants
                .in("A").add("5")
                .in("B").add("105")
                .in("C").add("105")
                .in("D").add("5");
        profile(grants);
    }

    public static void main(String[] args) throws Exception {
        new LuceneAccessFilterProfiler().profile();
    }

}
