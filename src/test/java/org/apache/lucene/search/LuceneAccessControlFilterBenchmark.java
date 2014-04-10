package org.apache.lucene.search;


import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

public class LuceneAccessControlFilterBenchmark extends BaseLuceneAccessControlFilterTest {

    File indexDirectory = new File("lc-data");

    @Override
    protected boolean profiling() {
        return true;
    }

    @Override
    protected boolean isMemoryStore() {
        return false;
    }

    void benchmark(Grants grants) throws IOException, ParseException {
        deleteDirectory(new File(DATA_DIRECTORY));

        long beginMillis = System.currentTimeMillis();
        int count = search(grants);
        long endMillis = System.currentTimeMillis();

        System.out.printf("Found %d for keyword '%s' in %d ms%n", count, QUERY_KEYWORD, endMillis-beginMillis);
    }

    void benchmark() throws IOException, ParseException {
        setUpLuceneIndexer();
        index(1000);
        tearDownLuceneIndexer();

        setUpLuceneSearcher();

        benchmark(null);

        Grants grants = new Grants();
        grants
                .in("A").add("5")
                .in("B").add("105")
                .in("C").add("105")
                .in("D").add("5");
        benchmark(grants);
    }

    public static void main(String[] args) throws Exception {
        new LuceneAccessControlFilterBenchmark().benchmark();
    }

}
