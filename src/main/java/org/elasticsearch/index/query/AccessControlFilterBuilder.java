package org.elasticsearch.index.query;

import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class AccessControlFilterBuilder extends BaseFilterBuilder {

    private final String field;

    private final Map<String,Object> grants;

    /**
     * @param field Lucene field name for access control
     * @param grants a java.util.Map of String key and (Boolean or java.util.Set) value
     */
    public AccessControlFilterBuilder(String field, Map<String,Object> grants) {
        this.field = field;
        this.grants = grants;
    }


    @Override
    protected void doXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(AccessControlFilterParser.NAME);
        builder.field("field", field);
        builder.field("grants", grants);
        builder.endObject();
    }
}
