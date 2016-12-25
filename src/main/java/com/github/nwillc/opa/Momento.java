package com.github.nwillc.opa;

public interface Momento {
    default void commit() {};
    void rollback();
}
