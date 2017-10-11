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

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @param <T> type your doing a logical comparison on
 * @param <R> type used by the persistence implementation to represent a query
 * @see QueryGenerator
 */
public class Logical<T, R> extends Query<T, R> {
    private final Collection<Query<T, R>> queries;

    public Logical(final Operator operator, final Query<T, R> query) {
        this(operator, Collections.singletonList(query));
    }

    public Logical(final Operator operator, final Collection<Query<T, R>> queries) {
        super(operator);
        this.queries = queries;
    }

    @Override
    public R apply(final QueryMapper<T, R> tQueryMapper) {
        queries.forEach(tFilter -> tFilter.apply(tQueryMapper));
        return tQueryMapper.apply(this);
    }

    @Override
    public String toString() {
        return getOperator().name().toLowerCase() +
                '(' + queries.stream().map(Query::toString).collect(Collectors.joining(",")) + ')';
    }
}
