/*
 * Copyright 2018 nwillc@gmail.com
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

package com.github.nwillc.opa.util;

import org.junit.Test;

import java.util.function.Function;

/**
 *
 */
public class AccessorPerfTest {
    private final static int COUNT = 10_000_000;

    @Test
    public void testPerf() throws NoSuchFieldException {
        final Bean bean = new Bean("foo");


        long nanoTime = System.nanoTime();
        Function<Bean, String> getValue = Bean::getValue;
        for (int x = 0; x < COUNT; x++) {
            getValue.apply(bean);
        }
        System.out.println("Elapsed: " + (System.nanoTime() - nanoTime));


        nanoTime = System.nanoTime();
        getValue = OldAccessor.getFunction("value", Bean.class);
        for (int x = 0; x < COUNT; x++) {
            getValue.apply(bean);
        }
        System.out.println("Elapsed: " + (System.nanoTime() - nanoTime));


        nanoTime = System.nanoTime();
        getValue = Accessor.getFunction("value", Bean.class);
        for (int x = 0; x < COUNT; x++) {
            getValue.apply(bean);
        }
        System.out.println("Elapsed: " + (System.nanoTime() - nanoTime));
    }

    public class Bean {
        private String value;

        public Bean(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
