package com.ovvium.services.util.ovvium.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@MappedSuperclass
public abstract class TimestampedEntity {

	public static final String CREATED = "created";
	public static final String UPDATED = "updated";

	@Setter(PRIVATE)
	@Column(updatable = false)
	private Instant created = Instant.now();

	@Setter(PROTECTED)
	private Instant updated = Instant.now();
	
	@PreUpdate
	private void preUpdateTimestamp() {
		this.updated = Instant.now();
		preUpdate();
	}

	protected void preUpdate() {
		// Override for extension
	}
}
