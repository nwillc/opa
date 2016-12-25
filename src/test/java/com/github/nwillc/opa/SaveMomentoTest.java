package com.github.nwillc.opa;

import com.github.nwillc.opa.memory.MemoryBackedDao;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class SaveMomentoTest {
    public static final String KEY = "foo";
    private Dao<String, TestEntity> dao;
    private TestEntity instance;

    @Before
    public void setUp() throws Exception {
        dao = new MemoryBackedDao<>();
        instance = new TestEntity(KEY);
    }

    @Test
    public void rollback() throws Exception {
        assertThat(dao.findOne(instance.getKey()).isPresent()).isFalse();
        final Momento momento = new SaveMomento<>(dao, KEY);
        dao.save(instance);
        assertThat(dao.findOne(instance.getKey()).isPresent()).isTrue();
        momento.rollback();
        assertThat(dao.findOne(KEY).isPresent()).isFalse();
    }

    private class TestEntity extends HasKey<String> {
        public TestEntity(String key) {
            super(key);
        }
    }
}