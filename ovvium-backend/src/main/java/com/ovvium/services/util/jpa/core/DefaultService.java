package com.ovvium.services.util.jpa.core;

import java.io.Serializable;

import com.ovvium.services.util.common.domain.DefaultOperations;
import com.ovvium.services.util.common.domain.Identifiable;

public interface DefaultService<T extends Identifiable<K>, K extends Serializable> extends DefaultOperations<T, K> {

}
