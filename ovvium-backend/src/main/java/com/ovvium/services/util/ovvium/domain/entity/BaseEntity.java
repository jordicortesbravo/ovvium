package com.ovvium.services.util.ovvium.domain.entity;

import com.ovvium.services.util.common.domain.Identifiable;
import com.ovvium.services.util.common.domain.Identifiables;
import com.ovvium.services.util.jpa.core.DbIdentifiable;
import com.ovvium.services.util.ovvium.base.Preconditions;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import java.util.UUID;

import static com.ovvium.services.util.ovvium.domain.entity.TypeConstants.PG_UUID;

@RequiredArgsConstructor
@MappedSuperclass
public abstract class BaseEntity extends TimestampedEntity implements Identifiable<UUID>, DbIdentifiable {

	private static final String SEQUENCE = "ovvium_sequence";

	@Id
	@SequenceGenerator(name = SEQUENCE, sequenceName = SEQUENCE, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE)
	private Long surrogateId;

	@Type(type = PG_UUID)
	@Column(unique = true, updatable = false, nullable = false)
	private UUID id = UUID.randomUUID();

	protected BaseEntity(UUID id) {
		this.id = Preconditions.checkNotNull(id, "Id can´t be null");
	}

	@Override
	public String toString() {
		return String.valueOf(id);
	}

	@Override
	public boolean equals(Object other) {
		return Identifiables.equals(this, other);
	}

	@Override
	public int hashCode() {
		return Identifiables.hashCode(this);
	}

	@Override
	public Long getDatabaseId() {
		return surrogateId;
	}

	@Override
	public UUID getId() {
		return id;
	}

}
