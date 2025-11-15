package com.ovvium.services.repository;

import java.util.UUID;

import com.ovvium.services.model.rating.AverageRating;
import com.ovvium.services.util.jpa.core.DefaultRepository;

public interface AverageRatingRepository extends DefaultRepository<AverageRating, UUID> {

}
