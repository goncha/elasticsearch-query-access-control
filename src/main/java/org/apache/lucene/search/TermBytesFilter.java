package org.apache.lucene.search;


import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;

import java.io.IOException;

public abstract class TermBytesFilter extends Filter {

    protected String field;

    protected TermBytesFilter(String field) {
        super();
        if (field == null || field.length() == 0)
            throw new IllegalArgumentException();
        this.field = field;
    }

    @Override
    public DocIdSet getDocIdSet(AtomicReaderContext context, final Bits acceptDocs) throws IOException {
        Terms terms = context.reader().terms(field);
        if (terms == null) {
            return null;
        }

        TermsEnum termsEnum = terms.iterator(null);
        BytesRef ref = null;
        if ((ref = termsEnum.next()) != null) {
            FixedBitSet bitSet = null;

            do {
                if (checkBytes(ref)) {
                    if (bitSet == null) bitSet = new FixedBitSet(context.reader().maxDoc());
                    DocsEnum docsEnum = termsEnum.docs(acceptDocs, null, DocsEnum.FLAG_NONE);
                    int docId;
                    while ((docId = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
                        bitSet.set(docId);
                    }
                }
            } while ((ref = termsEnum.next()) != null);

            return bitSet;
        } else {
            return null;
        }
    }

    protected abstract boolean checkBytes(BytesRef ref);

}
