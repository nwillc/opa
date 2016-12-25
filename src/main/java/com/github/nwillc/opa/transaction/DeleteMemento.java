package com.github.nwillc.opa.transaction;

import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.HasKey;

import java.util.Optional;

public class DeleteMemento<K, T extends HasKey<K>> extends Memento<K,T> {
    private final Optional<T> element;

    public DeleteMemento(Dao<K, T> dao, K key) {
        super(dao, key);
        element = dao.findOne(key);
    }

    @Override
    public void rollback() {
        element.ifPresent(getDao()::save);
    }
}
