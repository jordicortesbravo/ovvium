package com.ovvium.services.util.jpa.test;

import lombok.Data;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

/**
 * Wrapper d'un PlatformTransactionManager que permet impedir que es faci commit (fent rollback en el seu lloc). És útil en el cas dels
 * tests, ja que habitualment no es vol fer commit, ja que podria afectar a un altre test.
 * 
 * S'ha de tenir en compte que en el cas de fer falta un populator sí es voldrà fer commit. En aquest cas el més convenient és extendre
 * TestDataPopulator.
 * 
 */
@Data
public class CommitmentDeciderPlatformTransactionManager implements PlatformTransactionManager {

    private final PlatformTransactionManager delegate;
    private boolean actuallyCommit = true;

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) {
        return delegate.getTransaction(definition);
    }

    @Override
    public void commit(TransactionStatus status) {
        if (actuallyCommit) {
            delegate.commit(status);
        } else {
            delegate.rollback(status);
        }
    }

    @Override
    public void rollback(TransactionStatus status) {
        delegate.rollback(status);
    }

}
