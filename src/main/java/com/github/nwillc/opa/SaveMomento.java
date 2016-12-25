package com.github.nwillc.opa;

public class SaveMomento<K, T extends HasKey<K>> implements Momento {
    private final Dao<K,T> dao;
    private final K key;

    public SaveMomento(Dao<K, T> dao, K key) {
        this.dao = dao;
        this.key = key;
    }

    @Override
    public void rollback() {
        dao.delete(key);
    }
}
