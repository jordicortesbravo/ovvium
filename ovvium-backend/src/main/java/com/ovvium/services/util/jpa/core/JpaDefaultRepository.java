package com.ovvium.services.util.jpa.core;

import com.mysema.query.NonUniqueResultException;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPAUpdateClause;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.path.EntityPathBase;
import com.mysema.query.types.path.PathBuilder;
import com.ovvium.services.util.common.domain.Identifiable;
import com.ovvium.services.util.common.domain.Page;
import com.ovvium.services.util.common.domain.PageRequest;
import com.ovvium.services.util.common.domain.Request;
import org.springframework.core.GenericTypeResolver;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class JpaDefaultRepository<T extends Identifiable<K> & DbIdentifiable, K extends Serializable>
		implements
		DefaultRepository<T, K> {

	private JpaRepository<T, K> delegate;

	@SuppressWarnings({"unchecked", "rawtypes"})
	@PersistenceContext
	protected void setEntityManager(EntityManager entityManager) {
		Class<?>[] types = GenericTypeResolver.resolveTypeArguments(getClass(), DefaultRepository.class);
		delegate = new JpaRepository(entityManager, types[0]);
	}

	@Override
	public T save(T entity) {
		return delegate.save(entity);
	}

	@Override
	public Optional<T> get(K id) {
		return delegate.get(id);
	}

	@Override
	public T getOrFail(K id) {
		return delegate.getOrFail(id);
	}

	@Override
	public boolean remove(T entity) {
		return delegate.remove(entity);
	}

	@Override
	public boolean remove(K id) {
		return delegate.remove(id);
	}

	@Override
	public List<T> list() {
		return delegate.list();
	}

	@Override
	public List<T> list(Request request) {
		return delegate.list(request);
	}

	@Override
	public Page<T> page(PageRequest request) {
		return delegate.page(request);
	}

	@Override
	public long count() {
		return delegate.count();
	}

	@Override
	public long count(Request request) {
		return delegate.count(request);
	}

	protected JPAQuery query() {
		return delegate.query();
	}

	protected JPAQuery query(EntityPathBase<?>... paths) {
		return delegate.query(paths);
	}

	protected JPAQuery query(Request request) {
		return delegate.query(request);
	}

	protected JPAQuery query(Predicate predicate, OrderSpecifier<?>... orders) {
		return delegate.query(predicate, orders);
	}

	/**
	 * @return unique result or Optional if none
	 * @throws NonUniqueResultException
	 *             if more than one result is returned
	 */
	protected Optional<T> get(Predicate predicate) throws NonUniqueResultException {
		return delegate.get(predicate);
	}

	/**
	 * @return unique result or throw exception if not found
	 * @throws NonUniqueResultException
	 *             if more than one result is returned
	 */
	protected T getOrFail(Predicate predicate) throws NonUniqueResultException {
		return delegate.getOrFail(predicate);
	}

	/**
	 * @return first result or null if none
	 */
	protected Optional<T> first(Predicate predicate, OrderSpecifier<?>... orders) {
		return Optional.ofNullable(delegate.first(predicate, orders));
	}

	protected List<T> list(Predicate predicate, OrderSpecifier<?>... orders) {
		return delegate.list(predicate, orders);
	}

	protected long count(Predicate predicate) {
		return delegate.count(predicate);
	}

	protected boolean exists(Predicate predicate) {
		return delegate.count(predicate) > 0;
	}

	protected long count(Predicate predicate, boolean distinct) {
		return delegate.count(predicate, distinct);
	}

	protected Page<T> page(Predicate predicate, PageRequest request) {
		return delegate.page(predicate, request);
	}

	protected Page<T> page(JPAQuery query, int pageNumber, int pageSize) {
		return delegate.page(query, pageNumber, pageSize);
	}

	protected JPAUpdateClause update(Predicate predicate) {
		return delegate.update(predicate);
	}

	protected long remove(Predicate predicate) {
		return delegate.remove(predicate);
	}

	protected EntityManager getEntityManager() {
		return delegate.getEntityManager();
	}

	protected Class<T> getEntityClass() {
		return delegate.getEntityClass();
	}

	protected Class<K> getIdClass() {
		return delegate.getIdClass();
	}

	protected EntityPathBase<T> getRoot() {
		return delegate.getRoot();
	}

	protected PathBuilder<T> getBuilder() {
		return delegate.getBuilder();
	}

}
