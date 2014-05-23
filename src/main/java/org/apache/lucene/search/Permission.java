package org.apache.lucene.search;

import java.util.*;

public class Permission {

    Map<String,Set<String>> permission = new TreeMap<String,Set<String>>();

    Set<String> dimensionSet = null;

    public Permission in(String dimension) {
        Set<String> set = permission.get(dimension);
        if (set == null) {
            set = new TreeSet<String>();
            permission.put(dimension, set);
        }
        dimensionSet = set;
        return this;
    }

    public Permission add(String value) {
        if (dimensionSet == null) {
            throw new IllegalStateException("call in() first");
        }
        dimensionSet.add(value);
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String,Set<String>>> it = permission.entrySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            if (i++ > 0) sb.append(";");
            Map.Entry<String,Set<String>> entry = it.next();
            sb.append(entry.getKey()).append("=");

            int ii = 0;
            Iterator<String> setIt = entry.getValue().iterator();
            while (setIt.hasNext()) {
                if (ii++ > 0) sb.append(",");
                sb.append(setIt.next());
            }
        }
        return sb.toString();
    }


    public void fromString(String str) {
        if (str == null) return;

        int p = 0, end = str.length();
        for (int i = 0; i < end; i++) {
            char ch = str.charAt(i);
            if ('=' == ch) {
                in(str.substring(p, i));
                p = i + 1;
            } else if (',' == ch) {
                if (p < i) add(str.substring(p, i));
                p = i + 1;
            } else  if (';' == ch) {
                if (p < i) add(str.substring(p, i));
                p = i + 1;
                dimensionSet = null;
            }
        }

        if (p < end) {
            add(str.substring(p, end));
        }
    }


    public Map<String,Set<String>> getMap() {
        return permission;
    }
}
