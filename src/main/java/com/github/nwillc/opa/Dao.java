/*
 * Copyright 2018 nwillc@gmail.com
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

package com.github.nwillc.opa;

import com.github.nwillc.opa.query.Query;
import com.github.nwillc.opa.transaction.*;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A simplistic data access object interface. Supporting create/update, deletion and retrieval
 * with query criteria.
 *
 * @param <K> the type of the key for the persisted type
 * @param <T> the type being persisted
 */
public interface Dao<K, T extends HasKey<K>> {

    /**
     * Find an object based on the key.
     *
     * @param key the key to search for
     * @return an optional holding the object if found
     */
    Optional<T> findOne(final K key);

    /**
     * Return a stream of the objects
     *
     * @return stream of objects
     */
    Stream<T> findAll();

    /**
     * Find the objects matching a query.
     *
     * @param query the query to base the search on
     * @return a stream of any objects that match
     */
    Stream<T> find(Query<T> query);

    /**
     * Save the object, create it or update if it exists.
     *
     * @param entity the enity to save or update
     */
    void save(final T entity);

    /**
     * Save entity in a transaction which can be rolled back.
     *
     * @param entity      the entity to save
     * @param transaction the transaction which this is a part of
     * @since 0.3.0
     */
    default void save(final T entity, Transaction transaction) {
        Objects.requireNonNull(transaction);
        Optional<T> stored = findOne(entity.getKey());
        Memento<K, T> memento = stored.isPresent() ?
                new UpdateMemento<>(this, entity.getKey()) :
                new SaveMemento<>(this, entity.getKey());
        save(entity);
        transaction.add(memento);
    }

    /**
     * Delete an based on its key.
     *
     * @param key the key of the object to delete
     */
    void delete(final K key);

    /**
     * Delete entity in a transaction which can be rolled back.
     *
     * @param key         the key of the entity to delete
     * @param transaction the transaction which this is a part of
     * @since 0.3.0
     */
    default void delete(final K key, Transaction transaction) {
        Objects.requireNonNull(transaction);
        Memento<K, T> memento = new DeleteMemento<>(this, key);
        delete(key);
        transaction.add(memento);
    }
}
