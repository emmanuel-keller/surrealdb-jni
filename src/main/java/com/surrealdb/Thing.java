package com.surrealdb;

public class Thing implements AutoCloseable {

    private long id;

    Thing(long id) {
        this.id = id;
    }

    private static native boolean deleteInstance(long id);

    @Override
    public void close() {
        deleteInstance(id);
        id = 0;
    }

    @Override
    @Deprecated
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }
}
