package com.ovvium.services.service.populator;

import com.google.common.collect.Sets;
import com.ovvium.services.model.bill.Bill;
import com.ovvium.services.model.bill.ServiceBuilderLocation;
import com.ovvium.services.model.bill.ServiceTime;
import com.ovvium.services.model.common.MoneyAmount;
import com.ovvium.services.model.common.MultiLangString;
import com.ovvium.services.model.customer.*;
import com.ovvium.services.model.product.*;
import com.ovvium.services.model.user.Allergen;
import com.ovvium.services.model.user.FoodPreference;
import com.ovvium.services.model.user.User;
import com.ovvium.services.model.user.UserPciDetails;
import com.ovvium.services.repository.EmployeeRepository;
import com.ovvium.services.repository.LocationRepository;
import com.ovvium.services.repository.ZoneRepository;
import com.ovvium.services.service.*;
import com.ovvium.services.transfer.command.category.CreateCategoryCommand;
import com.ovvium.services.transfer.command.product.CreateProductGroupCommand;
import com.ovvium.services.util.util.basic.Utils;
import com.ovvium.services.util.util.reflection.ReflectionUtils;
import com.ovvium.services.util.util.xson.Xson;
import com.ovvium.services.web.controller.bff.v1.transfer.request.invoice.CreateInvoiceDateRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.order.CreateOrderRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.product.CreatePictureRequest;
import com.ovvium.services.web.controller.bff.v1.transfer.request.rating.CreateRatingRequest;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Enumerated;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ovvium.services.model.common.LocaleConstants.SPANISH;
import static com.ovvium.services.model.user.Allergen.*;
import static com.ovvium.services.repository.client.media.FileSystemMediaStore.LOCAL_PATH;
import static com.ovvium.services.service.populator.PopulatorConstants.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static java.util.Locale.UK;
import static javax.persistence.EnumType.STRING;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopulatorServiceImpl implements PopulatorService {

	@Data
	public static class ProductDTO {

		private String nameES;

		private String descriptionES;

		private String nameEN;

		private String descriptionEN;

		@Enumerated(STRING)
		private ServiceBuilderLocation serviceBuilderLocation;

		private String category;

		private ProductType productType;

		private String imageUri;

		private List<String> tags = new ArrayList<>();

		private List<String> allergens = new ArrayList<>();

		private Double price;

		private List<ProductOptionGroupDTO> optionGroups = new ArrayList<>();

		public Optional<List<String>> getAllergens() {
			return Optional.ofNullable(allergens);
		}

		public Optional<String> getImageUri() {
			return Optional.ofNullable(imageUri);
		}

		public Optional<String> getDescriptionES() {
			return Optional.ofNullable(descriptionES);
		}

		public Optional<String> getDescriptionEN() {
			return Optional.ofNullable(descriptionEN);
		}
	}

	@Data
	private static class ProductOptionGroupDTO {
		private MultiLangStringDTO title;
		private ProductOptionGroup.ProductOptionType type;
		private List<ProductOptionDTO> options = new ArrayList<>();
		private boolean required;
	}

	@Data
	private static class MultiLangStringDTO {
		private String defaultValue;
		private Map<String, String> translations = new HashMap<>();
	}

	@Data
	private static class ProductOptionDTO {
		private MultiLangStringDTO title;
		private MoneyAmount basePrice;
		private Double tax;
	}

	private final ProductService productService;
	private final RatingService ratingService;
	private final AccountService accountService;
	private final CustomerService customerService;
	private final InvoiceDateService invoiceDateService;
	private final PictureService pictureService;
	private final BillService billService;
	private final LocationRepository locationRepository;
	private final ZoneRepository zoneRepository;
	private final ResourceLoader resourceLoader;
	private final LockService lockService;
	private final EmployeeRepository employeeRepository;

	private final List<Customer> customers = new ArrayList<>();
	private final Map<String, User> users = new HashMap<>();
	private final Map<UUID, Map<String, Category>> categoriesByCustomer = new HashMap<>();
	private final Map<UUID, List<Product>> productsByCustomer = new HashMap<>();

	private int serialNumberCounter = 1_000_000_000;

	@Override
	public boolean isPopulated() {
		try {
			val user = accountService.getUser(USER_1_FRODO_UUID);
			return user != null;
		} catch (Exception e) {
			return false;
		}
	}

	@Async
	@Override
	@SneakyThrows
	@Transactional
	public void populate() {
		if (!lockService.tryLock("populate")) {
			log.info("Other instance is already populating... Skipping.");
			return;
		}
		try {
			val tempFolder = new File(LOCAL_PATH);
			if (tempFolder.exists()) {
				FileUtils.deleteDirectory(tempFolder);
			}
			log.info("Populating...");
			populateUsers();
			log.debug("Users Ok...");
			populateCustomers();
			log.debug("Customers Ok...");
			populateProductsAndCategories();
			populateProductGroups();
			log.debug("Products Ok...");
			populateInvoiceDate();
			//populateBills();
			log.debug("Bills Ok...");
			populateRatings();
			log.debug("Ratings Ok...");
		} finally {
			lockService.unlock("populate");
			log.info("Populated.");
		}
	}

	@Override
	public void onPopulated() {

	}

	private void populateUsers() {
		Stream.of(
				createTestUser("Frodo Baggins", "fbaggins.test@ovvium.com", USER_1_FRODO_UUID, USER_1_PCI_DETAILS_ID), // dev
				createTestUser("Sam Gamgee", "sgamgee.test@ovvium.com", USER_2_SAM_UUID, USER_2_PCI_DETAILS_ID), // dev
				createTestUser("Walter White", "wwhite.test@ovvium.com", USER_3_WALTER_UUID, USER_3_PCI_DETAILS_ID), // apps
				createTestUser("Jesse Pinkman", "jpinkman.test@ovvium.com", USER_4_JESSE_UUID, USER_4_PCI_DETAILS_ID), // apps
				createTestUser("Juan Rodríguez Guzmán", "jrodriguez.test@ovvium.com", USER_5_JUAN_UUID, USER_5_PCI_DETAILS_ID), // demos
				createTestUser("Rosa Martínez Peralta", "rmartinez.test@ovvium.com", USER_6_ROSA_UUID, USER_6_ROSA_UUID), // demos
				createCustomerUser("Gandalf The White", "gwhite.test@ovvium.com", "gandalf123", CUSTOMER_USER_1_GANDALF_UUID, CUSTOMER_USER_1_PCI_DETAILS_ID), // devs
				createCustomerUser("Ignasi Brun", "ibrun.test@ovvium.com", "ibrun1234", CUSTOMER_USER_2_IBRUN_UUID, CUSTOMER_USER_2_PCI_DETAILS_ID), // demos and sales
				createCustomerUser("Gustavo Fring", "gfring@ovvium.com", "gustavo123", CUSTOMER_USER_3_GUSTAVO_UUID, CUSTOMER_USER_3_PCI_DETAILS_ID), // app reviewers
				createOvviumAdminUser("Jordi Cortés", "jcortes@ovvium.com", "jcortes123", CUSTOMER_USER_4_JORDI_UUID, CUSTOMER_USER_4_PCI_DETAILS_ID) //admin
		).forEach(user -> {
					ReflectionUtils.set(user, "enabled", true);
					users.put(user.getName(), accountService.save(user));
				}
		);
	}

	private User createTestUser(String name, String email, UUID userId, UUID pciDetailsId) {
		User appUserTest = User.basicUser(name, email, "OvvTest123")
				.setFoodPreferences(Set.of(FoodPreference.values()[randomInt(0, FoodPreference.values().length - 1)]))
				.setAllergens(EnumSet.of(EGGS, GLUTEN));
		ReflectionUtils.set(appUserTest, "id", userId);
		// Add card details
		UserPciDetails userPciDetails = appUserTest.addUserPciDetail(USER_PCI_USER_ID, USER_PCI_USER_TOKEN);
		ReflectionUtils.set(userPciDetails, "id", pciDetailsId);
		appUserTest.setPicture(createPictureFromClasspath(String.format("avatar%d.jpg", randomInt(1, 8))));
		return appUserTest;
	}

	private User createCustomerUser(String name, String email, String pass, UUID userId, UUID pciDetailsId) {
		User customerUser = User.adminCustomerUser(name, email, pass)
				.setFoodPreferences(Set.of(FoodPreference.values()[randomInt(0, FoodPreference.values().length - 1)]))
				.setAllergens(EnumSet.of(EGGS, FISH));
		ReflectionUtils.set(customerUser, "id", userId);
		// Add card details
		UserPciDetails userPciDetails = customerUser.addUserPciDetail(USER_PCI_USER_ID, USER_PCI_USER_TOKEN);
		ReflectionUtils.set(userPciDetails, "id", pciDetailsId);
		customerUser.setPicture(createPictureFromClasspath(String.format("avatar%d.jpg", randomInt(1, 8))));
		return customerUser;
	}

	private User createOvviumAdminUser(String name, String email, String pass, UUID userId, UUID pciDetailsId) {
		User customerUser = User.adminUser(name, email, pass)
				.setFoodPreferences(Set.of(FoodPreference.values()[randomInt(0, FoodPreference.values().length - 1)]))
				.setAllergens(EnumSet.of(EGGS, FISH));
		ReflectionUtils.set(customerUser, "id", userId);
		// Add card details
		UserPciDetails userPciDetails = customerUser.addUserPciDetail(USER_PCI_USER_ID, USER_PCI_USER_TOKEN);
		ReflectionUtils.set(userPciDetails, "id", pciDetailsId);
		customerUser.setPicture(createPictureFromClasspath(String.format("avatar%d.jpg", randomInt(1, 8))));
		return customerUser;
	}

	@SneakyThrows
	private void populateCustomers() {
		val dragonVerde = new Customer(users.get("Gandalf The White"), "El Dragón Verde",
				" A ella concurrían hobbits no solo de Delagua sino también de Hobbiton, era un gran lugar de reunión para discutir" +
						" y hablar sobre rumores y cosas por el estilo como cualquier otra posada. ",
				"A61652021", "Delagua", Sets.newHashSet("9321222200"),
				CUSTOMER_SPLIT_ID,
				CommissionConfig.cardCategory(0.5, 0.005, 0.09),
				new InvoiceNumberPrefix("EDV"))
				.setLatitude("42.3973618")
				.setLongitude("3.1420913");
		ReflectionUtils.set(dragonVerde, "id", CUSTOMER_1_UUID);
		addEmployees(dragonVerde);
		addPicture(dragonVerde, URI.create("https://www.viajablog.com/wp-content/uploads/2012/12/Hobbiton.jpg"));

		val arsenal = new Customer(users.get("Ignasi Brun"), "Arsenal Demo",
				"Arsenal Masculino dispone de un amplio y luminoso restaurante con comedores privados para comidas de empresa y celebraciones," +
						" con una extensa carta, menú bajo en calorías y un cuidado buffet libre. ",
				"B61652021", "Carrer de Pomaret, 49, 08017 Barcelona, España", Sets.newHashSet("932128400"),
				CUSTOMER_SPLIT_ID,
				CommissionConfig.cardCategory(0.5, 0.005, 0.09),
				new InvoiceNumberPrefix("ARS"))
				.setLatitude("41.3973618")
				.setLongitude("2.1420913");
		ReflectionUtils.set(arsenal, "id", CUSTOMER_2_UUID);
		addEmployees(arsenal);
		addPicture(arsenal, URI.create("https://www.cmdsport.com/app/uploads/2017/11/arsenal-masculino.jpg"));


		val pollosHermanos = new Customer(users.get("Gustavo Fring"), "Los Pollos Hermanos",
				"The finest ingredients are brought together with love and care, then slow cooked to perfection. Yes, the old ways are still best at Los Pollos Hermanos." +
						" But don't take my word for it. One taste, and you'll know.",
				"D61652021", "Albuquerque", Sets.newHashSet("9888888888"),
				CUSTOMER_SPLIT_ID,
				CommissionConfig.cardCategory(0.5, 0.005, 0.09),
				new InvoiceNumberPrefix("LPH"))
				.setLatitude("35.0143697")
				.setLongitude("-106.6857943");
		ReflectionUtils.set(pollosHermanos, "id", CUSTOMER_3_UUID);
		addPicture(pollosHermanos, URI.create("https://laughingsquid.com/wp-content/uploads/2015/05/LosPollosHermanos1.jpg"));

		asList(dragonVerde, arsenal, pollosHermanos)
				.forEach(it -> customers.add(customerService.save(it)));

		populateZonesAndLocations(dragonVerde, CUSTOMER_1_LOCATION_1_UUID, CUSTOMER_1_LOCATION_1_TAG_ID);
		populateZonesAndLocations(arsenal, CUSTOMER_2_LOCATION_1_UUID, CUSTOMER_2_LOCATION_1_TAG_ID);
		populateZonesAndLocations(pollosHermanos, CUSTOMER_3_LOCATION_1_UUID, CUSTOMER_3_LOCATION_1_TAG_ID);
	}


	private void addPicture(Customer customer, URI uri) {
		try {
			val pictureRequest = new CreatePictureRequest(
					IOUtils.toByteArray(uri),
					"product.jpg"
			);
			Picture picture = pictureService.createPicture(pictureRequest);
			customer.setPicture(picture);
		} catch (Exception e) {
			// nothing
		}
	}

	private void addEmployees(Customer customer) {
		val goiko = new Employee(customer, "Pedro", "0000");
		val aroa = new Employee(customer, "Aroa", "1111");
		employeeRepository.save(goiko);
		employeeRepository.save(aroa);
	}

	private void populateZonesAndLocations(Customer customer, UUID locationId, TagId tagId) {
		String name = "Salón principal";
		List<Zone> zones = asList(new Zone(customer, name), new Zone(customer, "Terraza"));
		val locations = new ArrayList<Location>();
		for (Zone zone : zones) {
			zoneRepository.save(zone);
			for (int i = 1; i <= randomInt(10, 20); i++) {
				Location location = new Location(
						customer,
						zone,
						TagId.randomTagId(),
						new SerialNumber(StringUtils.leftPad(String.valueOf(serialNumberCounter++), 4, "0")),
						i);
				if(zone.getName().equals("Terraza")) {
					location.setAdvancePayment(true);
				}
				locations.add(locationRepository.save(location));
			}
		}
		val location = Utils.first(locations);
		ReflectionUtils.set(location, "id", locationId);
		ReflectionUtils.set(location, "tagId", tagId);
	}

	@SneakyThrows
	private void populateProductsAndCategories() {
		try (val is = resourceLoader.getResource("classpath:products.json").getInputStream()) {
			String json = IOUtils.toString(is, StandardCharsets.UTF_8);
			val xson = Xson.create(json);
			val productDTOs = xson.asList(ProductDTO.class);
			customers.forEach(customer -> {
				populateCategories(customer);
				log.debug("Creating products for " + customer.getName());
				val categoryMap = categoriesByCustomer.get(customer.getId());
				val orderByCategory = categoryMap.entrySet().stream()
						.collect(Collectors.toMap(Map.Entry::getKey, v -> 0));

				val products = productsByCustomer.getOrDefault(customer.getId(), new ArrayList<>());
				productsByCustomer.putIfAbsent(customer.getId(), products);
				productDTOs.forEach(productDTO -> {
					String categoryName = productDTO.getCategory();
					Integer currentOrder = orderByCategory.get(categoryName);
					Product savedProduct = createProduct(customer, productDTO, currentOrder, categoryName);
					orderByCategory.put(categoryName, currentOrder + 1);
					products.add(savedProduct);
				});
			});
			} catch (IOException exc) {
				throw new RuntimeException(exc);
			}
	}

	private void populateRatings() {

		val dummyComments = asList("Increíble!",
				"Me ha encantado, lo recomiendo.",
				"Relación calidad precio insuperable, volveré a pedirlo.",
				"¡Repetiré seguro! Recomendado.",
				"Sabor inigualable, sin duda.",
				"Lo he descubierto gracias a la app, está genial.",
				"Riquísimo.",
				"Textura inigualable",
				"Lo he pedido al verlo en la app, ¡y no me arrepiento!"
		);

		for (Customer customer : customers) {
			val products = productsByCustomer.get(customer.getId());
			for (User user : users.values()) {
				products.stream()
						.filter(p -> randomInt(0, 1) % 2 == 0)
						.forEach(product -> ratingService.create(new CreateRatingRequest()//
								.setProductId(product.getId())//
								.setUserId(user.getId())//
								.setRating(randomInt(3, 5)) //
								.setComment(dummyComments.get(randomInt(0, dummyComments.size() - 1))) //
						));
			}
		}
	}

	private void populateInvoiceDate() {
		customers.forEach(customer -> invoiceDateService.createInvoiceDate(new CreateInvoiceDateRequest()
				.setCustomerId(customer.getId())
				.setDate(LocalDate.now())));
	}

	private void populateBills() {
		val customer = Utils.first(customers);
		val invoiceDate = invoiceDateService.getCurrentInvoiceDate(customer);
		val location = Utils.first(customer.getLocations());
		val user = Utils.first(users.values());
		val bill = new Bill(invoiceDate, user, singletonList(location));
		ReflectionUtils.set(bill, "id", BILL_UUID); // for development purposes
		billService.save(bill);
		val products = productsByCustomer.get(customer.getId());
		billService.addOrder(bill.getId(), //
				new CreateOrderRequest() //
						.setUserId(user.getId()) //
						.setProductId(Utils.first(products).getId()) //
						.setNotes("Bien hecho"));
	}

	private void populateCategories(Customer customer) {
		CATEGORIES.forEach((es, gb) -> {
			val category = productService.createCategory(new CreateCategoryCommand(
					customer,
					MultiLangString.ofDefaultAndTranslations(es, Map.of(SPANISH.toLanguageTag(), es, UK.toLanguageTag(), gb))
			));
			val categoryMap = categoriesByCustomer.getOrDefault(customer.getId(), new HashMap<>());
			categoriesByCustomer.putIfAbsent(customer.getId(), categoryMap);
			categoryMap.put(es, category);
		});
	}

	private void populateProductGroups() {
		customers.forEach(customer -> {
			val categoryMap = categoriesByCustomer.get(customer.getId());
			val products = productsByCustomer.get(customer.getId());
			val entries = Stream.of(ServiceTime.values())
					.collect(Collectors.toMap(k -> k, v -> Sets.newConcurrentHashSet(asList(
							products.get(randomInt(0, products.size() - 1)).as(ProductItem.class),
							products.get(randomInt(0, products.size() - 1)).as(ProductItem.class)
					))));
			ProductGroup group = productService.createGroup(new CreateProductGroupCommand(
					customer,
					categoryMap.get("MENUS"),
					new MultiLangString("Menu del Dia"),
					new MultiLangString("Disfruta de nuestro Menu del Dia"),
					ServiceBuilderLocation.KITCHEN,
					MoneyAmount.ofDouble(10d),
					TAX,
					emptySet(),
					null,
					null,
					null,
					null,
					entries,
					null
			));
			group.publish();
			productService.save(group);
		});
	}

	private Product createProduct(Customer customer, ProductDTO productDTO, int currentOrder, String categoryName) {
		val categoryMap = categoriesByCustomer.get(customer.getId());
		val category = categoryMap.getOrDefault(categoryName.toUpperCase(), Utils.first(categoryMap.values()));
		val basePrice = productDTO.getPrice() / (TAX + 1);
		val product = new ProductItem(
				customer,
				MultiLangString.ofDefaultAndTranslations(productDTO.getNameES(), Map.of(SPANISH.toLanguageTag(), productDTO.getNameES(), UK.toLanguageTag(), productDTO.getNameEN())),
				category,
				productDTO.getProductType(),
				productDTO.getServiceBuilderLocation(),
				MoneyAmount.ofDouble(basePrice),
				TAX,
				currentOrder);
		productDTO.getDescriptionES()
				.map(desc -> {
					val map = new HashMap<String, String>();
					map.put(SPANISH.toLanguageTag(), desc);
					productDTO.getDescriptionEN().ifPresent(it -> map.put(UK.toLanguageTag(), it));
					return MultiLangString.ofDefaultAndTranslations(desc, map);
				})
				.ifPresent(product::setDescription);
		product.publish();
		log.info(String.format("Creating product '%s' for '%s'", product.getName().getDefaultValue(), customer.getName()));
		productDTO.getAllergens().ifPresent(allergens -> allergens.stream()
				.map(Allergen::valueOf)
				.forEach(it -> product.getAllergens().add(it)));
		val savedProduct = productService.save(product);
		productDTO.getImageUri().ifPresent((uri) -> {
			try {
				addPictureToProduct(productDTO.nameES, savedProduct, IOUtils.toByteArray(URI.create(uri)));
			} catch (Exception e) {
				log.warn("Cannot add image to product " + productDTO.getNameES());
			}
		});
		product.setOptions(productDTO.getOptionGroups().stream()
				.map(this::createProductOptionGroup)
				.collect(Collectors.toList()));
		if (savedProduct.getCoverPicture().isEmpty()) {
			log.warn("No image defined, adding default image for " + productDTO.getNameES());
			product.setCoverPicture(createPictureFromClasspath("chicken.jpg"));
		}
		return savedProduct;
	}

	private ProductOptionGroup createProductOptionGroup(ProductOptionGroupDTO group) {
		return new ProductOptionGroup(createMultiLangString(group.title), group.type, group.options.stream()
				.map(this::createProductOption)
				.collect(Collectors.toList()), group.required);
	}

	private MultiLangString createMultiLangString(MultiLangStringDTO multiLangString) {
		return MultiLangString.ofDefaultAndTranslations(multiLangString.getDefaultValue(), multiLangString.translations);
	}

	private ProductOption createProductOption(ProductOptionDTO option) {
		return new ProductOption(createMultiLangString(option.title), option.basePrice, option.tax);
	}

	private int randomInt(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}

	private void addPictureToProduct(String name, Product product, byte[] data) {
		try {
			if (ArrayUtils.isEmpty(data)) {
				return;
			}
			val pictureRequest = new CreatePictureRequest(
					data,
					"product.jpg"
			);
			Picture picture = pictureService.createPicture(pictureRequest);
			product.setCoverPicture(picture);

		} catch (Exception e) {
			log.error(String.format("Error creating image for product '%s'", name));
		}
	}

	@SneakyThrows
	private Picture createPictureFromClasspath(String img) {
		@Cleanup val is = resourceLoader.getResource("classpath:mockup/" + img).getInputStream();
		return pictureService.createPicture(new CreatePictureRequest(IOUtils.toByteArray(is), img));
	}
}
