package com.github.nwillc.opa.query;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;


public class OperatorTest {
    private Collection<String> labels = Arrays.asList("EQ", "CONTAINS", "NOT", "AND", "OR");

    @Test
    public void nameNames() throws Exception {
        assertThat(Operator.values()).hasSize(labels.size());
        labels.forEach(l -> assertThat(Operator.valueOf(l)).isNotNull());
    }
}