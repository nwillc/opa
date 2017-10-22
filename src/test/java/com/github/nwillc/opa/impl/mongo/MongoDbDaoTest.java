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

package com.github.nwillc.opa.impl.mongo;

import com.github.fakemongo.junit.FongoRule;
import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.junit.AbstractDaoTest;
import org.junit.Rule;

public class MongoDbDaoTest extends AbstractDaoTest {
    @Rule
    public FongoRule fongoRule = new FongoRule();

    @Override
    public Dao<String, TestEntity> get() {
        return new MongoDbDao<>(fongoRule.getMongoClient(), "testdb", TestEntity.class);
    }
}