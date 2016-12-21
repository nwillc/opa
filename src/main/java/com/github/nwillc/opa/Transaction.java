package com.github.nwillc.opa;

public interface Transaction extends AutoCloseable {

    void commit();
    void rollback();

    @Override
    default void close() throws Exception {
        this.rollback();
    }
}
