package com.surrealdb;

import java.util.Iterator;

public class EntryIterator extends Native implements Iterator<Entry> {

    EntryIterator(long ptr) {
        super(ptr);
    }

    private static native boolean hasNext(long ptr);

    private static native long next(long ptr);

    final protected native boolean deleteInstance(long ptr);

    @Override
    public boolean hasNext() {
        return hasNext(getPtr());
    }

    @Override
    public Entry next() {
        return new Entry(next(getPtr()));
    }
}
