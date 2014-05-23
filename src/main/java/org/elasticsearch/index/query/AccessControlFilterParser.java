package org.elasticsearch.index.query;

import org.apache.lucene.search.AccessControlFilter;
import org.apache.lucene.search.Filter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.xcontent.XContentParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * JSON example of this filter:
 *
 * <pre>
 * "access-control": {
 *     "field": "FIELD_NAME",
 *     "grants": {
 *         "DIMENSION_A": [ ID1, ID2, ID3, ... ],
 *         "DIMENSION_B": [ ID1, ...],
 *         "DIMENSION_C": true,
 *         ...
 *     }
 * }
 * </pre>
 */
public class AccessControlFilterParser implements FilterParser {

    public static final String NAME = "access-control";

    @Inject
    public AccessControlFilterParser() {
    }

    @Override
    public String[] names() {
        return new String[] { NAME };
    }

    @Override
    public Filter parse(QueryParseContext parseContext) throws IOException, QueryParsingException {
        XContentParser parser = parseContext.parser();

        Map<String,Object> grants = null;

        String fieldName = null;

        String currentFieldName = null;
        XContentParser.Token token;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else {
                if ("field".equals(currentFieldName)) {
                    fieldName = parser.text();
                } else if ("grants".equals(currentFieldName)) {
                    if (token == XContentParser.Token.START_OBJECT) {
                        grants = parseGrants(parseContext);
                    } else if (token == XContentParser.Token.VALUE_NULL) {
                        // null value
                    } else {
                        throw new QueryParsingException(parseContext.index(),
                                "[access-control] filter only supports [grants] as object value or null value");
                    }
                } else {
                    throw new QueryParsingException(parseContext.index(),
                            "[access-control] filter does not support [" + currentFieldName + "]");
                }
            }
        }

        if (fieldName == null) {
            throw new QueryParsingException(parseContext.index(), "No field specified for access-control filter");
        }

        return new AccessControlFilter(fieldName, grants);
    }

    protected Map<String,Object> parseGrants(QueryParseContext parseContext)
            throws IOException, QueryParsingException {
        Map<String,Object> grants = new HashMap<String,Object>();

        XContentParser parser = parseContext.parser();

        String currentFieldName = null;
        Set<String> currentGrant = null;
        XContentParser.Token token;

        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else {
                if (token == XContentParser.Token.START_ARRAY) {
                    currentGrant = new HashSet<String>();
                    while ((token = parser.nextToken()) != XContentParser.Token.END_ARRAY) {
                        if (token == XContentParser.Token.VALUE_STRING) {
                            currentGrant.add(parser.text());
                        } else {
                            throw new QueryParsingException(parseContext.index(),
                                    "[access-control] filter only supports array of string or boolean true as object value");
                        }
                    }
                    grants.put(currentFieldName, currentGrant);
                } else if (token == XContentParser.Token.VALUE_BOOLEAN) {
                    boolean val = parser.booleanValue();
                    if (val) {
                        grants.put(currentFieldName, val);
                    } else {
                        throw new QueryParsingException(parseContext.index(),
                                "[access-control] filter only supports array of string or boolean true as object value");
                    }
                } else {
                    throw new QueryParsingException(parseContext.index(),
                            "[access-control] filter only supports array of string or boolean true as object value");
                }
            }
        }

        return grants;
    }

}
