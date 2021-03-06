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

package com.github.nwillc.opa.impl.memory;

import com.github.nwillc.opa.query.Comparison;
import com.github.nwillc.opa.query.DequeQueryMapper;
import com.github.nwillc.opa.query.Query;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class MemoryQueryMapper<T> extends DequeQueryMapper<Predicate<T>,T> {
    @Override
    @SuppressWarnings("unchecked")
    public Predicate<T> apply(final Query<T> tQuery) {
        Function<T, String> accessor;
        String value;

        Objects.requireNonNull(tQuery.getOperator());

        switch (tQuery.getOperator()) {
            case EQ:
                accessor = ((Comparison) tQuery).getAccessor();
                value = ((Comparison) tQuery).getValue();
                phrases.addLast(t -> accessor.apply(t).equals(value));
                break;
            case CONTAINS:
                accessor = ((Comparison) tQuery).getAccessor();
                value = ((Comparison) tQuery).getValue();
                phrases.addLast(t -> accessor.apply(t).contains(value));
                break;
            case NOT:
                final Predicate<T> predicate = phrases.removeLast();
                phrases.addLast(predicate.negate());
                break;
            case AND:
                final AllMatch allMatch = new AllMatch(phrases);
                phrases = new ArrayDeque<>();
                phrases.addLast(allMatch);
                break;
            case OR:
                final AnyMatch anyMatch = new AnyMatch(phrases);
                phrases = new ArrayDeque<>();
                phrases.addLast(anyMatch);
                break;
        }

        return phrases.getFirst();
    }

    private class AllMatch implements Predicate<T> {
        private final Collection<Predicate<T>> predicates;

        private AllMatch(Collection<Predicate<T>> predicates) {
            this.predicates = predicates;
        }

        @Override
        public boolean test(T t) {
            return predicates.stream().allMatch(p -> p.test(t));
        }
    }

    private class AnyMatch implements Predicate<T> {
        private final Collection<Predicate<T>> predicates;

        private AnyMatch(Collection<Predicate<T>> predicates) {
            this.predicates = predicates;
        }

        @Override
        public boolean test(T t) {
            return predicates.stream().anyMatch(p -> p.test(t));
        }
    }
}
