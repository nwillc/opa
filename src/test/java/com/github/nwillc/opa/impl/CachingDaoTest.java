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

package com.github.nwillc.opa.impl;


import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.impl.memory.MemoryBackedDao;
import com.github.nwillc.opa.junit.AbstractDaoTest;
import mockit.Expectations;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JMockit.class)
public class CachingDaoTest extends AbstractDaoTest {
    private Dao<String, TestEntity> backingDao;
    private Dao<String, TestEntity> cachingDao;

    @Override
    @Before
    public void setUp() {
        backingDao = new MemoryBackedDao<>();
        cachingDao = new CachingDao<>(backingDao);
        super.setUp();
    }

    @Override
    public Dao<String, TestEntity> get() {
        return cachingDao;
    }

    @Test
    public void testFindAllCaches() throws Exception {
        new Expectations(MemoryBackedDao.class){{
             backingDao.findOne(anyString); times = 0;
        }};
        backingDao.save(new TestEntity("1", "one"));
        assertThat(cachingDao.findAll().count()).isEqualTo(1);
        assertThat(cachingDao.findOne("1").get().getKey()).isEqualTo("1");
    }

    @Test
    public void testFindOneCaches() throws Exception {
        new Expectations(MemoryBackedDao.class){{
            backingDao.findOne("1"); times = 1; result = Optional.of(new TestEntity("1", "one"));
        }};
        assertThat(cachingDao.findOne("1").get().getKey()).isEqualTo("1");
        new Expectations(MemoryBackedDao.class){{
            backingDao.findOne("1"); times = 0;
        }};
        assertThat(cachingDao.findOne("1").get().getKey()).isEqualTo("1");
    }
}