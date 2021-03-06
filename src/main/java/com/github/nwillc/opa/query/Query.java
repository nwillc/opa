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

package com.github.nwillc.opa.query;

import java.util.function.Function;

/**
 * This represents a query in a implementation independent manner.
 *
 * @param <T> type the query operates on
 * @see QueryBuilder
 */
public class Query<T> implements Function<QueryMapper<T>, Object> {
    private final Operator operator;

    public Query(final Operator operator) {
        this.operator = operator;
    }

    @Override
    public Object apply(final QueryMapper<T> tQueryMapper) {
        return tQueryMapper.apply(this);
    }

    public Operator getOperator() {
        return operator;
    }
}
