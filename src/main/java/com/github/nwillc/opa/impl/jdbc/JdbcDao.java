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

import com.github.nwillc.funjdbc.SqlStatement;
import com.github.nwillc.funjdbc.UncheckedSQLException;
import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.HasKey;
import com.github.nwillc.opa.query.Query;

import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A DAO implementation employing a JDBC persistence implementation.
 *
 * @param <K> The entity key type
 * @param <T> The entity type
 */
public class JdbcDao<K, T extends HasKey<K>> implements Dao<K, T> {
    private final JdbcDaoConfiguration<K, T> configuration;

    public JdbcDao(JdbcDaoConfiguration<K, T> configuration) {
        this.configuration = configuration;
    }

    public JdbcDaoConfiguration<K, T> getConfiguration() {
        return configuration;
    }

    @Override
    public Optional<T> findOne(K key) {
        try {
            return configuration.dbFind(configuration.getExtractor(), configuration.getRetrieve().apply(key));
        } catch (SQLException e) {
            throw new UncheckedSQLException("Find failed", e);
        }
    }

    @Override
    public Stream<T> findAll() {
        try {
            return configuration.dbQuery(configuration.getExtractor(), configuration.getQueryAll());
        } catch (SQLException e) {
            throw new UncheckedSQLException("findAll failed", e);
        }
    }

    @Override
    public Stream<T> find(Query<T> query) {
        final JdbcQueryMapper<T> mapper = new JdbcQueryMapper<>();
        try {
            final SqlStatement sqlStatement = new SqlStatement("%s WHERE %s", configuration.getQueryAll().toString(), query.apply(mapper).toString());
            return configuration.dbQuery(configuration.getExtractor(), sqlStatement);
        } catch (Exception e) {
            throw new UncheckedSQLException("find failed", e);
        }
    }

    @Override
    public void save(T entity) {
        final Optional<T> found = findOne(entity.getKey());
        final SqlStatement sqlStatement;

        if (found.isPresent()) {
            sqlStatement = configuration.getUpdate().apply(entity);
        } else {
            sqlStatement = configuration.getCreate().apply(entity);
        }

        try {
            configuration.dbUpdate(sqlStatement);
        } catch (SQLException e) {
            throw new UncheckedSQLException("Save failed", e);
        }
    }

    @Override
    public void delete(K key) {
        try {
            configuration.dbUpdate(configuration.getDelete().apply(key));
        } catch (SQLException e) {
            throw new UncheckedSQLException("Delete failed", e);
        }
    }
}
