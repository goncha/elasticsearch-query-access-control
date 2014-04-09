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

import java.io.IOException;

public class BaseLuceneAccessControlFilterTest extends BaseAccessControlFilterTest {

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

    Directory createIndexDirectory() throws IOException {
        return new RAMDirectory();
    }

    void setUpLuceneIndexer() throws IOException {
        indexDirectory = createIndexDirectory();

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

}
