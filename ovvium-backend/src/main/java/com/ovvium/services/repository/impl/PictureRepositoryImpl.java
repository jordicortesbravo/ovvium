package com.ovvium.services.repository.impl;

import com.ovvium.services.model.product.Picture;
import com.ovvium.services.repository.PictureRepository;
import com.ovvium.services.util.jpa.core.JpaDefaultRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public class PictureRepositoryImpl extends JpaDefaultRepository<Picture, UUID> implements PictureRepository {

}
