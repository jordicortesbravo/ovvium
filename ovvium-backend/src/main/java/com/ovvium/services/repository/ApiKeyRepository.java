package com.ovvium.services.repository;

import com.ovvium.services.model.user.apikey.ApiKey;
import com.ovvium.services.util.jpa.core.DefaultRepository;

import java.util.UUID;

public interface ApiKeyRepository extends DefaultRepository<ApiKey, UUID> {


}
