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

package com.github.nwillc.opa.test;

import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.query.Query;
import com.github.nwillc.opa.query.QueryBuilder;
import com.github.nwillc.opa.test.DaoTest.TestEntity;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class QueryMapperTest<R> implements DaoSupplier<R> {
    private Dao<String, TestEntity, R> dao;

    @Override
    public abstract Dao<String, TestEntity, R> get();

    @Before
    public void setUp() {
        dao = get();
        assertThat(dao).isNotNull();
    }

    @Test
    public void testEq() throws Exception {
        QueryBuilder<TestEntity, R> generator = new QueryBuilder<TestEntity, R>(TestEntity.class).eq("value", "1");

        TestEntity testEntity = new TestEntity("key", "1");

        Query<TestEntity, R> query = generator.build();
        assertThat(dao.find(query).count()).isEqualTo(0);
        dao.save(testEntity);
        assertThat(dao.find(query).count()).isEqualTo(1);
        testEntity.setValue("2");
        dao.save(testEntity);
        assertThat(dao.find(query).count()).isEqualTo(0);
    }

    @Test
    public void testContains() throws Exception {
        QueryBuilder<TestEntity, R> generator = new QueryBuilder<TestEntity, R>(TestEntity.class).contains("value", "1");

        TestEntity testEntity = new TestEntity("key", "1 2 3");

        Query<TestEntity, R> query = generator.build();
        assertThat(dao.find(query).count()).isEqualTo(0);
        dao.save(testEntity);
        assertThat(dao.find(query).count()).isEqualTo(1);
        testEntity.setValue("2");
        dao.save(testEntity);
        assertThat(dao.find(query).count()).isEqualTo(0);
    }

    @Test
    public void testNot() throws Exception {
        QueryBuilder<TestEntity, R> generator = new QueryBuilder<TestEntity, R>(TestEntity.class).eq("value", "1").not();

        TestEntity testEntity = new TestEntity("key", "1");

        Query<TestEntity, R> query = generator.build();
        assertThat(dao.find(query).count()).isEqualTo(0);
        dao.save(testEntity);
        assertThat(dao.find(query).count()).isEqualTo(0);
        testEntity.setValue("2");
        dao.save(testEntity);
        assertThat(dao.find(query).count()).isEqualTo(1);
    }

    @Test
    public void testAnd() throws Exception {
        QueryBuilder<TestEntity, R> generator = new QueryBuilder<TestEntity, R>(TestEntity.class)
                .eq("key", "1").eq("value", "1").and();

        TestEntity testEntity = new TestEntity("1", "1");
        Query<TestEntity, R> query = generator.build();
        assertThat(dao.find(query).count()).isEqualTo(0);
        dao.save(testEntity);
        assertThat(dao.find(query).count()).isEqualTo(1);
        testEntity.setValue("2");
        dao.save(testEntity);
        assertThat(dao.find(query).count()).isEqualTo(0);
        dao.delete("1");
        testEntity.setValue("1");
        testEntity.setKey("2");
        dao.save(testEntity);
        assertThat(dao.find(query).count()).isEqualTo(0);
    }

    @Test
    public void testOr() throws Exception {
        QueryBuilder<TestEntity, R> generator = new QueryBuilder<TestEntity, R>(TestEntity.class)
                .eq("key", "1").eq("value", "1").or();

        TestEntity testEntity = new TestEntity("1", "1");
        Query<TestEntity, R> query = generator.build();
        assertThat(dao.find(query).count()).isEqualTo(0);
        dao.save(testEntity);
        assertThat(dao.find(query).count()).isEqualTo(1);
        testEntity.setValue("2");
        dao.save(testEntity);
        assertThat(dao.find(query).count()).isEqualTo(1);
        dao.delete("1");
        testEntity.setValue("1");
        testEntity.setKey("2");
        dao.save(testEntity);
        assertThat(dao.find(query).count()).isEqualTo(1);
    }
}
