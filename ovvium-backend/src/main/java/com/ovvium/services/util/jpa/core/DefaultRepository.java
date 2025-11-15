package com.ovvium.services.util.jpa.core;

import com.ovvium.services.util.common.domain.DefaultOperations;
import com.ovvium.services.util.common.domain.Identifiable;

import java.io.Serializable;


public interface DefaultRepository<T extends Identifiable<K>, K extends Serializable> extends DefaultOperations<T, K> {


}
