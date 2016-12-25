package com.github.nwillc.opa.transaction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

public class MementoTransactionTest {
    private MementoTransaction instance;

    @Before
    public void setUp() throws Exception {
        instance = new MementoTransaction();
    }

    @Test
    public void testCommit() throws Exception {
        Memento m1 = mock(Memento.class);
        Memento m2 = mock(Memento.class);
        instance.add(m1);
        instance.add(m2);
        instance.commit();
        InOrder inOrder = inOrder(m1,m2);
        inOrder.verify(m1).commit();
        inOrder.verify(m2).commit();
    }

    @Test
    public void testRollback() throws Exception {
        Memento m1 = mock(Memento.class);
        Memento m2 = mock(Memento.class);
        instance.add(m1);
        instance.add(m2);
        instance.rollback();
        InOrder inOrder = inOrder(m2,m1);
        inOrder.verify(m2).rollback();
        inOrder.verify(m1).rollback();
    }
}