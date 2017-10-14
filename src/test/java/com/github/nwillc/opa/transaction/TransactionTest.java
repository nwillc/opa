/*
 * Copyright 2017 nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without
 * fee is hereby granted, provided that the above copyright notice and this permission notice appear
 * in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT,
 * OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE,
 * DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS
 * ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.github.nwillc.opa.transaction;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionTest {
    @Test
    public void testDefaultMethods() throws Exception {
        Transaction transaction = new Transaction() {
        };

        transaction.commit();
        transaction.rollback();
        transaction.add(null);
    }

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