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
        return Arrays.asList(new HasKey<String>("foo"), new HasKey<String>("foo"));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<HasKey<String>> getNotEquals() {
        return Arrays.asList(new HasKey<String>("foo"), new HasKey<String>("bar"));
    }

    @Test
    public void testHasKey() throws Exception {
        HasKey<String> hasKey = new HasKey<String>("foo");

        assertThat(hasKey.getKey()).isEqualTo("foo");
    }
}