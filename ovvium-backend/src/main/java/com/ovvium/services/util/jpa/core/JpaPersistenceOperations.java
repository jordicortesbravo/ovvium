package com.ovvium.services.util.jpa.core;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class JpaPersistenceOperations implements PersistenceOperations {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void synchronize() {
        entityManager.flush();
        entityManager.clear();
    }

    @Override
    public void clearCache() {
        entityManager.getEntityManagerFactory().getCache().evictAll();
    }

}
