package com.github.nwillc.opa;


public interface Transaction extends Momento, AutoCloseable {
    @Override
    default void close() throws Exception {
        this.rollback();
    }
}
