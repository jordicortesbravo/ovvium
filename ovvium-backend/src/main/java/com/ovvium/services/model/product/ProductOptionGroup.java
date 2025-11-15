package com.ovvium.services.model.product;


import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.util.ovvium.base.Preconditions;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Accessors(chain = true)
public class ProductOptionGroup extends BaseEntity {

    public enum ProductOptionType {
        SINGLE, MULTI
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "defaultValue", column = @Column(name = "title_default_value")),
            @AttributeOverride(name = "translations", column = @Column(name = "title_translations"))
    })
    private MultiLangString title;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProductOptionType type;

    @JoinColumn(name = "option_group_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> options;

    private boolean required = false;

    public ProductOptionGroup(MultiLangString title, ProductOptionType type, List<ProductOption> choices, boolean required) {
        setTitle(title);
        setType(type);
        setChoices(choices);
        this.required = required;
    }

    public ProductOptionGroup setTitle(MultiLangString title) {
        this.title = Preconditions.checkNotNull(title, "title can't be blank");
        return this;
    }

    public ProductOptionGroup setType(ProductOptionType type) {
        this.type = Preconditions.checkNotNull(type, "type can't be blank");
        return this;
    }

    public ProductOptionGroup setChoices(List<ProductOption> options) {
        this.options = Preconditions.checkMinSize(options, 2, "choices must have at least 2 options");
        return this;
    }
}
