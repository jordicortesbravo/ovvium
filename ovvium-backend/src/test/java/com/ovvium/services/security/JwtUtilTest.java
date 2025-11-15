package com.ovvium.services.security;

import com.ovvium.mother.model.UserMother;
import com.ovvium.services.security.exception.InvalidTokenException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.ovvium.services.security.exception.AccountError.INVALID_TOKEN;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;

public class JwtUtilTest {

    private static final String SECRET_KEY = "a_secret_key";
    private static final String DIFF_SECRET_KEY = "more_different_secret";

    @Test
    void given_secret_when_encode_jwt_and_decode_then_should_return_correct_values() {
        var user = new AuthenticatedUser(UserMother.getUserJorge());
        var accessToken = JwtUtil.generateAccessToken(user, SECRET_KEY, Instant.now().plus(2, DAYS));

        var authenticatedUser = JwtUtil.parseToken(accessToken, SECRET_KEY);

        assertThat(authenticatedUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void given_different_secret_when_decode_should_throw_signature_exception() {
        var user = new AuthenticatedUser(UserMother.getUserJorge());
        var accessToken = JwtUtil.generateAccessToken(user, SECRET_KEY, Instant.now().plus(2, DAYS));

        // This second secret key needs to be a lot more different,
        // check this https://stackoverflow.com/questions/38812424/jwt-token-verification-with-java
        Assertions.assertThatThrownBy(() ->
            JwtUtil.parseToken(accessToken, DIFF_SECRET_KEY)
        ).isInstanceOf(InvalidTokenException.class)
                .hasMessage(INVALID_TOKEN.name());
    }
}