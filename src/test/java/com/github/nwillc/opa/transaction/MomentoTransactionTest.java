package com.github.nwillc.opa.transaction;

import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.HasKey;
import com.github.nwillc.opa.memory.MemoryBackedDao;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MomentoTransactionTest {
    private MomentoTransaction instance;
    private Dao dao;

    @Before
    public void setUp() throws Exception {
        instance = new MomentoTransaction();
        dao = mock(Dao.class);
    }

    @Test
    public void testSimpleCommit() throws Exception {
       Momento momento = mock(Momento.class);
       instance.add(momento);
       instance.commit();
       verify(momento, times(1)).commit();
    }

    @Test
    public void testSimpleRollback() throws Exception {
        Momento momento = mock(Momento.class);
        instance.add(momento);
        instance.rollback();
        verify(momento, times(1)).rollback();
    }
}