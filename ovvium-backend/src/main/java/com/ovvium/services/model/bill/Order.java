package com.ovvium.services.model.bill;

import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.model.exception.ResourceNotFoundException;
import com.ovvium.services.model.product.*;
import com.ovvium.services.model.user.User;
import com.ovvium.services.transfer.command.order.OrderProductCommand;
import com.ovvium.services.util.ovvium.domain.DomainStatus;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import com.ovvium.services.util.util.container.Pair;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.ovvium.services.model.bill.PaymentStatus.PAID;
import static com.ovvium.services.model.bill.PaymentStatus.PENDING;
import static com.ovvium.services.model.exception.ErrorCode.ORDER_ALREADY_PAID;
import static com.ovvium.services.util.ovvium.base.Preconditions.*;
import static java.lang.String.format;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Where(clause = "status != 'DELETED'")
@Getter
@Accessors(chain = true)
@NoArgsConstructor(access = PROTECTED)
public class Order extends BaseEntity {

    private static final int MAX_NOTES_SIZE = 200;

    @Setter
    @ManyToOne
    private User user;

    @ManyToOne
    private Product product;

    @Enumerated(STRING)
    private PaymentStatus paymentStatus = PENDING;

    @Setter
    @Enumerated(STRING)
    private IssueStatus issueStatus = IssueStatus.PENDING;

    @Setter
    @Enumerated(STRING)
    private ServiceTime serviceTime = ServiceTime.SOONER;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "base_price_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "base_price_currency"))
    })
    private MoneyAmount basePrice;

    private double tax;

    @Column(length = MAX_NOTES_SIZE)
    private String notes;

    @Enumerated(STRING)
    private DomainStatus status = DomainStatus.CREATED;

    /**
     * Selected choices in ProductGroup's order
     */
    @OneToMany(cascade = ALL)
    @JoinColumn(name = "order_id")
    private Set<OrderGroupChoice> choices = new HashSet<>();

    /**
     * Selected product options for the order
     */
//	@Setter
    @JoinColumn(name = "o_order_id")
    @OneToMany(cascade = ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<SelectedProductOption> selectedOptions = new HashSet<>();

    private Order(Product product) {
        this.product = checkNotNull(product, "Product can't be null");
        this.basePrice = product.getBasePrice();
        this.tax = product.getTax();
    }

    public MoneyAmount getPrice() {
        return getTotalPrice(basePrice, tax).add(getSelectedOptionsTotalPrice());
    }

    public MoneyAmount getBasePrice() {
        return basePrice.add(getSelectedOptionsBasePrice());
    }

    public boolean isPaid() {
        return this.paymentStatus == PAID;
    }

    public Order markAsPaid() {
        check(!isPaid(), new OvviumDomainException(ORDER_ALREADY_PAID));
        this.paymentStatus = PAID;
        return this;
    }

    public Order setNotes(String notes) {
        this.notes = checkMaxCharacters(notes, MAX_NOTES_SIZE,
                format("Notes size maximum allowed of %d characters.", MAX_NOTES_SIZE));
        return this;
    }

    public Optional<String> getNotes() {
        return Optional.ofNullable(notes);
    }

    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }

    public Order delete() {
        this.status = DomainStatus.DELETED;
        this.choices.forEach(OrderGroupChoice::delete);
        return this;
    }

    public Set<OrderGroupChoice> getChoices() {
        return Collections.unmodifiableSet(choices);
    }

    public Set<SelectedProductOption> getSelectedOptions() {
        return Collections.unmodifiableSet(selectedOptions);
    }

    public OrderGroupChoice getChoice(UUID orderGroupChoiceId) {
        return choices.stream()
                .filter(orderGroupChoice -> orderGroupChoice.getId().equals(orderGroupChoiceId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(String.format("OrderGroupChoice with id %s is not on Order %s", orderGroupChoiceId, getId())));
    }

    public static Order oneOfValues(OrderProductCommand values) {
        val product = values.product();
        val order = new Order(product);
        if (product instanceof ProductGroup) {
            order.choices = checkNotEmpty(values.groupChoices(), "Order Group choices cannot be empty").stream()
                    .map(it -> {
                        Pair<ProductItem, ServiceTime> pair = product.as(ProductGroup.class).getProductItem(it.productId());
                        OrderGroupChoice groupChoice = new OrderGroupChoice(pair.getSecond(), pair.getFirst());
                        it.getNotes().ifPresent(groupChoice::setNotes);
                        return groupChoice;
                    }).collect(Collectors.toSet());
        }
        order.selectedOptions.clear();
        product.getOptionGroups().forEach(group ->
                group.getOptions()
                        .stream()
                        .filter(o -> values.selectedOptions().contains(o.getId()))
                        .forEach(order::addSelectedOption)
        );
        // The order can be taken by a waiter. In that case there is no relationship with the user
        values.getUser().ifPresent(order::setUser);
        values.getServiceTime().ifPresent(order::setServiceTime);
        values.getNotes().ifPresent(order::setNotes);
        return order;
    }

    public Order addSelectedOption(ProductOption option) {
        this.selectedOptions.add(new SelectedProductOption(option));
        return this;
    }

    public Order clearSelectedOptions() {
        this.selectedOptions.clear();
        return this;
    }

    private MoneyAmount getTotalPrice(MoneyAmount basePrice, double tax) {
        return basePrice.add(basePrice.multiply(tax));
    }

    private MoneyAmount getSelectedOptionsTotalPrice() {
        return selectedOptions.stream()
                .filter(o -> !o.isFree())
                .map(SelectedProductOption::getPrice)
                .reduce(MoneyAmount.ZERO, MoneyAmount::add);
    }

    private MoneyAmount getSelectedOptionsBasePrice() {
        return selectedOptions.stream()
                .filter(o -> !o.isFree())
                .map(SelectedProductOption::getBasePrice)
                .reduce(MoneyAmount.ZERO, MoneyAmount::add);
    }
}
