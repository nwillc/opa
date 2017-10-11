/*
 * Copyright (c) 2016, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.github.nwillc.opa.query;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class QueryMapperTest {

    @Test
    public void testVisitor() throws Exception {
        final AtomicReference<String> str = new AtomicReference<>();

        QueryBuilder<Bean, Predicate<Bean>> generator = new QueryBuilder<>(Bean.class);
        generator.eq("foo", "bar").eq("foo", "baz").or();
        generator.build().apply(q -> {
            str.set(q.toString());
            return null;
        });

        assertThat(str.get()).isEqualTo("or(eq(\"foo\",\"bar\"),eq(\"foo\",\"baz\"))");
    }

    class Bean {
        String foo;
    }
}