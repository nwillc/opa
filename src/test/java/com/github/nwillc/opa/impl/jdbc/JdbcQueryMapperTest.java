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

import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.junit.AbstractDaoTest;
import com.github.nwillc.opa.junit.QueryMapperTest;
import com.github.nwillc.opa.query.Query;
import com.github.nwillc.opa.query.QueryBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JdbcQueryMapperTest extends QueryMapperTest {
    @Override
    public Dao<String, AbstractDaoTest.TestEntity> get() {
        return JdbcDaoTest.getDao();
    }

    @Test
    public void testSqlEq() throws Exception {
        final Query<LabelValue> query = new QueryBuilder<>(LabelValue.class)
                .eq("label", "LABEL")
                .build();

        final JdbcQueryMapper<LabelValue> queryMapper = new JdbcQueryMapper<>();
        final Object apply = query.apply(queryMapper);
        assertThat(apply.toString()).isEqualTo("label = 'LABEL'");
    }

    @Test
    public void testSqlContains() throws Exception {
        final Query<LabelValue> query = new QueryBuilder<>(LabelValue.class)
                .contains("label", "LABEL")
                .build();

        final JdbcQueryMapper<LabelValue> queryMapper = new JdbcQueryMapper<>();
        final Object apply = query.apply(queryMapper);
        assertThat(apply.toString()).isEqualTo("label like '%LABEL%'");
    }

    @Test
    public void testSqlNot() throws Exception {
        final Query<LabelValue> query = new QueryBuilder<>(LabelValue.class)
                .eq("value", "VALUE")
                .not()
                .build();

        final JdbcQueryMapper<LabelValue> queryMapper = new JdbcQueryMapper<>();
        final Object apply = query.apply(queryMapper);
        assertThat(apply.toString()).isEqualTo("NOT ( value = 'VALUE' )");
    }

    @Test
    public void testSqlAnd() throws Exception {
        final Query<LabelValue> query = new QueryBuilder<>(LabelValue.class)
                .eq("value", "VALUE")
                .contains("label", "LABEL")
                .and()
                .build();

        final JdbcQueryMapper<LabelValue> queryMapper = new JdbcQueryMapper<>();
        final Object apply = query.apply(queryMapper);
        assertThat(apply.toString()).isEqualTo("( value = 'VALUE' AND label like '%LABEL%' )");
    }

    @Test
    public void testSqlOr() throws Exception {
        final Query<LabelValue> query = new QueryBuilder<>(LabelValue.class)
                .eq("value", "VALUE")
                .eq("label", "VALUE")
                .contains("label", "LABEL")
                .or()
                .build();

        final JdbcQueryMapper<LabelValue> queryMapper = new JdbcQueryMapper<>();
        final Object apply = query.apply(queryMapper);
        assertThat(apply.toString()).isEqualTo("( value = 'VALUE' OR label = 'VALUE' OR label like '%LABEL%' )");
    }

    @Test
    public void testSqlAndOr() throws Exception {
        final Query<LabelValue> query = new QueryBuilder<>(LabelValue.class)
                .eq("value", "VALUE")
                .eq("label", "VALUE")
                .or()
                .contains("label", "LABEL")
                .and()
                .build();

        final JdbcQueryMapper<LabelValue> queryMapper = new JdbcQueryMapper<>();
        final Object apply = query.apply(queryMapper);
        assertThat(apply.toString()).isEqualTo("( ( value = 'VALUE' OR label = 'VALUE' ) AND label like '%LABEL%' )");
    }

    public class LabelValue {
        public String label;
        public String value;
    }
}