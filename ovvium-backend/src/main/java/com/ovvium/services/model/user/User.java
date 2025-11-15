package com.ovvium.services.model.user;

import com.ovvium.services.app.constant.Roles;
import com.ovvium.services.model.exception.OvviumDomainException;
import com.ovvium.services.model.product.Picture;
import com.ovvium.services.model.user.converter.AllergenSetConverter;
import com.ovvium.services.model.user.converter.FoodPreferenceSetConverter;
import com.ovvium.services.security.exception.AccountError;
import com.ovvium.services.security.exception.InvalidTokenException;
import com.ovvium.services.util.jpa.converter.StringSetConverter;
import com.ovvium.services.util.ovvium.domain.entity.BaseEntity;
import com.ovvium.services.util.ovvium.string.OvviumStringUtils;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.*;

import static com.ovvium.services.app.constant.Roles.CUSTOMERS_ADMIN;
import static com.ovvium.services.app.constant.Roles.USERS;
import static com.ovvium.services.model.exception.ErrorCode.PASSWORD_TOO_SHORT;
import static com.ovvium.services.security.exception.AccountError.ACTIVATION_CODE_INVALID;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotBlank;
import static com.ovvium.services.util.ovvium.base.Preconditions.checkNotEmpty;
import static com.ovvium.services.util.ovvium.string.OvviumStringUtils.EMAIL_REMOVED_DOMAIN;
import static com.ovvium.services.util.util.basic.Utils.set;
import static java.lang.String.format;
import static javax.persistence.CascadeType.ALL;
import static lombok.AccessLevel.PROTECTED;
import static org.springframework.util.DigestUtils.md5DigestAsHex;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Accessors(chain = true)
public class User extends BaseEntity implements Comparable<User> {

	public static final int MINIMUM_PASSWORD = 8;

	private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Email
	@NotNull
	private String email;

	@NotNull
	private String name;

	private String password;

	@Convert(converter = StringSetConverter.class)
	private Set<String> roles = new HashSet<>();

	@Embedded
	@AttributeOverrides({ //
			@AttributeOverride(name = "id", column = @Column(name = "facebook_profile_id")), //
			@AttributeOverride(name = "email", column = @Column(name = "facebook_email")), //
			@AttributeOverride(name = "fullName", column = @Column(name = "facebook_profile_name")), //
	})
	private SocialProfile facebookProfile;

	@Embedded
	@AttributeOverrides({ //
			@AttributeOverride(name = "id", column = @Column(name = "google_profile_id")), //
			@AttributeOverride(name = "email", column = @Column(name = "google_profile_email")), //
			@AttributeOverride(name = "fullName", column = @Column(name = "google_profile_name")), //
	})
	private SocialProfile googleProfile;

	@Embedded
	@AttributeOverrides({ //
		@AttributeOverride(name = "id", column = @Column(name = "apple_profile_id")),
		@AttributeOverride(name = "email", column = @Column(name = "apple_profile_email")),
		@AttributeOverride(name = "fullName", column = @Column(name = "apple_profile_name")),
	})
	private SocialProfile appleProfile;

	private boolean enabled;

	@Setter
	@ManyToOne
	private Picture picture;

	@Setter
	@Convert(converter = AllergenSetConverter.class)
	private Set<Allergen> allergens = EnumSet.noneOf(Allergen.class);

	@Setter
	@Convert(converter = FoodPreferenceSetConverter.class)
	private Set<FoodPreference> foodPreferences = EnumSet.noneOf(FoodPreference.class);

	@Setter
	@JoinColumn(name = "user_id")
	@OneToMany(cascade = ALL, orphanRemoval = true)
	private Set<UserPciDetails> pciDetails = new LinkedHashSet<>();

	private User(String name, String email, Set<String> roles) {
		setName(name);
		// TODO Check if valid with regex
		this.email = checkNotBlank(email, "Email cannot be blank");
		setRoles(roles);
	}

	public User setName(String name) {
		this.name = checkNotBlank(name, "Name cannot be blank").trim().replaceAll("\\s+", " ");
		return this;
	}

	/**
	 * FIXME This method and getSurnames should use a DB field each.
	 * For now and because we would need APP changes, we will do it like this
	 */
	public String getFirstName(){
		return StringUtils.substringBefore(this.name, " ");
	}

	public String getSurnames(){
		return StringUtils.substringAfter(this.name, " ");
	}

	public Optional<Picture> getPicture() {
		return Optional.ofNullable(picture);
	}

	public void setRoles(Set<String> roles) {
		this.roles = checkNotEmpty(roles, "User roles cannot be empty");
	}

	public Set<String> getRoles() {
		return Set.copyOf(roles);
	}

	public UserPciDetails addUserPciDetail(String providerUserId, String providerReferenceToken) {
		val userPciDetails = new UserPciDetails(providerUserId, providerReferenceToken);
		this.pciDetails.add(userPciDetails);
		return userPciDetails;
	}

	public UserPciDetails removeUserPciDetail(UUID pciDetailsId) {
		val userPciDetail = getSinglePciDetails(pciDetailsId);
		this.pciDetails.remove(userPciDetail);
		return userPciDetail;
	}

	public UserPciDetails getSinglePciDetails(UUID pciDetailsId) {
		return pciDetails.stream().filter(pciDetails -> pciDetails.getId().equals(pciDetailsId)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException(format("PciDetails with id %s not found for User %s", pciDetailsId, getId())));
	}

	@SneakyThrows
	public void checkPassword(String password) {
		checkNotBlank(password, "Password can't be blank");
		if (this.password == null) {
			// this is when a user is registered using Social, where no password exists
			throw new IllegalStateException("Missing password.");
		}
		if (!passwordEncoder.matches(password, this.password)) {
			throw new BadCredentialsException(AccountError.INVALID_PASSWORD.name());
		}
	}

	public void verify(String activationCode, String secret) {
		checkNotBlank(activationCode, "Activation code is required");
		checkNotBlank(secret, "Secret is required");
		if (!generateVerificationHash(this.getId(), this.email, secret).equals(activationCode)) {
			throw new InvalidTokenException(ACTIVATION_CODE_INVALID.name());
		}
		this.enabled = true;
	}

	public boolean isCustomerAdmin() {
		return roles.stream().anyMatch(CUSTOMERS_ADMIN::equalsIgnoreCase);
	}

	public User promoteToAdminUser(){
		this.roles.add(CUSTOMERS_ADMIN);
		return this;
	}

	/**
	 * Generates and sets a new password when a user has forgotten its password.
	 */
	public String generatePassword() {
		val password = OvviumStringUtils.randomPassword();
		this.setPassword(password);
		return password;
	}

	public User changePassword(String oldPassword, String newPassword) {
		checkPassword(oldPassword);
		setPassword(newPassword);
		return this;
	}

	public static User adminCustomerUser(String name, String email, String password) {
		return new User(name, email, set(USERS, CUSTOMERS_ADMIN)).setPassword(password);
	}

	public static User adminUser(String name, String email, String password) {
		return new User(name, email, set(USERS, Roles.OVVIUM_ADMIN)).setPassword(password);
	}

	public static User basicUser(String name, String email, String password) {
		return new User(name, email, set(USERS)).setPassword(password);
	}

	public static User socialUser(String name, String email) {
		return new User(name, email, set(USERS));
	}

	public static String generateVerificationHash(UUID userId, String email, String secret) {
		byte[] seed = format("%s%s%s", email, userId, secret).getBytes();
		return md5DigestAsHex(seed);
	}

	// TODO Check if it is a secure password
	private User setPassword(String password) {
		checkNotBlank(password, "Password can't be blank");
		if (password.length() < MINIMUM_PASSWORD) {
			throw new OvviumDomainException(PASSWORD_TOO_SHORT);
		}
		this.password = passwordEncoder.encode(password);
		return this;
	}

	public User setSocialProfile(SocialProvider provider, String email, String id, String name) {
		SocialProfile profile = new SocialProfile(id, email, name);
		switch (provider) {
			case GOOGLE -> this.googleProfile = profile;
			case FACEBOOK -> this.facebookProfile = profile;
			case APPLE -> this.appleProfile = profile;
			default -> throw new IllegalArgumentException("Social Provider not implemented");
		}
		this.email = profile.getEmail();
		this.enabled = true;
		return this;
	}

	/**
	 * Make the user anonymous so we are GDPR compliant without hard-deleting users.
	 */
	public void anonymize() {
		this.name = "UNKNOWN";
		this.pciDetails.clear();
		this.appleProfile = null;
		this.facebookProfile = null;
		this.googleProfile = null;
		this.allergens = EnumSet.noneOf(Allergen.class);
		this.foodPreferences = EnumSet.noneOf(FoodPreference.class);
		this.enabled = false;
		this.email = OvviumStringUtils.createEmail(getId().toString(), EMAIL_REMOVED_DOMAIN);
		this.roles = Set.of(USERS);
		setPicture(null);
		generatePassword();
	}

	@Override
	public int compareTo(User other) {
		return name.compareTo(other.name);
	}
}
