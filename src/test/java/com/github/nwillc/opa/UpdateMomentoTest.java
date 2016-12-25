package com.github.nwillc.opa;

import com.github.nwillc.opa.memory.MemoryBackedDao;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateMomentoTest {
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
        instance.value = "bar";
        dao.save(instance);
        assertThat(dao.findOne(instance.getKey()).isPresent()).isTrue();
        final TestEntity entity = new TestEntity(KEY);
        Momento momento = new UpdateMomento(dao, KEY);
        entity.value = "baz";
        dao.save(entity);
        final Optional<TestEntity> entity1 = dao.findOne(KEY);
        assertThat(entity1.isPresent()).isTrue();
        assertThat(entity1.get().value).isEqualTo("baz");
        momento.rollback();
        final Optional<TestEntity> entity2 = dao.findOne(KEY);
        assertThat(entity2.isPresent()).isTrue();
        assertThat(entity2.get().value).isEqualTo("bar");
    }

    private class TestEntity extends HasKey<String> {
        String value;

        public TestEntity(String key) {
            super(key);
        }


    }
}