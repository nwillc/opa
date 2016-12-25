/*
 * Copyright (c) 2016, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.github.nwillc.opa.memory;

import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.transaction.MementoTransaction;
import com.github.nwillc.opa.transaction.Transaction;
import com.github.nwillc.opa_impl_tests.DaoTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryBackedDaoTest extends DaoTest {
    @Override
    public Dao<String, TestEntity> get() {
        return new MemoryBackedDao<>();
    }

    @Test
    public void testTransactionalDelete() throws Exception {
        Transaction transaction = new MementoTransaction();
        TestEntity testEntity = new TestEntity("foo");
        final Dao<String, TestEntity> dao = get();

        dao.save(testEntity);
        dao.delete(testEntity.getKey(), transaction);
        assertThat(dao.findOne(testEntity.getKey()).isPresent()).isFalse();
        transaction.rollback();
        assertThat(dao.findOne(testEntity.getKey()).isPresent()).isTrue();
    }

    @Test
    public void testTransactionalSave() throws Exception {
        Transaction transaction = new MementoTransaction();
        TestEntity testEntity = new TestEntity("foo");
        final Dao<String, TestEntity> dao = get();

        assertThat(dao.findOne(testEntity.getKey()).isPresent()).isFalse();
        dao.save(testEntity, transaction);
        assertThat(dao.findOne(testEntity.getKey()).isPresent()).isTrue();
        transaction.rollback();
        assertThat(dao.findOne(testEntity.getKey()).isPresent()).isFalse();
    }

    @Test
    public void testTransactionalUpdate() throws Exception {
        Transaction transaction = new MementoTransaction();
        TestEntity testEntity = new TestEntity("foo", "bar");
        final Dao<String, TestEntity> dao = get();

        dao.save(testEntity);
        assertThat(dao.findOne(testEntity.getKey()).get().getValue()).isEqualTo("bar");
        TestEntity testEntity2 = new TestEntity("foo", "baz");

        dao.save(testEntity2, transaction);
        assertThat(dao.findOne(testEntity.getKey()).get().getValue()).isEqualTo("baz");

        transaction.rollback();
        assertThat(dao.findOne(testEntity.getKey()).get().getValue()).isEqualTo("bar");
    }
}