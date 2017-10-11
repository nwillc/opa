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

    @Test
    public void testSetKey() {
        HasKey<String> hasKey = new HasKey<>("foo");

        hasKey.setKey("bar");
        assertThat(hasKey.getKey()).isEqualTo("bar");
    }

    @Test
    public void testToString() {
        HasKey<String> hasKey = new HasKey<>("foo");

        assertThat(hasKey.toString()).contains("key").contains("foo");
    }

    @Test
    public void testClassNotEquals() {
        HasKey<String> hasKey = new HasKey<>("foo");
        SubType subType = new SubType("foo");

        assertThat(hasKey.getKey()).isEqualTo(subType.getKey());
        assertThat(hasKey.equals(subType)).isFalse();
    }

    public class SubType extends HasKey<String> {

        public SubType(String key) {
            super(key);
        }
    }
}