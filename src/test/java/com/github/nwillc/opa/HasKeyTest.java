package com.github.nwillc.opa;

import com.github.nwillc.contracts.EqualsContract;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class HasKeyTest extends EqualsContract<HasKey<String>> {

    @SuppressWarnings("unchecked")
    @Override
    protected List<HasKey<String>> getEquals() {
        return Arrays.asList(new HasKey<>("foo"), new HasKey<>("foo"));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<HasKey<String>> getNotEquals() {
        return Arrays.asList(new HasKey<>("foo"), new HasKey<>("bar"));
    }

    @Test
    public void testHasKey() throws Exception {
        HasKey<String> hasKey = new HasKey<>("foo");

        assertThat(hasKey.getKey()).isEqualTo("foo");
    }
}