package com.github.nwillc.opa.transaction;

import com.github.nwillc.opa.Dao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

public class MomentoTransactionTest {
    private MomentoTransaction instance;
    private Dao dao;

    @Before
    public void setUp() throws Exception {
        instance = new MomentoTransaction();
        dao = mock(Dao.class);
    }

    @Test
    public void testCommit() throws Exception {
        Momento m1 = mock(Momento.class);
        Momento m2 = mock(Momento.class);
        instance.add(m1);
        instance.add(m2);
        instance.commit();
        InOrder inOrder = inOrder(m1,m2);
        inOrder.verify(m1).commit();
        inOrder.verify(m2).commit();
    }

    @Test
    public void testRollback() throws Exception {
        Momento m1 = mock(Momento.class);
        Momento m2 = mock(Momento.class);
        instance.add(m1);
        instance.add(m2);
        instance.rollback();
        InOrder inOrder = inOrder(m2,m1);
        inOrder.verify(m2).rollback();
        inOrder.verify(m1).rollback();
    }
}