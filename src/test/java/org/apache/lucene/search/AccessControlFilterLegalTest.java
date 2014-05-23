package org.apache.lucene.search;


import org.junit.Test;

public class AccessControlFilterLegalTest {

    @Test(expected=IllegalArgumentException.class)
    public void testNullFieldName() {
        new AccessControlFilter(null);
    }

}
