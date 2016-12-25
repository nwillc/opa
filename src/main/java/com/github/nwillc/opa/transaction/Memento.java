package com.github.nwillc.opa.transaction;

import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.HasKey;

public class Memento<K, T extends HasKey<K>> implements Transaction {
    private final Dao<K,T> dao;
    private final K key;

    Memento(Dao<K, T> dao, K key) {
        this.dao = dao;
        this.key = key;
    }

    Dao<K, T> getDao() {
        return dao;
    }

    K getKey() {
        return key;
    }
}
