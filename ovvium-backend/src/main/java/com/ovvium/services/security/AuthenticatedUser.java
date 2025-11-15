package com.ovvium.services.security;

import com.ovvium.services.model.customer.Employee;
import com.ovvium.services.model.user.User;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Getter
public class AuthenticatedUser implements UserDetails {

	@Data
	public class EmployeeUser {
		private final UUID id;
		private final String name;
		private final Set<String> roles;
	}

	private final UUID id;
	private final String name;
	private final String email;
	private final Set<String> roles;
	private EmployeeUser employeeUser;

	public AuthenticatedUser(User user) {
		this.id = user.getId();
		this.name = user.getName();
		this.email = user.getEmail();
		this.roles = user.getRoles();
	}

	public AuthenticatedUser(UUID id, String name, String email, Set<String> roles, EmployeeUser employeeUser) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.roles = roles;
		this.employeeUser = employeeUser;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<String> roles = getRoles();
		return roles.stream().map(SimpleGrantedAuthority::new).collect(toList());
	}

	public Set<String> getRoles() {
		return getEmployeeUser()
					.map(EmployeeUser::getRoles)
					.orElse(this.roles);
	}

	@Override
	public String getPassword() {
		return "N/A";
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public AuthenticatedUser setEmployee(Employee employee) {
		this.employeeUser = new EmployeeUser(employee.getId(), employee.getName(), employee.getRoles());
		return this;
	}

	public Optional<EmployeeUser> getEmployeeUser() {
		return Optional.ofNullable(employeeUser);
	}
}
