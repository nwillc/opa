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

package com.github.nwillc.opa.impl.jdbc;

import com.github.nwillc.funjdbc.SqlStatement;
import com.github.nwillc.opa.query.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;


/**
 * A {@link com.github.nwillc.opa.query.QueryMapper} implementation that converts a
 * {@link com.github.nwillc.opa.query.Query} to a {@link com.github.nwillc.funjdbc.SqlStatement}.
 *
 * @param <T> The entity type.
 */
public class JdbcQueryMapper<T> extends DequeQueryMapper<String, T> {
    @Override
    public Object apply(Query<T> tQuery) {
        final String collect;

        switch (tQuery.getOperator()) {
            case EQ:
                phrases.addLast(String.format("%s = '%s'",
                        ((Comparison) tQuery).getFieldName(),
                        ((Comparison) tQuery).getValue()));
                break;
            case CONTAINS:
                phrases.addLast(String.format("%s like '%%%s%%'",
                        ((Comparison) tQuery).getFieldName(),
                        ((Comparison) tQuery).getValue()));
                break;
            case NOT:
                final String last = phrases.removeLast();
                phrases.addLast("NOT ( " + last + " )");
                break;
            case AND:
                collect = phrases.stream().collect(Collectors
                        .joining(" AND ", "( ", " )"));
                phrases = new ArrayDeque<>();
                phrases.addLast(collect);
                break;
            case OR:
                collect = phrases.stream().collect(Collectors
                        .joining(" OR ", "( ", " )"));
                phrases = new ArrayDeque<>();
                phrases.addLast(collect);
                break;
        }
        return phrases.getFirst();
    }
}
