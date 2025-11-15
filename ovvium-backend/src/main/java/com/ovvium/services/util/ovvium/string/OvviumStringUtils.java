package com.ovvium.services.util.ovvium.string;

import com.ovvium.services.util.util.string.StringUtils;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.lang.RandomStringUtils;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

@UtilityClass
public class OvviumStringUtils {

	private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

	public static final String EMAIL_FAKE_DOMAIN = "fake.ovvium.com";
	public static final String EMAIL_REMOVED_DOMAIN = "removed.ovvium.com";

	public static String createEmail(String id, String domain) {
		return String.format("%s@%s", id, domain);
	}

	public static String randomPassword() {
		val characterList = String.join("",
				RandomStringUtils.random(2, 65, 90, true, true), // uppercase
				RandomStringUtils.random(2, 97, 122, true, true), // lowercase
				RandomStringUtils.randomNumeric(1), // number
				RandomStringUtils.random(1, 35, 38, false, false), //special char
				RandomStringUtils.randomAlphanumeric(2) // random char
		).chars()
				.mapToObj(c -> (char) c)
				.collect(Collectors.toList());
		Collections.shuffle(characterList);
		return characterList.stream()
				.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
				.toString();
	}

	public static String randomAlphanumeric(int length) {
		return StringUtils.randomString(length, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
	}

	public static String randomUniqueString() {
		return ENCODER.encodeToString(uuidToBytes(UUID.randomUUID()));
	}

	private byte[] uuidToBytes(UUID uuid) {
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		return bb.array();
	}
}
