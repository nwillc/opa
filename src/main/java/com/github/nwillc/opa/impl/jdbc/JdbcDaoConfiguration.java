/*
 * Copyright 2017 nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.github.nwillc.opa.impl.jdbc;

import com.github.nwillc.funjdbc.DbAccessor;
import com.github.nwillc.funjdbc.SqlStatement;
import com.github.nwillc.funjdbc.functions.Extractor;
import com.github.nwillc.opa.HasKey;


/**
 * The SQL bits needed by the DAO.  This provides the SQL statements, and the code needed to perform them.
 *
 * @param <K> The entites key type
 * @param <T> The entity type
 * @since 0.6.2
 */
public interface JdbcDaoConfiguration<K, T extends HasKey<K>> extends DbAccessor {
    /**
     * Get an extractor that will create and entity from a result set.
     *
     * @return extractor.
     */
    Extractor<T> getExtractor();

    /**
     * The SQL to query all entity instances.
     *
     * @return the SQL
     */
    SqlStatement getQueryAll();

    /**
     * Given an entity return the SQL to insert it.
     *
     * @return the sql entry
     */
    SqlEntry<T> getCreate();

    /**
     * Given a key, return the SQL to retrieve the entity with the key.
     *
     * @return the sql entry
     */
    default SqlEntry<K> getRetrieve() {
        return key -> new SqlStatement(getQueryAll() + " WHERE key = '%s'", key);
    }

    /**
     * Given an entity return the SQL to update it.
     *
     * @return the sql entry
     */
    SqlEntry<T> getUpdate();

    /**
     * Given a key return the SQL to delete it.
     *
     * @return the ssql entry
     */
    SqlEntry<K> getDelete();
}
