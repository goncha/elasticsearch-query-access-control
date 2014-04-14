package org.apache.lucene.search;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class AllGrantsSet<E> extends AbstractSet<E> implements Serializable {

    public static final AllGrantsSet<String> ALL_GRANTS_SET = new AllGrantsSet<String>();

    private static final long serialVersionUID = 1L;

    private AllGrantsSet() {}

    public Iterator<E> iterator() {
        return new Iterator<E>() {
            public boolean hasNext() {
                return false;
            }
            public E next() {
                throw new NoSuchElementException();
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public int size() {return 0;}

    public boolean contains(Object obj) {return false;}

    // Preserves singleton property
    private Object readResolve() {
        return ALL_GRANTS_SET;
    }

}
