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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

/**
 * Use this to build an OPA Query.  For example, assuming you had an entity like:
 * <pre>
 *     {@code
 *      class KeyValue {
 *          String key;
 *          String value;
 *      }
 *     }
 * </pre>
 * And your persistence used Strings to represent its queries, the following would be some example
 * QueryBuilder usages:
 * <pre>
 *     {@code
 *          // Check for a key not equal to "pi" and a value of "3.142"
 *          new QueryBuilder(KeyValue.class)
 *              .eq("key","pi")
 *              .not()
 *              .eq("value","3.142")
 *              .and()
 *              .build();
 *
 *          // Check for a key of "yesterday" or "today" or "tomorrow"
 *          new QueryBuilder(KeyValue.class)
 *              .eq("key","yesterday")
 *              .eq("key","today")
 *              .eq("key","tomorrow")
 *              .or()
 *              .build();
 *
 *          // Check for an key "name" and a value containing "jon"
 *          new QueryBuilder(KeyValue.class).eq("key","name").contains("value","jon").and().build();
 *     }
 * </pre>
 *
 * @param <T> type the query operates on
 */
public class QueryBuilder<T> {
    private Deque<Query<T>> queries = new ArrayDeque<>();
    private final Class<T> tClass;

    public QueryBuilder(final Class<T> tClass) {
        this.tClass = tClass;
    }

    /**
     * An entity's field value should contains a substring.
     *
     * @param field   the name of the field
     * @param pattern the pattern its value should contain
     * @return the query builder
     * @throws NoSuchFieldException if the entity does not contain the field
     */
    public QueryBuilder<T> contains(final String field, final String pattern) throws NoSuchFieldException {
        queries.addLast(new Comparison<>(tClass, field, pattern, Operator.CONTAINS));
        return this;
    }

    /**
     * An entity's field value should equal a value.
     *
     * @param field the name of the field
     * @param value the pattern its value should contain
     * @return the query builder
     * @throws NoSuchFieldException if the entity does not contain the field
     */
    public QueryBuilder<T> eq(final String field, final String value) throws NoSuchFieldException {
        queries.addLast(new Comparison<>(tClass, field, value, Operator.EQ));
        return this;
    }

    /**
     * Negate the prior query.
     *
     * @return the query builder.
     */
    public QueryBuilder<T> not() {
        if (queries.isEmpty()) {
            throw new IllegalStateException("Nothing to negate");
        }
        queries.addFirst(new Logical<>(Operator.NOT, queries.removeFirst()));
        return this;
    }

    /**
     * And all the prior queries.
     *
     * @return the query builder.
     */
    public QueryBuilder<T> and() {
        if (queries.size() < 2) {
            throw new IllegalStateException("And requires multiple queries");
        }
        final Query<T> and = new Logical<>(Operator.AND, queries);
        queries = new ArrayDeque<>();
        queries.addFirst(and);
        return this;
    }

    /**
     * Or all the prior queries.
     *
     * @return the query builder.
     */
    public QueryBuilder<T> or() {
        if (queries.size() < 2) {
            throw new IllegalStateException("Or requires multiple queries");
        }
        final Query<T> and = new Logical<>(Operator.OR, queries);
        queries = new ArrayDeque<>();
        queries.addFirst(and);
        return this;
    }

    public Query<T> build() {
        if (queries.isEmpty()) {
            return null;
        }

        if (queries.size() == 1) {
            return queries.getFirst();
        }

        return new Logical<>(Operator.OR, queries);
    }

    @Override
    public String toString() {
        return queries.stream().map(Query::toString).collect(Collectors.joining(", "));
    }

}
