package org.elasticsearch.index.query;

import org.apache.lucene.search.BaseAccessControlFilterTest;
import org.apache.lucene.search.Grants;
import org.apache.lucene.util.ArrayUtil;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import java.io.IOException;

public abstract class BaseElasticSearchAccessControlFilterTest extends BaseAccessControlFilterTest {

    static final String DATA_DIRECTORY = "es-data";

    static final String CLUSTER_NAME = "elasticsearch";

    static final String INDEX_TYPE = "line";

    static final String INDEX_NAME = "bigdata";

    static final int MAX_SIZE = 10 * 1000 * 1000;

    Node node;

    Client client;

    BulkRequestBuilder bulkRequestBuilder;

    @Override
    protected void indexDoc(String id, String content, String perm) throws IOException {
        bulkRequestBuilder.add(Requests.indexRequest(INDEX_NAME).type(INDEX_TYPE).id(id)
                .source("id", id, "content", content, "perm", perm));
    }

    @Override
    protected int indexBulk(int multiply) throws IOException {
        bulkRequestBuilder = client.prepareBulk();
        int count = super.indexBulk(multiply);
        bulkRequestBuilder.execute().actionGet();
        return count;
    }

    protected boolean isMemoryStore() { return true; }

    @Override
    protected void beforeIndexing() {
        super.beforeIndexing();

        IndicesAdminClient adminClient = client.admin().indices();

        // disable refreshing for bulk indexing when creating index
        ImmutableSettings.Builder settingsBuilder = ImmutableSettings.builder()
                .put("index.refresh_interval", "-1")
                .put("index.translog.interval", "60s")
                ;

        if (isMemoryStore())
            settingsBuilder.put("index.store.type", "memory");

        // create index first before setting index settings
        adminClient.prepareCreate(INDEX_NAME).addMapping(INDEX_TYPE,
                "id",       "type=string,store=true,included_in_all=false",
                "content",  "type=string,store=false",
                "perm",     "type=string,store=false,index=not_analyzed,include_in_all=false",
                "_source",  "enabled=false")
                .setSettings(settingsBuilder)
                .execute().actionGet();
    }

    @Override
    protected void afterIndexing() {
        super.afterIndexing();

        IndicesAdminClient adminClient = client.admin().indices();

        // force a refreshing after bulk indexing
        adminClient.prepareRefresh(INDEX_NAME).execute().actionGet();

        // enable refreshing after bulk indexing
        Settings.Builder settingsBuilder = ImmutableSettings.builder()
                .put("index.refresh_interval", "1s");
        adminClient.prepareUpdateSettings(INDEX_NAME).setSettings(settingsBuilder).execute().actionGet();
    }

    void setUpNode() {
        NodeBuilder builder = NodeBuilder.nodeBuilder();
        builder.clusterName(CLUSTER_NAME).local(true);
        builder.settings().put("path.data", DATA_DIRECTORY);
        builder.settings().put("http.enabled", false);
        builder.settings().put("index.number_of_shards", 1);
        builder.settings().put("index.number_of_replicas", 0);
        node = builder.build();
        node.start();
        client = node.client();
    }

    void tearDownNode() {
        client.admin().indices().prepareDelete(INDEX_NAME).execute().actionGet();
        client.close();
    }

    protected int search(Grants grants) {
        SearchRequestBuilder reqBuilder = client.prepareSearch(INDEX_NAME);

        reqBuilder.setQuery(QueryBuilders.termQuery("content", QUERY_KEYWORD))
                .setSize(MAX_SIZE);

        if (grants != null) {
            reqBuilder.setPostFilter(new AccessControlFilterBuilder("perm", grants.getMap()));
        }

        return reqBuilder.execute().actionGet().getHits().getHits().length;
    }

}
