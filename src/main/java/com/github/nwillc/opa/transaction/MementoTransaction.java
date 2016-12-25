package com.github.nwillc.opa.transaction;

import java.util.ArrayDeque;
import java.util.Deque;

public class MementoTransaction implements Transaction {
    private final Deque<Memento> mementos = new ArrayDeque<>();

    @Override
    public void commit() {
     mementos.iterator().forEachRemaining(Memento::commit);
    }

    @Override
    public void rollback() {
        mementos.descendingIterator().forEachRemaining(Memento::rollback);
    }

    public void add(Memento memento) {
        mementos.addLast(memento);
    }
}
