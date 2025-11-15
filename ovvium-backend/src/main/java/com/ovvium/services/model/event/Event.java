package com.ovvium.services.model.event;

import com.google.gson.Gson;
import com.ovvium.services.util.ovvium.domain.DomainStatus;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import com.ovvium.services.util.ovvium.domain.event.OvviumEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.val;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import java.util.Optional;
import java.util.UUID;

import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;
import static com.ovvium.services.util.ovvium.domain.DomainStatus.DELETED;
import static com.ovvium.services.util.ovvium.domain.entity.TypeConstants.PG_UUID;
import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Accessors(chain = true)
@Where(clause = "status != 'DELETED'")
public class Event extends BaseEntity {

	private static final Gson GSON = new Gson();

	private String type;
	private String content;

	// Used only by Hazelcast
	@Setter
	private Long queueId;

	@Type(type = PG_UUID)
	private UUID userId;

	private String path;

	@Enumerated(STRING)
	private DomainStatus status = DomainStatus.CREATED;

	public Event(OvviumEvent event) {
		this.content = GSON.toJson(checkNotNull(event, "Event object cannot be null"));
		this.type = getEventName(event.getClass());
		this.userId = event.getRequestUserId().orElse(null);
		this.path = event.getRequestPath().orElse(null);
	}

	@SneakyThrows
	public <T extends OvviumEvent> T asOvviumEvent() {
		val aClass = (Class<T>) Class.forName(type);
		return GSON.fromJson(content, aClass);
	}

	public Event delete() {
		this.status = DELETED;
		return this;
	}

	public Optional<String> getPath() {
		return Optional.ofNullable(path);
	}

	public Optional<UUID> getUserId() {
		return Optional.ofNullable(userId);
	}

	public Optional<Long> getQueueId() {
		return Optional.ofNullable(queueId);
	}

	public static String getEventName(Class<? extends OvviumEvent> eventClass) {
		return eventClass.getName();
	}
}
