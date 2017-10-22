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

package com.github.nwillc.opa.impl.memory;

import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.HasKey;
import com.github.nwillc.opa.query.Query;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MemoryBackedDao<K, T extends HasKey<K>> implements Dao<K, T> {
    private final Map<K, T> entities = new ConcurrentHashMap<>();

    @Override
    public void delete(final K key) {
        entities.remove(key);
    }

    @Override
    public Optional<T> findOne(final K s) {
        return Optional.ofNullable(entities.get(s));
    }

    @Override
    public Stream<T> findAll() {
        return entities.values().stream();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Stream<T> find(final Query<T> query) {
        final MemoryQueryMapper<T> mapper = new MemoryQueryMapper<>();
        return findAll().filter((Predicate<T>) query.apply(mapper));
    }

    @Override
    public void save(final T t) {
        entities.put(t.getKey(), t);
    }
}
