package org.apache.lucene.search;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class AccessControlFilterTest {

    static Permission p() { return new Permission(); }
    static Grants g() { return new Grants(); }


    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {//0
                    null,
                    null,
                    true
                },
                {//1
                    p().in("X").add("1"),
                    null,
                    true
                },
                {//2
                    p().in("X").add("1"),
                    g().in("X").add("1"),
                    true
                },
                {//3
                    p().in("X").add("1"),
                    g().in("X").add("2"),
                    false
                },
                {//4
                    p().in("X").add("1"),
                    g().in("X").add("1").add("2"),
                    true
                },
                {//5
                    p().in("X").add("1").add("2"),
                    g().in("X").add("1"),
                    false
                },
                {//6
                    p().in("X").add("1").add("2"),
                    g().in("X").add("1").add("2"),
                    true
                },
                {//7
                    p().in("X").add("1").in("Y").add("1"),
                    g().in("X").add("1"),
                    false
                },
                {//8
                    p().in("X").add("1").in("Y").add("1"),
                    g().in("X").add("1").in("Y").add("1"),
                    true
                },
                {//9
                    p().in("X").add("1").add("2").in("Y").add("1"),
                    g().in("X").add("1").in("Y").add("1"),
                    false
                },
                {//10
                    p().in("X").add("1").add("2").in("Y").add("1"),
                    g().in("X").add("1").add("2").in("Y").add("1"),
                    true
                },
                {//11
                    p().in("X").add("1").add("2").in("Y").add("1"),
                    g().allIn("X").in("Y").add("1"),
                    true
                },
                {//12
                    p().in("X").add("1").add("2").in("Y").add("1"),
                    g().in("X").add("1").add("2").allIn("Y"),
                    true
                },
        });
    }



    public Permission p;

    public Grants g;

    public Boolean found;


    public AccessControlFilterTest(Permission p, Grants g, Boolean found) {
        this.p = p;
        this.g = g;
        this.found = found;
    }


    @Test
    public void test() throws Exception {
        String queryField = "f1";
        String permField = "f2";

        Directory d = new RAMDirectory();
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);

        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_47, analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter w = new IndexWriter(d, iwc);

        Document doc = new Document();
        doc.add(new StringField(queryField, queryField, Field.Store.NO));
        doc.add(new StringField(permField, p == null ? "" : p.toString(), Field.Store.NO));
        w.addDocument(doc);
        w.close();

        IndexReader reader = DirectoryReader.open(d);
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser(Version.LUCENE_47, queryField, analyzer);
        Query query = parser.parse(queryField);
        TopDocs docs = searcher.search(query, new AccessControlFilter(permField, g == null ? null : g.getMap()), 100);

        if (found) {
            Assert.assertEquals(1, docs.scoreDocs.length);
            Assert.assertEquals(0, docs.scoreDocs[0].doc);
        } else {
            Assert.assertEquals(0, docs.totalHits);
        }

        reader.close();
        d.close();
    }

}
