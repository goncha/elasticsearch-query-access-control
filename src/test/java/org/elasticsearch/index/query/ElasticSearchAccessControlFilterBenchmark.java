package org.elasticsearch.index.query;


import org.apache.lucene.search.Grants;

import java.io.File;
import java.io.IOException;

public class ElasticSearchAccessControlFilterBenchmark extends BaseElasticSearchAccessControlFilterTest {

    @Override
    protected boolean profiling() {
        return true;
    }

    void benchmark(Grants grants) {
        long beginMillis = System.currentTimeMillis();
        int count = search(grants);
        long endMillis = System.currentTimeMillis();

        System.out.printf("Found %d for keyword '%s' in %d ms%n", count, QUERY_KEYWORD, endMillis-beginMillis);
    }

    void benchmark() throws IOException {
        deleteDirectory(new File(DATA_DIRECTORY));

        setUpNode();
        index(1000);

        benchmark(null);

        Grants grants = new Grants();
        grants
                .in("A").add("5")
                .in("B").add("105")
                .in("C").add("105")
                .in("D").add("5");
        benchmark(grants);

        tearDownNode();
    }

    public static void main(String[] args) throws Exception {
        new ElasticSearchAccessControlFilterBenchmark().benchmark();
    }

}
