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

/**
 * A memento is designed to be enough context information of a persistence activity
 * to undo it at a later time.
 *
 * @param <K> key type on the entities
 * @param <T> type of the entities
 * @since 0.3.0
 */
public class Memento<K, T extends HasKey<K>, R> implements Transaction {
    private final Dao<K, T, R> dao;
    private final K key;

    Memento(Dao<K, T, R> dao, K key) {
        this.dao = dao;
        this.key = key;
    }

    Dao<K, T, R> getDao() {
        return dao;
    }

    K getKey() {
        return key;
    }
}
