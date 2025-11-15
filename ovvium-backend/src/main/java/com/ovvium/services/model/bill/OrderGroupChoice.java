package com.ovvium.services.model.bill;

import com.ovvium.services.model.product.Product;
import com.ovvium.services.util.ovvium.domain.DomainStatus;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.util.Optional;

import static com.ovvium.services.util.ovvium.base.Preconditions.checkMaxCharacters;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Accessors(chain = true)
@NoArgsConstructor(access = PROTECTED)
@Where(clause = "status != 'DELETED'")
public class OrderGroupChoice extends BaseEntity {

	private static final int MAX_NOTES_SIZE = 200;

	@ManyToOne
	private Product product;

	@Setter
	@Enumerated(STRING)
	private IssueStatus issueStatus = IssueStatus.PENDING;

	@Enumerated(STRING)
	private ServiceTime serviceTime;

	@Column(length = MAX_NOTES_SIZE)
	private String notes;

	@Enumerated(STRING)
	private DomainStatus status = DomainStatus.CREATED;

	public OrderGroupChoice(ServiceTime serviceTime, Product product) {
		setProduct(product);
		this.serviceTime = checkNotNull(serviceTime, "ServiceTime can't be null");
	}

	public OrderGroupChoice setProduct(Product product) {
		this.product = checkNotNull(product, "Product can't be null");
		return this;
	}

	public OrderGroupChoice setNotes(String notes) {
		this.notes = checkMaxCharacters(notes, MAX_NOTES_SIZE,
				format("Notes size maximum allowed of %d characters.", MAX_NOTES_SIZE));
		return this;
	}

	public Optional<String> getNotes() {
		return Optional.ofNullable(notes);
	}

	OrderGroupChoice delete() {
		this.status = DomainStatus.DELETED;
		return this;
	}

}
