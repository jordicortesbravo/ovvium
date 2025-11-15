package com.ovvium.services.repository.impl;

import com.ovvium.services.model.rating.AverageRating;
import com.ovvium.services.repository.AverageRatingRepository;
import com.ovvium.services.util.jpa.core.JpaDefaultRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class AverageRatingRepositoryImpl extends JpaDefaultRepository<AverageRating, UUID>
		implements AverageRatingRepository {


}
