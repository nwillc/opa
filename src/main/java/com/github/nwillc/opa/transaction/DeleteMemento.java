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

package com.github.nwillc.opa.transaction;

import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.HasKey;

import java.util.Optional;

/**
 * A memento of a delete.
 *
 * @param <K> The type of the keys.
 * @param <T> The type of the entity
 * @param <R> The implementation specific query representation.
 */
public class DeleteMemento<K, T extends HasKey<K>, R> extends Memento<K, T, R> {
    private final Optional<T> element;

    public DeleteMemento(Dao<K, T, R> dao, K key) {
        super(dao, key);
        element = dao.findOne(key);
    }

    @Override
    public void rollback() {
        element.ifPresent(getDao()::save);
    }
}
