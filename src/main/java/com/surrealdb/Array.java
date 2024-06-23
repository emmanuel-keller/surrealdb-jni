package com.surrealdb;

import java.util.Iterator;

public class Array extends Native implements Iterable<Value> {


    Array(long ptr) {
        super(ptr);
    }

    private static native String toString(long ptr);

    private static native String toPrettyString(long ptr);

    private static native long get(long id, int idx);

    private static native int len(long ptr);

    private static native long iterator(long ptr);

    private static native long synchronizedIterator(long ptr);

    public String toString() {
        return toString(getPtr());
    }

    public String toPrettyString() {
        return toPrettyString(getPtr());
    }

    public Value get(int idx) {
        return new Value(get(getPtr(), idx));
    }

    public int len() {
        return len(getPtr());
    }

    final protected native boolean deleteInstance(long ptr);

    @Override
    public Iterator<Value> iterator() {
        return new ValueIterator(iterator(getPtr()));
    }

    public SynchronizedValueIterator synchronizedIterator() {
        return new SynchronizedValueIterator(synchronizedIterator(getPtr()));
    }
}

