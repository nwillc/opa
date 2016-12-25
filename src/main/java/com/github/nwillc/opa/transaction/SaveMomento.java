package com.github.nwillc.opa.transaction;

import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.HasKey;

public class SaveMomento<K, T extends HasKey<K>> extends Momento<K,T> {

    SaveMomento(Dao<K, T> dao, K key) {
      super(dao, key);
    }

    @Override
    public void rollback() {
        getDao().delete(getKey());
    }
}
