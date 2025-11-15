package com.ovvium.services.model.product;

import com.ovvium.mother.builder.CustomerBuilder;
import com.ovvium.mother.builder.ProductGroupBuilder;
import com.ovvium.mother.model.CategoryMother;
import com.ovvium.mother.model.CustomerMother;
import com.ovvium.mother.model.ProductMother;
import com.ovvium.services.model.bill.ServiceBuilderLocation;
import com.ovvium.services.model.bill.ServiceTime;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.customer.Customer;
import com.ovvium.services.util.util.container.Pair;
import org.junit.Test;

import java.time.*;
import java.util.ArrayList;
import java.util.EnumSet;

import static com.ovvium.mother.model.ProductMother.CERVEZA_ID;
import static com.ovvium.services.model.bill.ServiceTime.SECOND_COURSE;
import static java.time.DayOfWeek.MONDAY;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductGroupTest extends ProductTest {

	@Test
	public void given_empty_entries_when_create_product_group_then_should_throw_exception() {
		ArrayList<ProductGroupEntry> emptyEntries = new ArrayList<>();
		assertThatThrownBy(() -> {
			Customer customer = CustomerMother.getCanRocaCustomer();
			new ProductGroup(
					customer,
					new MultiLangString("Menú del Día"),
					CategoryMother.getPacksCategory(customer),
					ServiceBuilderLocation.KITCHEN,
					MoneyAmount.ofDouble(12),
					0.1,
					1,
					emptyEntries
			);
		}).isInstanceOf(IllegalArgumentException.class);

	}

	@Test
	public void given_product_group_days_of_week_null_when_set_days_of_week_then_should_set_all_as_default() {
		final ProductGroup productGroup = ProductMother.getMenuDiarioProduct();

		productGroup.setDaysOfWeek(null);

		assertThat(productGroup.getDaysOfWeek()).isEqualTo(EnumSet.allOf(DayOfWeek.class));
	}

	@Test
	public void given_product_group_start_time_null_when_set_start_time_then_should_set_min_as_default() {
		final ProductGroup productGroup = ProductMother.getMenuDiarioProduct();

		productGroup.setStartTime(null);

		assertThat(productGroup.getStartTime()).isEqualTo(LocalTime.MIN);
	}

	@Test
	public void given_product_group_end_time_null_when_set_end_time_then_should_set_max_as_default() {
		final ProductGroup productGroup = ProductMother.getMenuDiarioProduct();

		productGroup.setEndTime(null);

		assertThat(productGroup.getEndTime()).isEqualTo(LocalTime.MAX);
	}

	@Test
	public void given_product_group_with_item_when_get_product_by_id_then_should_return_correct_product_and_service_item() {
		ProductItem cervezaProduct = ProductMother.getCervezaProduct();
		final ProductGroup productGroup = new ProductGroupBuilder()
				.setEntries(asList(
						new ProductGroupEntry(ServiceTime.SOONER, singleton(ProductMother.getPatatasBravasProduct())),
						new ProductGroupEntry(SECOND_COURSE, singleton(cervezaProduct))
				))
				.build();

		Pair<ProductItem, ServiceTime> pair = productGroup.getProductItem(CERVEZA_ID);

		assertThat(pair.getFirst()).isEqualTo(cervezaProduct);
		assertThat(pair.getSecond()).isEqualTo(SECOND_COURSE);
	}

	@Test
	public void given_product_group_with_defaults_when_get_time_range_available_then_should_return_true() {
		final ProductGroup productGroup = new ProductGroupBuilder()
				.build();

		final boolean timeRangeAvailable = productGroup.isTimeRangeAvailable(Instant.now());

		assertThat(timeRangeAvailable).isTrue();
	}

	@Test
	public void given_product_group_with_fixed_date_inside_range_when_get_time_range_available_then_should_return_true() {
		ZoneId zone = ZoneId.of("Europe/Madrid");
		final ProductGroup productGroup = new ProductGroupBuilder()
				.setCustomer(
						new CustomerBuilder().setTimeZone(zone).build()
				).setStartTime(LocalTime.of(12, 0, 0))
				.setEndTime(LocalTime.of(12, 5, 5))
				.setDaysOfWeek(singleton(MONDAY))
				.build();

		final Instant instant = LocalDateTime.of(2020, 10, 5, 12, 1, 0, 0) //local time in Europe/Madrid is 12:01:00
				.atZone(zone) // we set the zone, so this will change to 10 or 11 depending of the Offset and DST
				.toInstant(); // we get the instant, which is UTC only
		final boolean timeRangeAvailable = productGroup.isTimeRangeAvailable(instant);

		assertThat(timeRangeAvailable).isTrue();
	}

	@Test
	public void given_product_group_with_fixed_date_outside_range_day_of_week_when_get_time_range_available_then_should_return_false() {
		final ProductGroup productGroup = new ProductGroupBuilder()
				.setStartTime(LocalTime.of(12, 0, 0))
				.setEndTime(LocalTime.of(12, 5, 5))
				.setDaysOfWeek(singleton(MONDAY))
				.build();

		final Instant date = LocalDateTime.of(2020, 6, 16, 12, 2, 34, 0)
				.atZone(productGroup.getCustomer().getTimeZone())
				.toInstant();
		final boolean timeRangeAvailable = productGroup.isTimeRangeAvailable(date);

		assertThat(timeRangeAvailable).isFalse();
	}


	@Test
	public void given_product_group_with_fixed_date_outside_range_day_of_week_because_of_timezone_offset_when_get_time_range_available_then_should_return_false() {
		ZoneId zone = ZoneId.of("Europe/Madrid");
		final ProductGroup productGroup = new ProductGroupBuilder()
				.setCustomer(
						new CustomerBuilder().setTimeZone(zone).build()
				).setStartTime(LocalTime.of(12, 0, 0))
				.setEndTime(LocalTime.of(12, 5, 5))
				.setDaysOfWeek(singleton(MONDAY))
				.build();

		final Instant date = LocalDateTime.of(2020, 6, 15, 23, 50, 0, 0)
				.atOffset(ZoneOffset.UTC) // at Europe/Madrid, the dayOfMonth is 16 (TUESDAY)
				.toInstant();
		final boolean timeRangeAvailable = productGroup.isTimeRangeAvailable(date);

		assertThat(timeRangeAvailable).isFalse();
	}


	@Test
	public void given_product_group_with_fixed_date_outside_range_time_lower_bound_when_get_time_range_available_then_should_return_false() {
		final ProductGroup productGroup = new ProductGroupBuilder()
				.setStartTime(LocalTime.of(12, 0, 0))
				.setEndTime(LocalTime.of(12, 5, 5))
				.setDaysOfWeek(singleton(MONDAY))
				.build();

		final Instant date = LocalDateTime.of(2020, 6, 15, 11, 59, 59, 59)
				.atZone(productGroup.getCustomer().getTimeZone())
				.toInstant();
		final boolean timeRangeAvailable = productGroup.isTimeRangeAvailable(date);

		assertThat(timeRangeAvailable).isFalse();
	}

	@Test
	public void given_product_group_with_fixed_date_outside_range_time_upper_bound_when_get_time_range_available_then_should_return_false() {
		final ProductGroup productGroup = new ProductGroupBuilder()
				.setStartTime(LocalTime.of(12, 0, 0))
				.setEndTime(LocalTime.of(12, 5, 5))
				.setDaysOfWeek(singleton(MONDAY))
				.build();

		final Instant date = LocalDateTime.of(2020, 6, 15, 12, 5, 6, 0)
				.atZone(productGroup.getCustomer().getTimeZone())
				.toInstant();
		final boolean timeRangeAvailable = productGroup.isTimeRangeAvailable(date);

		assertThat(timeRangeAvailable).isFalse();
	}

	@Test
	public void given_product_group_with_fixed_date_inside_range_time_equals_upper_bound_when_get_time_range_available_then_should_return_true() {
		final ProductGroup productGroup = new ProductGroupBuilder()
				.setStartTime(LocalTime.of(12, 0, 0))
				.setEndTime(LocalTime.of(12, 5, 5))
				.setDaysOfWeek(singleton(MONDAY))
				.build();

		final Instant date = LocalDateTime.of(2020, 6, 15, 12, 5, 5, 0)
				.atZone(productGroup.getCustomer().getTimeZone())
				.toInstant();
		final boolean timeRangeAvailable = productGroup.isTimeRangeAvailable(date);

		assertThat(timeRangeAvailable).isTrue();
	}

	@Override
	protected Product getProductOfPriceAndTax(MoneyAmount price, double tax) {
		return new ProductGroupBuilder()
				.setPrice(price)
				.setTax(tax)
				.build();
	}

	@Override
	protected Product getProductOfOrder(int order) {
		return new ProductGroupBuilder()
				.setOrder(order)
				.build();
	}
}