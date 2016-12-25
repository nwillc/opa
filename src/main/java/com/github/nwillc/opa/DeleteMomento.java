package com.github.nwillc.opa;

import java.util.Optional;

public class DeleteMomento<K, T extends HasKey<K>> implements Momento {
    private final Dao<K,T> dao;
    private final Optional<T> element;

    public DeleteMomento(Dao<K, T> dao, K key) {
        this.dao = dao;
        element = dao.findOne(key);
    }

    @Override
    public void rollback() {
        element.ifPresent(dao::save);
    }
}
