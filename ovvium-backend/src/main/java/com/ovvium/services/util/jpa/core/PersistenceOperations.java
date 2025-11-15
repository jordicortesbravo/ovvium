package com.ovvium.services.util.jpa.core;

public interface PersistenceOperations {

    /**
     * Actualitza els canvis pendents a la base de dades (no commit) i buida la caché de primer nivell.
     */
    void synchronize();

    /**
     * Buida la caché de segon nivell
     */
    void clearCache();

}
