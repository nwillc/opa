package com.github.nwillc.opa.transaction;

import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.HasKey;

import java.util.Optional;

public class UpdateMemento<K, T extends HasKey<K>> extends Memento<K,T> {
    private final Optional<T> entity;

    UpdateMemento(Dao<K,T> dao, K key) {
        super(dao,key);
        entity = dao.findOne(key);
    }

    @Override
    public void rollback() {
        entity.ifPresent(getDao()::save);
    }
}
