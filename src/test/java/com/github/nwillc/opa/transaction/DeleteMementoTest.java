package com.github.nwillc.opa.transaction;

import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.HasKey;
import com.github.nwillc.opa.memory.MemoryBackedDao;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteMementoTest {
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
        dao.save(instance);
        assertThat(dao.findOne(instance.getKey()).isPresent()).isTrue();
        final Memento memento = new DeleteMemento<>(dao, KEY);
        dao.delete(KEY);
        assertThat(dao.findOne(instance.getKey()).isPresent()).isFalse();
        memento.rollback();
        assertThat(dao.findOne(instance.getKey()).isPresent()).isTrue();
    }

    private class TestEntity extends HasKey<String> {
        public TestEntity(String key) {
            super(key);
        }
    }
}