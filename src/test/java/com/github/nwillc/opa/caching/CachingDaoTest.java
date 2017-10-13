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

package com.github.nwillc.opa.caching;


import com.github.nwillc.opa.CachingDao;
import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.memory.MemoryBackedDao;
import com.github.nwillc.opa.test.DaoTest;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CachingDaoTest extends DaoTest {
    private Dao<String, TestEntity> backingDao;
    private Dao<String, TestEntity> cachingDao;

    @Override
    @Before
    public void setUp() {
        backingDao = spy(new MemoryBackedDao<>());
        cachingDao = new CachingDao<>(backingDao);
        super.setUp();
    }

    @Override
    public Dao<String, TestEntity> get() {
        return cachingDao;
    }

    @Test
    public void testFindAllCaches() throws Exception {
        backingDao.save(new TestEntity("1", "one"));
        assertThat(cachingDao.findAll().count()).isEqualTo(1);
        assertThat(cachingDao.findOne("1").get().getKey()).isEqualTo("1");
        verify(backingDao, times(0)).findOne("1");
    }

    @Test
    public void testFindOneCaches() throws Exception {
        backingDao.save(new TestEntity("1", "one"));
        assertThat(cachingDao.findOne("1").get().getKey()).isEqualTo("1");
        verify(backingDao, times(1)).findOne("1");
        assertThat(cachingDao.findOne("1").get().getKey()).isEqualTo("1");
        verify(backingDao, times(1)).findOne("1");
    }
}