package org.apache.lucene.search;


import java.io.*;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseAccessControlFilterTest {

    protected static final String QUERY_KEYWORD = "request";

    /**
     * Convert to permission to key-value map
     */
    protected Map<String,String> perm(String ... permEntries) {
        Map<String,String> perm = new HashMap<String,String>();
        for (int i = 0; i < permEntries.length; i += 2) {
            if (i+1 < permEntries.length) {
                perm.put(permEntries[i], permEntries[i+1]);
            }
        }
        return perm;
    }

    protected void beforeIndexing() {}

    protected void afterIndexing() {}

    protected boolean profiling() { return false; }

    protected boolean isMemoryStore() { return true; }

    protected void index(int multiply) throws IOException {
        long beginMillis = System.currentTimeMillis();

        beforeIndexing();

        int count = 0;
        for (int m = 0; m < multiply; m++) {
            try {
                count += indexBulk(m);
            } catch (Throwable t) {
                System.err.printf("Error at multiply: %d%n", m);
                throw new RuntimeException(t);
            }
        }

        afterIndexing();

        long endMillis = System.currentTimeMillis();
        if (profiling()) {
            System.out.printf("Indexed %d docs in %d ms%n", count, (endMillis-beginMillis));
        }
    }

    protected File getDataFile() {
        return new File("data.txt");
    }

    protected int indexBulk(int multiply) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(getDataFile())));

        String line = null;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            String id = Integer.toString(multiply * 10000 + i);
            Permission perm = new Permission();
            perm.in("A").add(Integer.toString(i % 100))
                .in("C").add(Integer.toString(i % 300))
                .in("B").add(Integer.toString(i % 300))
                .in("D").add(Integer.toString(i % 1000));
            i++;

            indexDoc(id, line, perm.toString());
        }
        reader.close();
        return i;
    }

    protected abstract void indexDoc(String id, String content, String perm) throws IOException;

    protected static void deleteDirectory(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteDirectory(f);
            }
        }
        if(!file.delete()) {
            System.err.printf("Failed to delete %s%n", file.getAbsolutePath());
        }
    }

}
