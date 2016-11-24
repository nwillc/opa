package com.github.nwillc.opa;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HasKeyTest {

    @Test
    public void testHasKey() throws Exception {
        HasKey<String> hasKey = new HasKey<String>("foo");

        assertThat(hasKey.getKey()).isEqualTo("foo");
    }
}