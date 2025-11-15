package com.ovvium.services.repository.client.social;

import com.ovvium.services.security.exception.InvalidTokenException;
import com.ovvium.services.util.ovvium.optional.OptionalUtils;
import com.ovvium.services.util.util.client.xson.XsonApiClient;
import com.ovvium.services.util.util.xprops.XProps;
import com.ovvium.services.util.util.xson.Xson;
import lombok.val;

import java.util.Map;
import java.util.Optional;

import static com.ovvium.services.model.user.SocialProvider.GOOGLE;
import static com.ovvium.services.security.exception.AccountError.INVALID_TOKEN;

public class GoogleClient extends SocialClient {

	public enum GoogleCredentialType {
		IOS, ANDROID, WEBAPP
	}

	public static final String TOKEN_ID_PARAM = "id_token";
	private final Map<GoogleCredentialType, String> keys;
	private final XsonApiClient client;

	public GoogleClient(XProps props) {
		this.keys = Map.of(
				GoogleCredentialType.IOS, props.getRequired("app.ios.key"),
				GoogleCredentialType.ANDROID, props.getRequired("app.android.key"),
				GoogleCredentialType.WEBAPP, props.getRequired("app.webapp.key")
		);
		this.client = new XsonApiClient(props.sub("client"));
	}

	public SocialProfileDto getProfile(String googleToken) {
		return buildIdentity(verifyAccessToken(googleToken));
	}

	private Xson verifyAccessToken(String googleToken) {
		val jwt = client.request("info").put(TOKEN_ID_PARAM, googleToken).get();
		if (!keys.containsValue(jwt.get("aud").asString())) {
			throw new InvalidTokenException(INVALID_TOKEN.name());
		}
		return jwt;
	}

	private SocialProfileDto buildIdentity(Xson jwt) {
		val identity = new SocialProfileDto(GOOGLE);
		get(jwt, "sub", Xson::asString).ifPresent(identity::setId);
		get(jwt, "email", Xson::asString).ifPresent(identity::setEmail);
		get(jwt, "name", Xson::asString)
				.or(() -> calculateFullName(jwt))
				.ifPresent(identity::setFullName);
		get(jwt, "picture", Xson::asUrl).ifPresent(identity::setProfileImage);
		return identity;
	}

	private Optional<String> calculateFullName(Xson jwt) {
		String name = "";
		Optional<String> givenName = get(jwt, "given_name", Xson::asString);
		if(givenName.isPresent()){
			name += givenName.get();
		}
		val familyName = get(jwt, "family_name", Xson::asString);
		if(familyName.isPresent()){
			name += name.isBlank() ? familyName.get() : " "+ familyName.get();
		}
		return OptionalUtils.ofBlankable(name);
	}

}
