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
import com.github.nwillc.funjdbc.UncheckedSQLException;
import com.github.nwillc.funjdbc.functions.Extractor;
import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.HasKey;
import com.github.nwillc.opa.query.Query;

import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *   A DAO implementation employing a JDBC persistence implementation.
 *   @param <K> The entity key type
 *   @param <T> The entity type
 */
public class JdbcDao<K, T extends HasKey<K>> implements Dao<K, T> {
    private final DbAccessor dao;
    private final SqlEntry<T> saveFormatter;
    private final SqlEntry<T> updateFormatter;
    private final SqlEntry<K> findFormatter;
    private final SqlEntry<K> deleteFormatter;
    private final SqlStatement queryAll;
    private final Extractor<T> extractor;

    public JdbcDao(DbAccessor dao,
                   Extractor<T> extractor, SqlStatement queryAll, SqlEntry<T> saveFormatter, SqlEntry<T> updateFormatter,
                   SqlEntry<K> findFormatter, SqlEntry<K> deleteFormatter) {
        this.dao = dao;
        this.saveFormatter = saveFormatter;
        this.updateFormatter = updateFormatter;
        this.findFormatter = findFormatter;
        this.deleteFormatter = deleteFormatter;
        this.queryAll = queryAll;
        this.extractor = extractor;
    }

    @Override
    public Optional<T> findOne(K key) {
        try {
            return dao.dbFind(extractor, findFormatter.apply(key));
        } catch (SQLException e) {
            throw new UncheckedSQLException("Find failed", e);
        }
    }

    @Override
    public Stream<T> findAll() {
        try {
            return dao.dbQuery(extractor, queryAll);
        } catch (SQLException e) {
            throw new UncheckedSQLException("findAll failed", e);
        }
    }

    @Override
    public Stream<T> find(Query<T> query) {
        final JdbcQueryMapper<T> mapper = new JdbcQueryMapper<>(queryAll.toString());
        try {
            return dao.dbQuery(extractor, (SqlStatement) query.apply(mapper));
        } catch (SQLException e) {
            throw new UncheckedSQLException("find failed", e);
        }
    }

    @Override
    public void save(T entity) {
        final Optional<T> found = findOne(entity.getKey());
        final SqlStatement sqlStatement;

        if (found.isPresent()) {
            sqlStatement = updateFormatter.apply(entity);
        } else {
            sqlStatement = saveFormatter.apply(entity);
        }

        try {
            dao.dbUpdate(sqlStatement);
        } catch (SQLException e) {
            throw new UncheckedSQLException("Save failed", e);
        }
    }

    @Override
    public void delete(K key) {
        try {
            dao.dbUpdate(deleteFormatter.apply(key));
        } catch (SQLException e) {
            throw new UncheckedSQLException("Delete failed", e);
        }
    }
}
