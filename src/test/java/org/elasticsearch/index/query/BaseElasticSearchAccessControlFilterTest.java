package org.elasticsearch.index.query;

import org.apache.lucene.search.BaseAccessControlFilterTest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import java.io.File;
import java.io.IOException;

public abstract class BaseElasticSearchAccessControlFilterTest extends BaseAccessControlFilterTest {

    static final String DATA_DIRECTORY = "es-data";

    static final String CLUSTER_NAME = "elasticsearch";

    static final String INDEX_TYPE = "line";

    static final String INDEX_NAME = "bigdata";

    Node masterNode;
    Node clientNode;


    BulkRequestBuilder bulkRequestBuilder;

    @Override
    protected void indexDoc(String id, String content, String perm) throws IOException {
        bulkRequestBuilder.add(Requests.indexRequest(INDEX_NAME).type(INDEX_TYPE).id(id)
                .source("id", id, "content", content, "perm", perm));
    }

    @Override
    protected int indexBulk(int multiply) throws IOException {
        bulkRequestBuilder = clientNode.client().prepareBulk();
        int count = super.indexBulk(multiply);
        bulkRequestBuilder.execute().actionGet();
        return count;
    }

    protected boolean isMemoryStore() { return true; }

    @Override
    protected void beforeIndexing() {
        super.beforeIndexing();

        IndicesAdminClient adminClient = clientNode.client().admin().indices();

        // disable refreshing for bulk indexing when creating index
        ImmutableSettings.Builder settingsBuilder = ImmutableSettings.builder()
                .put("index.refresh_interval", "-1")
                .put("index.translog.disable_flush", true);

        if (isMemoryStore())
            settingsBuilder.put("index.store.type", "memory");

        // create index first before setting index settings
        adminClient.prepareCreate(INDEX_NAME).addMapping(INDEX_TYPE,
                "id", "type=string,store=true,index=no",
                "content", "type=string,store=true",
                "perm", "type=string,store=false,index=not_analyzed,include_in_all=false",
                "_source", "enabled=false")
                .setSettings(settingsBuilder)
                .execute().actionGet();
    }

    @Override
    protected void afterIndexing() {
        super.afterIndexing();

        IndicesAdminClient adminClient = clientNode.client().admin().indices();

        // force a refreshing after bulk indexing
        adminClient.prepareRefresh(INDEX_NAME).execute().actionGet();

        // enable refreshing after bulk indexing
        Settings.Builder settingsBuilder = ImmutableSettings.builder()
                .put("index.refresh_interval", "1s")
                .put("index.translog.disable_flush", false);
        adminClient.prepareUpdateSettings(INDEX_NAME).setSettings(settingsBuilder).execute().actionGet();
    }

    void setUpMasterNode() {
        NodeBuilder builder = NodeBuilder.nodeBuilder();
        builder.clusterName(CLUSTER_NAME).data(true).local(true);
        builder.settings().put("path.data", DATA_DIRECTORY);
        builder.settings().put("http.enabled", false);
        masterNode = builder.build();
        masterNode.start();
    }

    void setUpClientNode() {
        NodeBuilder builder = NodeBuilder.nodeBuilder();
        builder.clusterName(CLUSTER_NAME).data(false).local(true).client(true);
        clientNode = builder.build();
        clientNode.start();
    }

    void tearDownNodes() {
        clientNode.stop();
        masterNode.stop();
    }

}
