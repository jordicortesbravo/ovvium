package com.ovvium.services.util.ovvium.domain.entity;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseEntityTest {

	public class TestEntity extends BaseEntity {

		public TestEntity(UUID id){
			super(id);
		}

	}

	@Test
	public void given_set_of_entities_when_remove_entity_using_equals_and_hashchode_should_remove_entity_of_set() {
		UUID id = UUID.randomUUID();
		TestEntity testEntity = new TestEntity(id);
		HashSet<TestEntity> set = Sets.newHashSet(testEntity);

		TestEntity sameEntity = new TestEntity(id);
		boolean removed = set.remove(sameEntity);

		assertThat(removed).isTrue();
		assertThat(set).isEmpty();
	}
}