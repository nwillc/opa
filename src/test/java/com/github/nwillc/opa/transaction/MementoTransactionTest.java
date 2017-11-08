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

package com.github.nwillc.opa.transaction;

import mockit.FullVerificationsInOrder;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class MementoTransactionTest {
    private MementoTransaction instance;
    @Mocked
    Memento memento1;
    @Mocked
    Memento memento2;

    @Before
    public void setUp() throws Exception {
        instance = new MementoTransaction();
    }

    @Test
    public void testCommit() throws Exception {
        instance.add(memento1);
        instance.add(memento2);
        instance.commit();
        new FullVerificationsInOrder() {{
            memento1.commit();
            times = 1;
            memento2.commit();
            times = 1;
        }};
    }

//    @Test
//    public void testRollback() throws Exception {
//        Memento m1 = mock(Memento.class);
//        Memento m2 = mock(Memento.class);
//        instance.add(m1);
//        instance.add(m2);
//        instance.rollback();
//        InOrder inOrder = inOrder(m2, m1);
//        inOrder.verify(m2).rollback();
//        inOrder.verify(m1).rollback();
//    }
}