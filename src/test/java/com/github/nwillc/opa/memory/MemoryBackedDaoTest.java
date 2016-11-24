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

package com.github.nwillc.opa.memory;

import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.HasKey;
import com.github.nwillc.opa.query.QueryGenerator;
import org.junit.Test;

import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class MemoryBackedDaoTest {
    private Dao<String, TestEntity> testEntityDao = new MemoryBackedDao<>();

    @Test
    public void shouldDelete() throws Exception {
        testEntityDao.save(new TestEntity("foo"));
        assertThat(testEntityDao.findOne("foo").get().getKey()).isEqualTo("foo");
        testEntityDao.delete("foo");
        testEntityDao.findOne("foo").ifPresent(one -> fail("Entity foo still present"));
    }

    @Test
    public void shouldFindAll() throws Exception {
        testEntityDao.save(new TestEntity("foo"));
        testEntityDao.save(new TestEntity("bar"));
        assertThat(testEntityDao.findAll().map(TestEntity::getKey).collect(Collectors.toList())).contains("foo", "bar");
    }

	@Test
	public void shouldFindFilter() throws Exception {
		final TestEntity foo = new TestEntity("foo");
		testEntityDao.save(foo);
		testEntityDao.save(new TestEntity("bar"));
        QueryGenerator<TestEntity> generator = new QueryGenerator<>(TestEntity.class);
        generator.eq("key", "foo");
		assertThat(testEntityDao.find(generator.getFilter()).collect(Collectors.toList())).containsExactly(foo);
	}

    @Test
    public void shouldSaveAndFindOne() throws Exception {
        testEntityDao.findOne("foo").ifPresent(one -> fail("Entity foo already present"));
        testEntityDao.save(new TestEntity("foo"));
        assertThat(testEntityDao.findOne("foo").get().getKey()).isEqualTo("foo");
    }

    private class TestEntity extends HasKey<String> {
        private final String key;

        public TestEntity(String key) {
            super(UUID.randomUUID().toString());
            this.key = key;
        }

        @Override
        public String getKey() {
            return key;
        }
    }
}