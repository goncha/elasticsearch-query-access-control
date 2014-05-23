package org.apache.lucene.search;

import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;

import java.util.*;

public class AccessControlFilter extends TermBytesFilter {

    protected Map<String, Object> grants;

    public AccessControlFilter(String field) {
        this(field, null);
    }

    public AccessControlFilter(String field, Map<String, Object> grants) {
        super(field);
        this.grants = grants;
    }

    @Override
    protected boolean checkBytes(BytesRef ref) {
        if (grants == null) return true;

        String permStr = Term.toString(ref);
        if (permStr == null || permStr.length() == 0) {
            return true;
        }

        Map<String,Set<String>> perm = new Permission().fromString(permStr).getMap();
        return checkPermission(perm);
    }

    protected boolean checkPermission(Map<String, Set<String>> perm) {
        for (Map.Entry<String,Set<String>> permEntry : perm.entrySet()) {
            if (grants.containsKey(permEntry.getKey())) {
                Object grantSettings = grants.get(permEntry.getKey());
                if (grantSettings != null) {
                    if (grantSettings instanceof Boolean) {
                        continue;
                    } else {
                        if (!((Set<String>) grantSettings).containsAll(permEntry.getValue()))
                            return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccessControlFilter)) return false;

        AccessControlFilter that = (AccessControlFilter) o;

        if (field != null ? !field.equals(that.field) : that.field != null) return false;
        if (grants != null ? !grants.equals(that.grants) : that.grants != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int h = 0;
        h += field != null ? field.hashCode() : 0;
        h += grants != null ? grants.hashCode() : 0;
        return h;
    }
}
