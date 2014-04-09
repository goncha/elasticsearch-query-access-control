package org.apache.lucene.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Grants {

    Map<String,Set<String>> grants;

    Set<String> dimensionGrant;

    public Grants() {
        grants = new HashMap<String,Set<String>>();
        dimensionGrant = null;
    }

    public Grants in(String dimension) {
        Set<String> grant = grants.get(dimension);
        if (grant == null) {
            grant = new HashSet<String>();
            grants.put(dimension, grant);
        }
        dimensionGrant = grant;
        return this;
    }

    public Grants add(String id) {
        if (dimensionGrant == null) {
            throw new IllegalStateException("please call in() first");
        }
        dimensionGrant.add(id);
        return this;
    }

    public Map<String,Set<String>> getMap() {
        return grants;
    }

}
