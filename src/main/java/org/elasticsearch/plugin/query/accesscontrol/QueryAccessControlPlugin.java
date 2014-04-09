package org.elasticsearch.plugin.query.accesscontrol;


import org.elasticsearch.index.query.AccessControlFilterParser;
import org.elasticsearch.indices.query.IndicesQueriesModule;
import org.elasticsearch.plugins.AbstractPlugin;

public class QueryAccessControlPlugin extends AbstractPlugin {

    @Override
    public String name() {
        return "query-access-control";
    }

    @Override
    public String description() {
        return "Access control filter support";
    }

    public void onModule(IndicesQueriesModule module) {
        module.addFilter(new AccessControlFilterParser());
    }

}
