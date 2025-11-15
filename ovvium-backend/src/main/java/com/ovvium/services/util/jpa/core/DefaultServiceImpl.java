package com.ovvium.services.util.jpa.core;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.ovvium.services.util.common.domain.Identifiable;
import com.ovvium.services.util.common.domain.Page;
import com.ovvium.services.util.common.domain.PageRequest;
import com.ovvium.services.util.common.domain.Request;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
public class DefaultServiceImpl<T extends Identifiable<K>, K extends Serializable> implements DefaultService<T, K> {

	private final DefaultRepository<T, K> repository;

	@Override
	public T save(T entity) {
		return repository.save(entity);
	}

	@Override
	public Optional<T> get(K id) {
		return repository.get(id);
	}

	@Override
	public T getOrFail(K id) {
		return repository.getOrFail(id);
	}

	@Override
	public List<T> list() {
		return repository.list();
	}

	@Override
	public List<T> list(Request request) {
		return repository.list(request);
	}

	@Override
	public Page<T> page(PageRequest request) {
		return repository.page(request);
	}

	@Override
	public long count() {
		return repository.count();
	}

	@Override
	public long count(Request request) {
		return repository.count(request);
	}

	@Override
	public boolean remove(T entity) {
		return repository.remove(entity);
	}

	@Override
	public boolean remove(K id) {
		return repository.remove(id);
	}

}
