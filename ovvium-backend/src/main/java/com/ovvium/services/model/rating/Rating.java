package com.ovvium.services.model.rating;

import com.ovvium.services.model.common.RatingAmount;
import com.ovvium.services.model.product.Product;
import com.ovvium.services.model.user.User;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.util.Optional;
import java.util.UUID;

import static com.ovvium.services.util.ovvium.base.Preconditions.checkMaxCharacters;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;
import static com.ovvium.services.util.ovvium.domain.entity.TypeConstants.PG_UUID;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Accessors(chain = true)
@NoArgsConstructor(access = PROTECTED)
public class Rating extends BaseEntity {

	private static final int MAX_COMMENT_SIZE = 500;

	@Type(type = PG_UUID)
	private UUID productId;

	@Type(type = PG_UUID)
	private UUID userId;

	private String userName;

	@Embedded
	private RatingAmount rating;

	@Column(length = MAX_COMMENT_SIZE)
	private String comment;

	public Rating(Product product, User user, RatingAmount rating) {
		this.productId = checkNotNull(product, "Product can't be null").getId();
		this.userId = checkNotNull(user, "User can't be null").getId();
		this.userName = checkNotNull(user, "User can't be null").getName();
		setRating(rating);
	}


	public Rating setRating(RatingAmount rating) {
		this.rating = checkNotNull(rating, "Rating amount can't be null");
		return this;
	}

	public UUID getProductId() {
		return productId;
	}

	public UUID getUserId() {
		return userId;
	}

	public Rating setComment(String comment) {
		this.comment = comment == null ? null
				: checkMaxCharacters(comment, MAX_COMMENT_SIZE,
						String.format("Comment size maximum allowed of %d characters.", MAX_COMMENT_SIZE));
		return this;
	}

	public Optional<String> getComment() {
		return Optional.ofNullable(comment);
	}

}
