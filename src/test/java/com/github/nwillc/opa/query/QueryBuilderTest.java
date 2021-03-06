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

package com.github.nwillc.opa.query;

import com.github.nwillc.opa.HasKey;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class QueryBuilderTest {
    private QueryBuilder<Bean> queryGenerator;

    @Before
    public void setUp() throws Exception {
        queryGenerator = new QueryBuilder<>(Bean.class);
    }

    @Test
    public void testContains() throws Exception {
        QueryBuilder generator = queryGenerator
                .contains("key", "42");
        assertThat(generator.toString()).isEqualTo("contains(\"key\",\"42\")");
    }

    @Test
    public void testNot() throws Exception {
        QueryBuilder generator = queryGenerator
                .contains("key", "1")
                .not();
        assertThat(generator.toString()).isEqualTo("not(contains(\"key\",\"1\"))");
    }

    @Test
    public void testNotOfNothing() throws Exception {
        assertThatThrownBy(() -> queryGenerator.not()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testAnd() throws Exception {
        QueryBuilder generator = queryGenerator
                .contains("key", "1")
                .contains("key", "2")
                .and();
        assertThat(generator.toString()).isEqualTo("and(contains(\"key\",\"1\"),contains(\"key\",\"2\"))");

    }

    @Test
    public void testAndOfTooLittle() throws Exception {
        assertThatThrownBy(() -> queryGenerator.contains("key", "1").and()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testOr() throws Exception {
        QueryBuilder generator = queryGenerator
                .contains("key", "1")
                .contains("key", "2")
                .or();
        assertThat(generator.toString()).isEqualTo("or(contains(\"key\",\"1\"),contains(\"key\",\"2\"))");
    }

    @Test
    public void testOrOfTooLittle() throws Exception {
        assertThatThrownBy(() -> queryGenerator.contains("key","1").or()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testNoOperations() throws Exception {
        assertThat(queryGenerator.build()).isNull();
    }

    @Test
    public void testSingleOperator() throws Exception {
        QueryBuilder generator = queryGenerator.eq("key", "foo");

        assertThat(generator.toString()).isEqualTo("eq(\"key\",\"foo\")");

        Query<Bean> filter = generator.build();

        assertThat(filter).isInstanceOf(Comparison.class);
        assertThat(filter.getOperator()).isEqualTo(Operator.EQ);
    }

    @Test
    public void testAutoOrOperator() throws Exception {
        QueryBuilder generator = queryGenerator.eq("key", "foo").eq("key", "bar");

        assertThat(generator.toString()).isEqualTo("eq(\"key\",\"foo\"), eq(\"key\",\"bar\")");

        Query<Bean> filter = generator.build();

        assertThat(filter).isInstanceOf(Logical.class);
        assertThat(filter.getOperator()).isEqualTo(Operator.OR);
    }

    public class Bean extends HasKey<String> {
        private final String value;
        private String second;

        public Bean(String key, String value) {
            super(key);
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String getSecond() {
            return second;
        }

        public void setSecond(String second) {
            this.second = second;
        }

        @Override
        public String toString() {
            return "Bean{" +
                    "key='" + getKey() + '\'' +
                    " value='" + value + '\'' +
                    ", second='" + second + '\'' +
                    '}';
        }
    }
}