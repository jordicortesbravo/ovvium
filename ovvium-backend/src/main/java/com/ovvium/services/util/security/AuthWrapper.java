package com.ovvium.services.util.security;

import lombok.Data;
import lombok.Getter;
import lombok.val;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
public class AuthWrapper {

    /**
     * Per què els d'Spring ho valen, no han fet una constant.
     *
     * @see AnonymousAuthenticationFilter
     */
    public static final String ANONYMOUS_USER_NAME = "anonymousUser";

    @Getter
    private final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    @Getter
    private final Object principal = authentication == null ? null : authentication.getPrincipal();

    public Optional<String> getName() {
        return Optional.ofNullable(principal instanceof UserDetails ? ((UserDetails) principal).getUsername() //
                : principal instanceof AnonymousAuthenticationToken ? ((AnonymousAuthenticationToken) principal).getName() //
                : principal instanceof String ? (String) principal //
                : null);
    }

    public <T extends UserDetails> Optional<T> getPrincipal(Class<T> clazz) {
        if (isAnonymous()) {
            return Optional.empty();
        }
        return Optional.ofNullable(principal != null && principal.getClass().isAssignableFrom(clazz) ? ((T) principal) : null);
    }

    public boolean isAuthenticated() {
        return authentication != null;
    }

    public boolean isAnonymous() {
        return getName().filter(ANONYMOUS_USER_NAME::equals).isPresent();
    }

    public List<GrantedAuthority> getAuthorities() {
        if (isAuthenticated()) {
            return new ArrayList<>(authentication.getAuthorities());
        }
        return Collections.emptyList();
    }

    public List<String> getRoles() {
        val list = new ArrayList<String>();
        for (val a : getAuthorities()) {
            list.add(a.getAuthority());
        }
        return list;
    }

    public boolean hasRole(String role) {
        return getRoles().contains(role);
    }

    public boolean hasAnyRole(String... roles) {
        val userRoles = getRoles();
        for (val role : roles) {
            if (userRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAllRoles(String... roles) {
        val userRoles = getRoles();
        for (val role : roles) {
            if (!userRoles.contains(role)) {
                return false;
            }
        }
        return true;
    }

    public boolean isSudoer() {
        for (val authority : getAuthorities()) {
            if (authority instanceof SwitchUserGrantedAuthority) {
                return true;
            }
        }
        return false;
    }
}
