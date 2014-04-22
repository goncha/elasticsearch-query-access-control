package org.apache.lucene.search;

import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;

import java.util.*;

public class AccessControlFilter extends TermBytesFilter {

    protected Map<String, Object> grants;

    public AccessControlFilter(String field, Map<String, Object> grants) {
        super(field);
        this.grants = grants;
    }

    @Override
    protected boolean checkBytes(BytesRef ref) {
        String permStr = Term.toString(ref);
        if (permStr == null || permStr.length() == 0) {
            return true;
        }

        Map<String,String> perm = toMap(permStr);
        return checkPermission(perm);
    }

    protected boolean checkPermission(Map<String, String> perm) {
        for (Map.Entry<String,String> permEntry : perm.entrySet()) {
            if (grants.containsKey(permEntry.getKey())) {
                Object grantSettings = grants.get(permEntry.getKey());
                if (grantSettings != null) {
                    if (grantSettings instanceof Boolean) {
                        continue;
                    } else {
                        if (!((Set<String>) grantSettings).contains(permEntry.getValue()))
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


    public static String toString(Map<String,String> perm) {
        StringBuilder sb = new StringBuilder();

        List<String> keys = new ArrayList<String>(perm.keySet());
        Collections.sort(keys);

        int i = 0;
        for(String key : keys) {
            if (i++ > 0) sb.append(",");
            String value = perm.get(key);
            if (value == null) throw new IllegalArgumentException(key + "'s value is null in permission");
            sb.append(key).append("=").append(value);
        }

        return sb.toString();
    }

    public static Map<String,String> toMap(String permStr) {
        Map<String,String> perm = new HashMap<String, String>();

        if (permStr != null && permStr.length() > 0) {
            String[] fields = permStr.split(",");
            if (fields != null && fields.length > 0) {
                for (String field : fields) {
                    addPermEntry(perm, field);
                }
            } else {
                throw new IllegalArgumentException(permStr);
            }
        }

        return perm;
    }

    private static void addPermEntry(Map<String,String> perm, String field) {
        int pos = field.indexOf('=');
        if (pos > 0) {
            perm.put(field.substring(0, pos), field.substring(pos + 1));
        } else {
            throw new IllegalArgumentException(field);
        }
    }

}
