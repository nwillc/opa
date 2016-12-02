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
 *
 */

package com.github.nwillc.opa.caching;

import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.HasKey;
import com.github.nwillc.opa.query.Query;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;


/**
 * A simple cache in front on another dao.
 * @since 0.2.0
 * @param <K>
 * @param <T>
 */
public class CachingDao<K, T extends HasKey<K>> implements Dao<K, T>
{
    private final Dao<K,T> dao;
    private final Map<K,T> map = new ConcurrentHashMap<>();

    public CachingDao(Dao<K, T> dao) {
        this.dao = dao;
    }

    @Override
    public Optional<T> findOne(K key) {
        if (map.containsKey(key)) {
            return Optional.of(map.get(key));
        } else {
            Optional<T> one = dao.findOne(key);
            if (one.isPresent()) {
                map.put(key, one.get());
            }
            return one;
        }
    }

    @Override
    public Stream<T> findAll() {
        return dao.findAll().peek(t -> map.put(t.getKey(), t));
    }

    @Override
    public Stream<T> find(Query<T> query) {
        return dao.find(query).peek(t -> map.put(t.getKey(), t));
    }

    @Override
    public void save(T entity) {
        dao.save(entity);
    }

    @Override
    public void delete(K key) {
        dao.delete(key);
        map.remove(key);
    }
}