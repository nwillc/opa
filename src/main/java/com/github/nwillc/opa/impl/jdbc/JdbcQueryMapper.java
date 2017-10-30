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
import com.github.nwillc.opa.query.Comparison;
import com.github.nwillc.opa.query.Query;
import com.github.nwillc.opa.query.QueryMapper;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;


/**
 * A {@link com.github.nwillc.opa.query.QueryMapper} implementation that converts a
 * {@link com.github.nwillc.opa.query.Query} to a {@link com.github.nwillc.funjdbc.SqlStatement}.
 *
 * @param <T> The entity type.
 */
public class JdbcQueryMapper<T> implements QueryMapper<T> {
    private Deque<String> strings = new ArrayDeque<>();
    private Deque<Object> args = new ArrayDeque<>();
    private final String select;

    JdbcQueryMapper(String select) {
        this.select = select;
    }

    @Override
    public Object apply(Query<T> tQuery) {
        String one, two;

        switch (tQuery.getOperator()) {
            case EQ:
                strings.add("%s = '%s'");
                args.add(((Comparison) tQuery).getFieldName());
                args.add(((Comparison) tQuery).getValue());
                break;
            case CONTAINS:
                strings.add("%s like '%%%s%%'");
                args.add(((Comparison) tQuery).getFieldName());
                args.add(((Comparison) tQuery).getValue());
                break;
            case NOT:
                strings.push("NOT (");
                strings.add(")");
                break;
            case AND:
                one = strings.pop();
                two = strings.pop();
                strings.push(one);
                strings.push("AND");
                strings.push(two);
                break;
            case OR:
                one = strings.pop();
                two = strings.pop();
                strings.push(one);
                strings.push("OR");
                strings.push(two);
                break;
        }

        return new SqlStatement(select + " WHERE " + strings.stream().collect(Collectors.joining(" ")),
                args.toArray());
    }
}
