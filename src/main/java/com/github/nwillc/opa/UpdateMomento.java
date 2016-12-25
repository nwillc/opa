package com.github.nwillc.opa;

import java.util.Optional;

public class UpdateMomento<K, T extends HasKey<K>> implements Momento {
    private final Optional<T> entity;
    private final Dao<K,T> dao;

    public UpdateMomento(Dao<K,T> dao, K key) {
        this.dao = dao;
        entity = dao.findOne(key);
    }

    @Override
    public void rollback() {
        entity.ifPresent(dao::save);
    }
}
