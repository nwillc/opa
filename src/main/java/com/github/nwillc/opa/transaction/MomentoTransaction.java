package com.github.nwillc.opa.transaction;

import java.util.ArrayDeque;
import java.util.Deque;

public class MomentoTransaction implements Transaction {
    private final Deque<Momento> momentos = new ArrayDeque<>();

    @Override
    public void commit() {
        momentos.iterator().forEachRemaining(Momento::commit);
    }

    @Override
    public void rollback() {
        momentos.descendingIterator().forEachRemaining(Momento::rollback);
    }

    public void add(Momento momento) {
        momentos.addFirst(momento);
    }
}
