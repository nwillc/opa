package com.github.nwillc.opa.transaction;


public interface Transaction extends AutoCloseable {
    default void commit() {}
    default void rollback() {}
    default void add(Memento memento) {}

    @Override
    default void close() throws Exception {
        this.rollback();
    }
}
