package com.ovvium.services.model.customer;

import com.ovvium.services.util.util.string.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

import static com.ovvium.services.util.ovvium.base.Preconditions.check;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotBlank;
import static lombok.AccessLevel.PROTECTED;


@Embeddable
@NoArgsConstructor(access = PROTECTED)
public final class TagId {

	private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int TAG_SIZE = 10;

	@Getter
	private String value;

	public TagId(String tagId) {
		checkNotBlank(tagId, "Tag Id cannot be blank");
		this.value = check(tagId, isTagIdFormat(tagId), "This id is not a TagId");
	}

	private boolean isTagIdFormat(String tagId) {
		return tagId.length() == TAG_SIZE && org.apache.commons.lang.StringUtils.containsOnly(tagId, CHARACTERS);
	}

	private static String generateTagId() {
		return StringUtils.randomString(TAG_SIZE, CHARACTERS);
	}

	public static TagId randomTagId() {
		return new TagId(generateTagId());
	}
}
