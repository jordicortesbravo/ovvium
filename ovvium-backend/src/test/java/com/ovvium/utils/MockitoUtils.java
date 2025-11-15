package com.ovvium.utils;

import com.ovvium.services.util.common.domain.Identifiable;
import com.ovvium.services.util.jpa.core.DefaultRepository;
import org.mockito.stubbing.Answer;

import java.io.Serializable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockitoUtils {

	/**
	 * Creates a Mock of a Repository to return the saved Entity by default when save.
	 */
	public static <R extends DefaultRepository<T, K>, T extends Identifiable<K>, K extends Serializable> R mockRepository(Class<R> classToMock) {
		R mock = mock(classToMock);
		when(mock.save(any())).thenAnswer((Answer<T>) invocationOnMock -> invocationOnMock.getArgument(0));
		return mock;
	}

}
