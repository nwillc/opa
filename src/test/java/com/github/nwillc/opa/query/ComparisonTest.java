package com.github.nwillc.opa.query;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class ComparisonTest {
    @Test
    public void getFieldName() throws Exception {
        Comparison comparison = new Comparison<>(Bean.class, "label", "value", Operator.EQ);

        assertThat(comparison.getFieldName()).isEqualTo("label");
    }

    static class Bean {
        String label;
    }
}