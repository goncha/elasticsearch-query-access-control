package org.apache.lucene.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class LuceneAccessControlFilterTest extends BaseAccessControlFilterTest {

    Directory indexDirectory;

    Analyzer analyzer;

    IndexWriter indexWriter;

    DirectoryReader directoryReader;

    IndexSearcher indexSearcher;

    QueryParser queryParser;

    @Override
    protected void indexDoc(String id, String content, String perm) throws IOException {
        Document doc = new Document();

        doc.add(new StringField("id", id, Field.Store.YES));
        doc.add(new TextField("content", content, Field.Store.NO));
        doc.add(new StringField("perm", perm, Field.Store.NO));

        indexWriter.addDocument(doc);
    }

    void setUpLuceneIndexer() throws IOException {
        indexDirectory = new RAMDirectory();

        analyzer = new StandardAnalyzer(Version.LUCENE_47);

        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_47, analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);

    }

    protected void setUpLuceneSearcher() throws IOException {
        directoryReader = DirectoryReader.open(indexDirectory);
        indexSearcher = new IndexSearcher(directoryReader);
        queryParser = new QueryParser(Version.LUCENE_47, "content", analyzer);
    }

    protected void tearDownLuceneIndexer() throws IOException {
        indexWriter.close();
    }


    @Test
    public void searchWithoutAccessControl() throws Exception {
        setUpLuceneIndexer();
        index(1);
        tearDownLuceneIndexer();

        setUpLuceneSearcher();

        Query query = queryParser.parse("request");
        ScoreDoc[] hits = indexSearcher.search(query, null, Integer.MAX_VALUE).scoreDocs;
        Assert.assertEquals(416, hits.length);
    }


    @Test
    public void searchWithAccessControlThenReturnsOneHit() throws Exception {
        setUpLuceneIndexer();
        index(1);
        tearDownLuceneIndexer();

        setUpLuceneSearcher();

        Grants grants = new Grants();
        grants
                .in("A").add("0")
                .in("B").add("0")
                .in("C").add("0")
                .in("D").add("400");
        Filter filter = new AccessControlFilter("perm", grants.getMap());

        Query query = queryParser.parse("request");
        ScoreDoc[] hits = indexSearcher.search(query, filter, Integer.MAX_VALUE).scoreDocs;
        Assert.assertEquals(1, hits.length);
    }

    @Test
    public void searchWithAccessControlThenReturnsTwoHits() throws Exception {
        setUpLuceneIndexer();
        index(1);
        tearDownLuceneIndexer();


        setUpLuceneSearcher();

        Grants grants = new Grants();
        grants
                .in("A").add("5")
                .in("B").add("105")
                .in("C").add("105")
                .in("D").add("5");
        Filter filter = new AccessControlFilter("perm", grants.getMap());

        Query query = queryParser.parse("request");
        ScoreDoc[] hits = indexSearcher.search(query, filter, Integer.MAX_VALUE).scoreDocs;
        Assert.assertEquals(2, hits.length);
    }

    @Test
    public void searchWithAccessControlThenReturnsThreeHitsByMultiDimensionIds() throws Exception {
        setUpLuceneIndexer();
        index(1);
        tearDownLuceneIndexer();


        setUpLuceneSearcher();

        Grants grants = new Grants();
        grants
                .in("A").add("0").add("1")
                .in("B").add("0").add("1")
                .in("C").add("0").add("1")
                .in("D").add("400").add("800").add("601");
        Filter filter = new AccessControlFilter("perm", grants.getMap());

        Query query = queryParser.parse("request");
        ScoreDoc[] hits = indexSearcher.search(query, filter, Integer.MAX_VALUE).scoreDocs;
        Assert.assertEquals(3, hits.length);
    }
}
