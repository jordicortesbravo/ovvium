package com.ovvium.services.service;

import com.ovvium.mother.model.CategoryMother;
import com.ovvium.mother.model.CustomerMother;
import com.ovvium.mother.model.ProductMother;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.product.Product;
import com.ovvium.services.model.product.ProductGroup;
import com.ovvium.services.model.product.ProductType;
import com.ovvium.services.repository.CategoryRepository;
import com.ovvium.services.repository.ProductRepository;
import com.ovvium.services.service.impl.ProductServiceImpl;
import com.ovvium.services.transfer.command.product.CreateProductGroupCommand;
import com.ovvium.services.transfer.command.product.CreateProductItemCommand;
import com.ovvium.services.util.util.container.Pair;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import lombok.val;
import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Map;

import static com.ovvium.mother.model.CustomerMother.EL_BULLI_CUSTOMER_ID;
import static com.ovvium.mother.model.ProductMother.PATATAS_BRAVAS_ID;
import static com.ovvium.services.model.bill.ServiceBuilderLocation.KITCHEN;
import static com.ovvium.services.model.bill.ServiceTime.SOONER;
import static com.ovvium.utils.MockitoUtils.mockRepository;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductServiceTest {

    // SUT
    private ProductService productService;

    private ProductRepository productRepository;

    @Before
    public void setUp() {
        CustomerService customerService = mock(CustomerService.class);
        productRepository = mockRepository(ProductRepository.class);
        CategoryRepository categoryRepository = mockRepository(CategoryRepository.class);
        productService = new ProductServiceImpl(customerService, productRepository, categoryRepository);
        ReflectionUtils.set(productService, "self", productService);
    }

    @Test
    public void given_create_product_request_and_last_order_when_create_product_then_should_add_one_to_last_order() {
        var bulliCustomer = CustomerMother.getElBulliCustomer();
        var request = new CreateProductItemCommand(
                bulliCustomer,
                CategoryMother.getBebidasCategory(bulliCustomer),
                new MultiLangString("Name"),
                null,
                ProductType.DRINK,
                KITCHEN,
                MoneyAmount.ofDouble(2d),
                0.5d,
                emptySet(),
                null,
                null
        );

        when(productRepository.getLastOrder(eq(EL_BULLI_CUSTOMER_ID), any())).thenReturn(23);

        Product product = productService.create(request);

        assertThat(product.getOrder()).isEqualTo(24);
    }

    @Test
    public void given_create_product_group_request_when_create_product_group_then_should_create_product_group_correctly() {
        var bulliCustomer = CustomerMother.getElBulliCustomer();
        val request = new CreateProductGroupCommand(
                bulliCustomer,
                CategoryMother.getBebidasCategory(bulliCustomer),
                new MultiLangString("Name"),
                null,
                KITCHEN,
                MoneyAmount.ofDouble(2d),
                0.5d,
                emptySet(),
                null,
                EnumSet.of(DayOfWeek.MONDAY),
                LocalTime.parse("12:00:00"),
                LocalTime.parse("16:00:00"),
                Map.of(
                        SOONER, singleton(ProductMother.getPatatasBravasProduct())
                ),
                null);

        Product productItem = ProductMother.getPatatasBravasProduct();
        when(productRepository.getLastOrder(eq(EL_BULLI_CUSTOMER_ID), any())).thenReturn(23);
        when(productRepository.getOrFail(PATATAS_BRAVAS_ID)).thenReturn(productItem);

        ProductGroup product = productService.createGroup(request);

        assertThat(product.getOrder()).isEqualTo(24);
        assertThat(product.getProductItem(PATATAS_BRAVAS_ID)).isEqualTo(Pair.makePair(productItem, SOONER));
        assertThat(product.getDaysOfWeek()).isEqualTo(request.daysOfWeek());
        assertThat(product.getStartTime()).isEqualTo(LocalTime.of(12, 0, 0));
        assertThat(product.getEndTime()).isEqualTo(LocalTime.of(16, 0, 0));
    }

//    @Test
//    public void given_create_product_with_options_request_then_should_should_create_product_correctly() {
//        CreateProductItemRequest request = new CreateProductItemRequest()
//                .setType("DRINK")
//                .setCustomerId(EL_BULLI_CUSTOMER_ID)
//                .setServiceBuilderLocation(KITCHEN)
//                .setCategoryId(UUID.randomUUID())
//                .setBasePrice(1d)
//                .setTax(0.1d)
//                .setLocalizations(Collections.singletonList(new ProductLocalizationRequest()
//                        .setLocale(SPANISH.toLanguageTag())
//                        .setName("Café")))
//                .setOptions(Collections.singletonList(
//                        new ProductItemOptionGroupRequest().setTitle(new MultiLangStringRequest().setDefaultValue("¿Cómo quieres el café?"))
//                                .setRequired(true)
//                                .setType(ProductOptionGroup.ProductOptionType.SINGLE)
//                                .setChoices(Arrays.asList(
//                                        new ProductItemOptionRequest().setTitle(new MultiLangStringRequest("Sólo")).setBasePrice(MoneyAmount.ZERO).setTax(0.1d),
//                                        new ProductItemOptionRequest().setTitle(new MultiLangStringRequest("Irlandés")).setBasePrice(MoneyAmount.ofDouble(0.5d)).setTax(0.1d)
//                                ))
//                ));
//
//        Customer customer = CustomerMother.getElBulliCustomer();
//        when(customerService.getCustomer(EL_BULLI_CUSTOMER_ID)).thenReturn(customer);
//        when(categoryRepository.getOrFail(any())).thenReturn(CategoryMother.getBebidasCategory(customer));
//        when(productRepository.getLastOrder(eq(EL_BULLI_CUSTOMER_ID), any())).thenReturn(23);
//
//        ProductItem product = (ProductItem) productService.create(request);
//
//        assertThat(product.getOrder()).isEqualTo(24);
//        assertThat(product.getOptionGroups()).hasSize(1);
//        List<ProductOption> options = Utils.first(product.getOptionGroups()).getOptions();
//        assertThat(Utils.first(product.getOptionGroups()).isRequired()).isTrue();
//        assertThat(options.size()).isEqualTo(2);
//        ProductOption productOption = Utils.first(options);
//        assertThat(productOption.getTitle().getDefaultValue()).isEqualTo("Sólo");
//        assertThat(productOption.getBasePrice()).isEqualTo(MoneyAmount.ZERO);
//        assertThat(productOption.getTax()).isEqualTo(0.1d);
//    }
//
//
//    @Test
//    public void given_invalid_create_product_group_request_when_create_product_group_then_should_throw_validation_error() {
//        CreateProductGroupRequest request = new CreateProductGroupRequest()
//                .setCustomerId(EL_BULLI_CUSTOMER_ID);
//
//        assertThatThrownBy(() -> productService.createGroup(request))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("serviceBuilderLocation must not be null");
//    }
//
//    @Test
//    public void given_product_group_as_choice_instead_of_item_when_create_product_group_then_should_throw_exception() {
//        CreateProductGroupRequest request = new CreateProductGroupRequest()
//                .setCustomerId(EL_BULLI_CUSTOMER_ID)
//                .setServiceBuilderLocation(KITCHEN)
//                .setCategoryId(UUID.randomUUID())
//                .setBasePrice(2d)
//                .setTax(0.5d)
//                .setLocalizations(Collections.singletonList(new ProductLocalizationRequest()
//                        .setLocale(SPANISH.toLanguageTag())
//                        .setName("Name")))
//                .setProductIds(Map.of(
//                        SOONER, Set.of(MENU_PRODUCT_ID)
//                ));
//
//        Product productItem = ProductMother.getMenuDiarioProduct();
//        Customer customer = CustomerMother.getElBulliCustomer();
//        when(customerService.getCustomer(EL_BULLI_CUSTOMER_ID)).thenReturn(customer);
//        when(categoryRepository.getOrFail(any())).thenReturn(CategoryMother.getBebidasCategory(customer));
//        when(productRepository.getOrFail(MENU_PRODUCT_ID)).thenReturn(productItem);
//
//        assertThatThrownBy(() ->
//                productService.createGroup(request)
//        ).isInstanceOf(IllegalStateException.class)
//                .hasMessage("Class is not from type ProductItem");
//    }
}