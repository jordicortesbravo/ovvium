package com.ovvium.services.repository;

import com.ovvium.services.model.user.User;
import com.ovvium.services.repository.transfer.SimpleUserDto;
import com.ovvium.services.util.jpa.core.DefaultRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserRepository extends DefaultRepository<User, UUID> {

	Optional<User> get(String email);

	User getOrFail(String email);

	Optional<User> getByAppleId(String appleId);

    Set<SimpleUserDto> getNotVerifiedUserEmails();
}
