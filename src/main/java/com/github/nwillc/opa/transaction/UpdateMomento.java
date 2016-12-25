package com.github.nwillc.opa.transaction;

import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.HasKey;

import java.util.Optional;

public class UpdateMomento<K, T extends HasKey<K>> extends Momento<K,T> {
    private final Optional<T> entity;

    UpdateMomento(Dao<K,T> dao, K key) {
        super(dao,key);
        entity = dao.findOne(key);
    }

    @Override
    public void rollback() {
        entity.ifPresent(getDao()::save);
    }
}
