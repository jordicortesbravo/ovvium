package com.ovvium.services.repository.impl;

import com.ovvium.services.model.user.apikey.ApiKey;
import com.ovvium.services.repository.ApiKeyRepository;
import com.ovvium.services.util.jpa.core.JpaDefaultRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ApiKeyRepositoryImpl extends JpaDefaultRepository<ApiKey, UUID> implements ApiKeyRepository {

}
