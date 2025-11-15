package com.ovvium.services.model.product;

import com.ovvium.services.util.ovvium.base.Preconditions;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.Entity;
import java.net.URI;
import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotNull;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Accessors(chain = true)
public class Picture extends BaseEntity {

	private static final String DEFAULT_EXT = ".jpg";

	private URI baseUri;
	private String filename;

	public Picture(String basePath, String filename) {
		this.baseUri = getPictureBaseUri(checkNotNull(basePath, "Basepath can't be null"));
		this.filename = Preconditions.checkNotBlank(filename, "Filename cannot be blank");
	}

	public Map<PictureSize, URI> getCrops() {
		return Stream.of(PictureSize.values())
				.collect(Collectors.toMap(
						Function.identity(),
						this::getUriOf
				));
	}

	public URI getUriOf(PictureSize size) {
		return UriComponentsBuilder.fromUri(baseUri)
				.pathSegment(size.name().toLowerCase() + DEFAULT_EXT)
				.build().toUri();
	}

	private URI getPictureBaseUri(String basePath) {
		return UriComponentsBuilder.fromUriString(basePath)
				.pathSegment(
						LocalDate.now().toString().replace("-",""),
						getId().toString()
				).build().toUri();
	}

}
