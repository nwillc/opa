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

package com.github.nwillc.opa.junit;

import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.HasKey;
import com.github.nwillc.opa.transaction.MementoTransaction;
import com.github.nwillc.opa.transaction.Transaction;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public abstract class AbstractDaoTest implements DaoSupplier {
    private Dao<String, TestEntity> dao;

    @Override
    public abstract Dao<String, TestEntity> get();

    @Before
    public void setUp() {
        dao = get();
        assertThat(dao).isNotNull();
    }

    @Test
    public void shouldSave() throws Exception {
        dao.findOne("foo").ifPresent(one -> fail("Entity foo already present"));
        dao.save(new TestEntity("foo"));
        assertThat(dao.findOne("foo").get().getKey()).isEqualTo("foo");
    }

    @Test
    public void shouldUpdate() throws Exception {
        dao.findOne("foo").ifPresent(one -> fail("Entity foo already present"));
        dao.save(new TestEntity("foo", "bar"));
        Optional<TestEntity> testEntity = dao.findOne("foo");
        assertThat(testEntity.isPresent()).isTrue();
        assertThat(testEntity.get().getValue()).isEqualTo("bar");
        testEntity.get().setValue("baz");
        dao.save(testEntity.get());
        testEntity = dao.findOne("foo");
        assertThat(testEntity.isPresent()).isTrue();
        assertThat(testEntity.get().getValue()).isEqualTo("baz");
    }

    @Test
    public void shouldFindAll() throws Exception {
        dao.save(new TestEntity("foo"));
        dao.save(new TestEntity("bar"));
        assertThat(dao.findAll().map(TestEntity::getKey).collect(Collectors.toList())).contains("foo", "bar");
    }

    @Test
    public void shouldDelete() throws Exception {
        dao.save(new TestEntity("foo"));
        assertThat(dao.findOne("foo").get().getKey()).isEqualTo("foo");
        dao.delete("foo");
        dao.findOne("foo").ifPresent(one -> fail("Entity foo still present"));
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

    public static class TestEntity extends HasKey<String> {
        private String value;

        public TestEntity() {
            this(null, null);
        }

        public TestEntity(String key) {
            this(key, null);
        }

        public TestEntity(String key, String value) {
            super(key);
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
