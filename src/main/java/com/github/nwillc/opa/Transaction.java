package com.github.nwillc.opa;

public interface Transaction extends AutoCloseable {

    void commit();
    void rollback();
    default Integer getId() { return null; }

    @Override
    default void close() throws Exception {
        this.rollback();
    }
}
