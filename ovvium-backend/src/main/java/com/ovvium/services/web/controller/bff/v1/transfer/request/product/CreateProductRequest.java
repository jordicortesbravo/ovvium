package com.ovvium.services.web.controller.bff.v1.transfer.request.product;

import com.ovvium.services.model.bill.ServiceBuilderLocation;
import com.ovvium.services.transfer.request.common.MultiLangStringRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public abstract class CreateProductRequest {

    @NotNull
    private Double basePrice;

    @NotNull
    private Double tax;

    @NotNull
    private ServiceBuilderLocation serviceBuilderLocation;

    @NotNull
    private UUID categoryId;

    @NotNull
    private MultiLangStringRequest name;

    private MultiLangStringRequest description;

    private UUID coverPictureId;

    private List<ProductItemOptionGroupRequest> options;

    public Optional<UUID> getCoverPictureId() {
        return Optional.ofNullable(coverPictureId);
    }

    public Optional<List<ProductItemOptionGroupRequest>> getOptions() {
        return Optional.ofNullable(options);
    }

    public Optional<MultiLangStringRequest> getDescription() {
        return Optional.ofNullable(description);
    }
}
