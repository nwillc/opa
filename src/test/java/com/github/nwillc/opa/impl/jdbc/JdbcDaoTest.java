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

package com.github.nwillc.opa.impl.jdbc;

import com.github.nwillc.funjdbc.DbAccessor;
import com.github.nwillc.funjdbc.SqlStatement;
import com.github.nwillc.funjdbc.functions.Extractor;
import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.SqlTestDatabase;
import com.github.nwillc.opa.junit.AbstractDaoTest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcDaoTest extends AbstractDaoTest {

    @Override
    public Dao<String, TestEntity> get() {
        return getDao();
    }

    public static Dao<String, TestEntity> getDao() {
        try {
            return new JdbcDao<>(new TestDbConfiguration());
        } catch (Exception e) {
            throw new RuntimeException("Could not create db", e);
        }
    }

    private static class TestDbConfiguration extends SqlTestDatabase implements JdbcDaoConfiguration<String, TestEntity> {
        public TestDbConfiguration() throws ClassNotFoundException, SQLException {
        }

        @Override
        public Extractor<TestEntity> getExtractor() {
            return rs -> new TestEntity(rs.getString("key"), rs.getString("value"));
        }

        @Override
        public SqlStatement getQueryAll() {
            return new SqlStatement("SELECT DISTINCT * FROM TestEntity");
        }

        @Override
        public SqlEntry<TestEntity> getCreate() {
            return testEntity -> new SqlStatement("INSERT INTO TestEntity (key, value) VALUES ('%s', '%s')",
                    testEntity.getKey(), testEntity.getValue());
        }

        @Override
        public SqlEntry<TestEntity> getUpdate() {
            return testEntity -> new SqlStatement("UPDATE TestEntity SET value = '%s' WHERE key = '%s'",
                    testEntity.getValue(), testEntity.getKey());
        }

        @Override
        public SqlEntry<String> getDelete() {
            return s -> new SqlStatement("DELETE FROM TestEntity WHERE key = '%s'", s);
        }
    }
}
