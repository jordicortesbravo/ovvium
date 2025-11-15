package com.ovvium.services.repository.client.social;

import com.ovvium.services.security.exception.InvalidTokenException;
import com.ovvium.services.util.util.client.xson.XsonApiClient;
import com.ovvium.services.util.util.xprops.XProps;
import com.ovvium.services.util.util.xson.Xson;
import lombok.SneakyThrows;
import lombok.val;

import java.util.Map;

import static com.ovvium.services.model.user.SocialProvider.FACEBOOK;
import static com.ovvium.services.security.exception.AccountError.INVALID_TOKEN;
import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

public class FacebookClient extends SocialClient {

	private final String key;
	private final String secret;
	private final XsonApiClient client;

	public FacebookClient(XProps props) {
		this.key = props.getRequired("app.key");
		this.secret = props.getRequired("app.secret");
		this.client = new XsonApiClient(props.sub("client"));
	}

	public SocialProfileDto getProfile(String facebookToken) {
		verifyUserAccessToken(facebookToken);

		val infoReq = client.request("info" );
		val infoParams = Map.<String, Object>of(
				"fields", "id,name,email,picture",
				"access_token", facebookToken
		);
		infoReq.setQueryString(toQueryString(infoParams));
		val info = infoReq.get();

		val profile = new SocialProfileDto(FACEBOOK);
		get(info, "id", Xson::asString).ifPresent(profile::setId);
		get(info, "email", Xson::asString).ifPresent(profile::setEmail);
		get(info, "name", Xson::asString).ifPresent(profile::setFullName);

		val pictureParams = Map.of(
				"userId", profile.getId().orElseThrow(() -> new IllegalArgumentException("userId required to get Facebook profile" )),
				"height", 150,
				"width", 150,
				"redirect", false
		);
		val pictureReq = client.request("picture" ).put(pictureParams);
		val picture = pictureReq.get();
		get(picture, "data/url", Xson::asUrl)
				.ifPresent(profile::setProfileImage);

		return profile;
	}

	private void verifyUserAccessToken(String facebookToken) {
		val infoReq = client.request("verifyToken" );
		val infoParams = Map.<String, Object>of(
				"input_token", facebookToken,
				"access_token", getAppAccessToken()
		);
		infoReq.setQueryString(toQueryString(infoParams));
		try {
			val info = infoReq.get();
			if (!key.equals(info.get("data/app_id" ).asString())) {
				throw new InvalidTokenException(INVALID_TOKEN.name());
			}
		} catch (Exception e) {
			throw new InvalidTokenException(INVALID_TOKEN.name());
		}
	}

	@SneakyThrows
	private String getAppAccessToken() {
		val tokenReq = client.request("accessToken" );
		val tokenParams = Map.<String, Object>of(
				"client_id", key,
				"client_secret", secret,
				"grant_type", "client_credentials"
		);
		tokenReq.setQueryString(toQueryString(tokenParams));
		val token = tokenReq.get().get("access_token" ).asString();

		return encode(token, UTF_8);
	}
}
