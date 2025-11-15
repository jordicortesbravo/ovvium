package com.ovvium.services.util.common.domain;

public interface Identifiable<T> {

    String ID_PROPERTY = "id";
    String ID_METHOD = "getId";

    T getId();
}
