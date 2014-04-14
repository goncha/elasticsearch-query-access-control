package org.apache.lucene.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Grants {

    Map<String,Object> grants;

    Set<String> dimensionGrant;

    public Grants() {
        grants = new HashMap<String,Object>();
        dimensionGrant = null;
    }

    public Grants allIn(String dimension) {
        dimensionGrant = null;
        grants.put(dimension, true);
        return this;
    }

    public Grants in(String dimension) {
        Object grant = grants.get(dimension);
        if (grant == null) {
            dimensionGrant = new HashSet<String>();
            grant = dimensionGrant;
            grants.put(dimension, grant);
        }

        return this;
    }

    public Grants add(String id) {
        if (dimensionGrant == null) {
            throw new IllegalStateException("please call in() first");
        }
        dimensionGrant.add(id);
        return this;
    }

    public Map<String,Object> getMap() {
        return grants;
    }

}
