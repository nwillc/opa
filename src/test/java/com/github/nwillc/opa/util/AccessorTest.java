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

package com.github.nwillc.opa.util;

import com.github.nwillc.contracts.UtilityClassContract;
import com.github.nwillc.opa.HasKey;
import org.junit.Test;

import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class AccessorTest extends UtilityClassContract {

    @Override
    public Class<?> getClassToTest() {
        return Accessor.class;
    }

    @Test
    public void getFunction() throws Exception {
        final Bean bean = new Bean("foo");

        assertThat(bean.value).isEqualTo("foo");
        Function<Bean, String> accessor = Accessor.getFunction("value", Bean.class);
        assertThat(accessor.apply(bean)).isEqualTo("foo");
    }

    @Test
    public void getIlleagalAccess() throws Exception {
        final Bean bean = new Bean("foo");

        assertThat(bean.value).isEqualTo("foo");
        Function<Bean, String> accessor = Accessor.getFunction("value", Bean.class);
        assertThat(accessor.apply(bean)).isEqualTo("foo");
    }

    @Test
    public void testBadField() {
        assertThatThrownBy(() -> Accessor.getFunction("notHere", Bean.class))
                .isInstanceOf(NoSuchFieldException.class)
                .hasMessageContaining("notHere");
    }

    @Test
    public void getSuperFunction() throws Exception {
        final Bean bean = new Bean("foo");

        Function<Bean, String> accessor = Accessor.getFunction("key", Bean.class);
        assertThat(accessor).isNotNull();
        assertThat(accessor.apply(bean)).isEqualTo(bean.getKey());
    }

    @Test
    public void testAccessorException() throws Exception {
        final Bean bean = new Bean("foo");

        Function<Bean, String> accessor = Accessor.getFunction("exception", Bean.class);
        String apply = accessor.apply(bean);
        assertThat(apply).isNull();

        accessor = Accessor.getFunction("error", Bean.class);
        apply = accessor.apply(bean);
        assertThat(apply).isNull();
    }

    @Test
    public void testGetter() throws Exception {
        Bean bean = new Bean("foo");
        Bean spy = spy(bean);
        Function<Bean, String> accessor = Accessor.getFunction("key", Bean.class);
        accessor.apply(spy);
        verify(spy).getKey();
    }

    @Test
    public void testNullValue() throws Exception {
        Bean bean = new Bean(null);
        Function<Bean, String> accessor = Accessor.getFunction("value", Bean.class);
        assertThat(accessor.apply(bean)).isNull();
    }

    public static class Bean extends HasKey<String> {
        public final String value;
        public final Object error = new Object() {
            @Override
            public String toString() {
                throw new RuntimeException();
            }
        };

        public Bean(String value) {
            super(UUID.randomUUID().toString());
            this.value = value;
        }

        public Exception getException() {
            throw new RuntimeException();
        }
    }
}