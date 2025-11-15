package com.ovvium.services.web.controller.bff.v1.transfer.request.product;

import com.ovvium.services.model.user.Allergen;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.util.Collections;
import java.util.Set;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class CreateProductItemRequest extends CreateProductRequest {

	@NotEmpty
	private String type;

	private Set<Allergen> allergens = Collections.emptySet();
}
