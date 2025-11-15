package com.ovvium.services.model.customer;

import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.model.exception.ResourceNotFoundException;
import com.ovvium.services.model.product.Picture;
import com.ovvium.services.model.user.User;
import com.ovvium.services.util.jpa.converter.StringSetConverter;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.net.URI;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;

import static com.ovvium.services.model.exception.ErrorCode.CUSTOMER_EMPLOYEE_NOT_FOUND;
import static com.ovvium.services.util.ovvium.base.Preconditions.*;
import static java.lang.String.format;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Accessors(chain = true)
public class Customer extends BaseEntity {

	private static final Pattern PHONE_PATTERN = Pattern
			.compile("\\d{9,10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}");

	private static final ZoneId DEFAULT_TIME_ZONE = ZoneId.of("Europe/Madrid");

	private String name;

	@Column(length = 500)
	private String description;

	private String address;

	@Setter
	private String latitude;

	@Setter
	private String longitude;

	private ZoneId timeZone = DEFAULT_TIME_ZONE;

	@Setter
	@ManyToOne
	private Picture picture;

	private String cif;

	@Convert(converter = StringSetConverter.class)
	private Set<String> phones = new LinkedHashSet<>();

	@OneToMany(fetch = LAZY)
	@JoinColumn(name = "location_customer_id")
	@OrderBy("zone ASC, position ASC")
	private List<Location> locations = new ArrayList<>();

	@OneToMany(fetch = LAZY)
	@JoinColumn(name = "user_customer_id")
	private Set<User> adminUsers = new HashSet<>();

	@JoinColumn(name = "employee_customer_id")
	@OneToMany(fetch = LAZY, cascade = ALL, orphanRemoval = true)
	@OrderBy("name ASC")
	private Set<Employee> employees = new LinkedHashSet<>();

	@Setter
	private URI website;

	private String pciSplitUserId;

	@Basic(fetch = LAZY)
	private String commissionConfig;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "invoice_number_prefix"))
	private InvoiceNumberPrefix invoiceNumberPrefix;

	public Customer(User user,
					String name,
					String description,
					String cif,
					String address,
					Set<String> phones,
					String pciSplitUserId,
					CommissionConfig commissionConfig,
					InvoiceNumberPrefix invoiceNumberPrefix) {
		addAdminUser(user);
		setName(name);
		setDescription(description);
		setCif(cif);
		setAddress(address);
		checkNotEmpty(phones, "Phones can't be empty").forEach(this::addPhone);
		setPciSplitUserId(pciSplitUserId);
		setCommissionConfig(commissionConfig);
		setInvoiceNumberPrefix(invoiceNumberPrefix);
	}

	public Customer addPhone(String phone) {
		checkNotBlank(phone, "Phone cannot be blank");
		check(PHONE_PATTERN.matcher(phone).matches(), format("Phone %s is not a valid phone format.", phone));
		phones.add(phone);
		return this;
	}
	public Customer setName(String name) {
		this.name = checkNotBlank(name, "Name can´t be empty");
		return this;
	}

	public Customer setDescription(String description) {
		this.description = checkNotBlank(description, "Description can´t be empty");
		return this;
	}

	public Customer setAddress(String address) {
		this.address = checkNotBlank(address, "Address can´t be empty");
		return this;
	}

	public Customer setCif(String cif) {
		this.cif = checkNotBlank(cif, "CIF can´t be empty");
		return this;
	}

	public Customer setPciSplitUserId(String pciSplitUserId) {
		this.pciSplitUserId = checkNotBlank(pciSplitUserId, "Split id cannot be blank.");
		return this;
	}

	public Customer setCommissionConfig(CommissionConfig commissionConfig) {
		this.commissionConfig = checkNotNull(commissionConfig, "CommissionConfig can't be null").toJson();
		return this;
	}

	public void setTimeZone(ZoneId timeZone) {
		this.timeZone = timeZone == null ? DEFAULT_TIME_ZONE : timeZone;
	}

	public Customer setInvoiceNumberPrefix(InvoiceNumberPrefix invoiceNumberPrefix) {
		this.invoiceNumberPrefix = checkNotNull(invoiceNumberPrefix, "InvoiceNumberPrefix can't be null");
		return this;
	}

	public CommissionConfig getCommissionConfig() {
		return CommissionConfig.of(commissionConfig);
	}

	public Optional<Picture> getPicture() {
		return Optional.ofNullable(picture);
	}

	public Optional<URI> getWebsite() {
		return Optional.ofNullable(website);
	}

	public Optional<String> getLatitude() {
		return Optional.ofNullable(latitude);
	}

	public Optional<String> getLongitude() {
		return Optional.ofNullable(longitude);
	}

	/**
	 * Get locations from Customer By Id or throw an Exception if any ID is not found.
	 */
	public List<Location> getLocationsById(Set<UUID> locationIds) {
		if (!locationIds.isEmpty()) {
			Map<UUID, Location> locationsById = getLocations().stream()
					.collect(toMap(Location::getId, identity()));
			return locationIds
					.stream()
					.map(id -> {
						check(locationsById.containsKey(id), new ResourceNotFoundException("Location with id " + id + " is not from Customer " + this.getId()));
						return locationsById.get(id);
					})//
					.collect(toList());
		}
		return Collections.emptyList();
	}

	public Employee getEmployee(UUID employeeId) {
		return employees.stream()
				.filter(employee -> employee.getId().equals(employeeId))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(String.format("Employee with id %s is not on Customer %s", employeeId, getId())));
	}

	public Employee getEmployeeByCode(String code) {
		return employees.stream()
				.filter(employee -> employee.getAccessCode().equals(code))
				.findFirst()
				.orElseThrow(() -> new OvviumDomainException(CUSTOMER_EMPLOYEE_NOT_FOUND));
	}

	public boolean isAdminUser(UUID userId) {
		return getAdminUsers().stream().map(User::getId).anyMatch(userId::equals);
	}

	private void addAdminUser(User user) {
		User employee = checkNotNull(user, "Customer must have at least one employee");
		check(employee.isCustomerAdmin(), "User must have CUSTOMER_ADMIN role to create a new Customer");
		this.adminUsers.add(employee);
	}
}
