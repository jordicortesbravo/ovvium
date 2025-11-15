package com.ovvium.services.util.jpa.core;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.NonUniqueResultException;
import com.mysema.query.jpa.impl.JPADeleteClause;
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
import com.ovvium.services.util.common.domain.SimplePage;
import com.ovvium.services.util.util.basic.Traverser;
import lombok.Data;
import lombok.val;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import static com.ovvium.services.util.common.domain.Identifiable.ID_PROPERTY;
import static com.ovvium.services.util.jpa.util.QuerydslUtils.*;
import static java.lang.String.format;

@Data
public class JpaRepository<T extends Identifiable<K> & DbIdentifiable, K extends Serializable> implements DefaultRepository<T, K> {

	private final EntityManager entityManager;
	private final Class<T> entityClass;
	private final Class<K> idClass;
	private final EntityPathBase<T> root;
	private final PathBuilder<T> builder;

	@SuppressWarnings("unchecked")
	public JpaRepository(EntityManager entityManager, Class<T> entityClass) {
		this.entityManager = entityManager;
		this.entityClass = entityClass;
		this.idClass = (Class<K>) GenericTypeResolver.resolveTypeArguments(entityClass, Identifiable.class)[0];
		this.root = getEntityPathBase(entityClass);
		this.builder = new PathBuilder<T>(entityClass, root.getMetadata());
	}

	@Override
	public T save(T entity) {
		if (entity.getDatabaseId() == null) {
			entityManager.persist(entity);
			return entity;
		}
		return entityManager.merge(entity);
	}

	/**
	 * Retrieve one entity by its id, throws exception if found more than one or
	 * empty optional if not exists.
	 */
	@Override
	public Optional<T> get(K id) {
		return get(builder.get(ID_PROPERTY, Serializable.class).eq(id));
	}

	/**
	 * Returns one entity bi its id, throws exception if found more than one or if
	 * not exists.
	 */
	@Override
	public T getOrFail(K id) {
		return get(id) //
				.orElseThrow( //
						() -> new EntityNotFoundException(format("Entity not found with id %s", id))//
				);
	}

	@Override
	public boolean remove(T entity) {
		return remove(entity.getId());
	}

	@Override
	public boolean remove(K id) {
		Assert.notNull(id, "Id is null!");
		val count = remove(builder.get(ID_PROPERTY, Serializable.class).eq(id));
		return count == 1;
	}

	@Override
	public List<T> list() {
		return query().list(root);
	}

	@Override
	public List<T> list(Request request) {
		return query(request).list(root);
	}

	@Override
	public Page<T> page(PageRequest request) {
		return page(null, request);
	}

	@Override
	public long count() {
		return count(new BooleanBuilder());
	}

	@Override
	public long count(Request request) {
		return count(getPredicate(builder, request.getFilters()), request.isDistinct());
	}

	public JPAQuery query() {
		return new JPAQuery(entityManager).from(root);
	}

	public JPAQuery query(EntityPathBase<?>... paths) {
		val query = query();
		for (val path : Traverser.of(paths)) {
			query.from(path);
		}
		return query;
	}

	public JPAQuery query(Request request) {
		val q = query(getPredicate(builder, request.getFilters()), getOrders(builder, request.getOrders()));
		if (request.isDistinct()) {
			q.distinct();
		}
		return q;
	}

	public JPAQuery query(Predicate predicate, OrderSpecifier<?>... orders) {
		val q = query().where(predicate);
		for (val order : orders) {
			q.orderBy(order);
		}
		return q;
	}

	/**
	 * @return unique result or null if none
	 * @throws NonUniqueResultException
	 *             if more than one result is returned
	 */
	public Optional<T> get(Predicate predicate) throws NonUniqueResultException {
		return Optional.ofNullable(query().where(predicate).uniqueResult(root));
	}

	public T getOrFail(Predicate predicate) throws NonUniqueResultException {
		return get(predicate) //
				.orElseThrow(() -> new EntityNotFoundException("Entity not found with this predicate."));
	}

	/**
	 * @return first result or null if none
	 */
	public T first(Predicate predicate, OrderSpecifier<?>... orders) {
		return query(predicate, orders).singleResult(root);
	}

	public List<T> list(Predicate predicate, OrderSpecifier<?>... orders) {
		return query(predicate, orders).list(root);
	}

	public long count(Predicate predicate) {
		return count(predicate, false);
	}

	public long count(Predicate predicate, boolean distinct) {
		val q = query().where(predicate);
		return distinct ? q.distinct().count() : q.count();
	}

	public Page<T> page(Predicate predicate, PageRequest request) {
		val q = query(request);
		if (predicate != null) {
			q.where(predicate);
		}
		return page(q, request.getPageNumber(), request.getPageSize());
	}

	public Page<T> page(JPAQuery query, int pageNumber, int pageSize) {
		val list = query.clone(entityManager).offset((long) pageNumber * pageSize).limit(pageSize).list(root);
		val count = list.size() < pageSize ? (((long) pageNumber * pageSize) + list.size()) : query.count();
		return new SimplePage<T>(list, pageNumber, pageSize, count);
	}

	public JPAUpdateClause update(Predicate predicate) {
		return new JPAUpdateClause(entityManager, root).where(predicate);
	}

	public long remove(Predicate predicate) {
		return new JPADeleteClause(entityManager, root).where(predicate).execute();
	}

}
