package com.github.nwillc.opa.transaction;

import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.HasKey;

class Momento<K, T extends HasKey<K>> implements Transaction {
    private final Dao<K,T> dao;
    private final K key;

    Momento(Dao<K, T> dao, K key) {
        this.dao = dao;
        this.key = key;
    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }

    Dao<K, T> getDao() {
        return dao;
    }

    K getKey() {
        return key;
    }
}
