package com.github.nwillc.opa.transaction;

import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.HasKey;
import com.github.nwillc.opa.memory.MemoryBackedDao;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SaveMementoTest {
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
        final Memento memento = new SaveMemento<>(dao, KEY);
        dao.save(instance);
        assertThat(dao.findOne(instance.getKey()).isPresent()).isTrue();
        memento.rollback();
        assertThat(dao.findOne(KEY).isPresent()).isFalse();
    }

    private class TestEntity extends HasKey<String> {
        public TestEntity(String key) {
            super(key);
        }
    }
}