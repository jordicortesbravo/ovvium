package com.ovvium.utils;

import org.springframework.transaction.annotation.Transactional;

// Used to do assertions on detached objects
public class TransactionalHelper {

    @Transactional
    public void executeWithinTransaction(Runnable runnable) {
        runnable.run();
    }

}