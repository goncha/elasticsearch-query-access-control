package org.apache.lucene.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Grants {

    Map<String,Object> grants;

    Object dimensionGrant;

    public Grants() {
        grants = new HashMap<String,Object>();
        dimensionGrant = null;
    }

    public Grants allIn(String dimension) {
        dimensionGrant = null;
        grants.put(dimension, Boolean.TRUE);
        return this;
    }

    public Grants in(String dimension) {
        Object grant = grants.get(dimension);
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

        if (dimensionGrant != Boolean.TRUE) {
            ((Set<String>) dimensionGrant).add(id);
        }
        return this;
    }

    public Grants remove(String id) {
        if (dimensionGrant == null) {
            throw new IllegalStateException("please call in() first");
        }

        if (dimensionGrant != Boolean.TRUE) {
            ((Set<String>) dimensionGrant).remove(id);
        }
        return this;
    }

    public Map<String,Object> getMap() {
        return grants;
    }

}
