package com.surrealdb;

import java.util.Iterator;

class ClassValueIterator<T> implements Iterator<T> {

    private final ClassValueConverter<T> converter;
    private final Iterator<Value> iterator;

    ClassValueIterator(Class<T> clazz, Iterator<Value> iterator) {
        this.converter = new ClassValueConverter<>(clazz);
        this.iterator = iterator;
    }


    @Override
    final public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    final public T next() {
        final Value value = iterator.next();
        return converter.convertObject(value);
    }
}