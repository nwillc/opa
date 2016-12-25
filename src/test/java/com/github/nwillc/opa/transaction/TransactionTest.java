package com.github.nwillc.opa.transaction;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionTest {
    @Test
    public void close() throws Exception {
        T2 transaction1;
        try (T2 transaction = new T2()) {
            transaction1 = transaction;
        }

        assertThat(transaction1.rollback).isTrue();
        assertThat(transaction1.commit).isFalse();
    }

    private class T2 implements Transaction {
        boolean rollback = false;
        boolean commit = false;

        @Override
        public void commit() {
            commit = true;
        }

        @Override
        public void rollback() {
            rollback = true;
        }
    }
}