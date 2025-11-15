package com.ovvium.services.repository.impl;

import com.mysema.query.types.ConstructorExpression;
import com.ovvium.services.model.user.QUser;
import com.ovvium.services.model.user.User;
import com.ovvium.services.repository.UserRepository;
import com.ovvium.services.repository.transfer.SimpleUserDto;
import com.ovvium.services.util.jpa.core.JpaDefaultRepository;
import com.ovvium.services.util.util.basic.Utils;
import lombok.val;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ovvium.services.util.ovvium.string.OvviumStringUtils.EMAIL_FAKE_DOMAIN;
import static com.ovvium.services.util.ovvium.string.OvviumStringUtils.EMAIL_REMOVED_DOMAIN;

@Repository
public class UserRepositoryImpl extends JpaDefaultRepository<User, UUID> implements UserRepository {

	private static final QUser qUser = QUser.user;

	@Override
	public Optional<User> get(String email) {
		return get(qUser.email.eq(email));
	}
	
	@Override
	public Optional<User> getByAppleId(String appleId) {
		val users = list(qUser.appleProfile.id.eq(appleId)).stream()//
				.sorted((u1,u2) -> u1.getEmail().contains(EMAIL_FAKE_DOMAIN) ? 1 : -1)
				.collect(Collectors.toList());
		return users.size() == 0 ? Optional.empty() : Optional.of(Utils.first(users));
	}

	@Override
	public User getOrFail(String email) {
		return get(email) //
				.orElseThrow(() -> new EntityNotFoundException("User not found for email " + email));
	}

	@Override
	public Set<SimpleUserDto> getNotVerifiedUserEmails() {
		return new HashSet<>(query()
				.where(qUser.enabled.isFalse().and(qUser.email.endsWith(EMAIL_REMOVED_DOMAIN).not()))
				.list(ConstructorExpression.create(SimpleUserDto.class, qUser.id, qUser.email)));
	}
}
