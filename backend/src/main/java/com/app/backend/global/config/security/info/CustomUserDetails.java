package com.app.backend.global.config.security.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.app.backend.domain.user.entity.Provider;
import com.app.backend.domain.user.entity.Role;
import com.app.backend.domain.user.entity.User;

public class CustomUserDetails implements UserDetails, OAuth2User {

	private final User user;
	private final Map<String, Object> attributes;

	public CustomUserDetails(User user) {
		this.user = user;
		this.attributes = Map.of();
	}

	public CustomUserDetails(User user, Map<String, Object> attributes) {
		this.user = user;
		this.attributes = attributes;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes != null ? attributes : Map.of();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<>();

		authorities.add(new SimpleGrantedAuthority(user != null ? user.getRole().name() : Role.ROLE_USER.name()));

		return authorities;
	}

	@Override
	public String getUsername() {
		return user != null ? user.getUsername() : "UNKNOWN";
	}

	@Override
	public String getPassword() {
		return user != null ? user.getPassword() : "UNKNOWN";
	}

	@Override
	public String getName() {
		return user != null ? user.getName() : "UNKNOWN";
	}

	public Long getUserId() {
		return user != null ? user.getId() : 0;
	}

	public Role getRole() {
		return user != null ? user.getRole() : Role.ROLE_USER;
	}

	public boolean getNeedSignup() {
		return attributes.get("needSignup") != null && (boolean)attributes.get("needSignup");
	}

	public String getUUID() {
		return attributes.get("uuid") != null ? (String)attributes.get("uuid") : null;
	}

	public Provider getProvider() {
		return attributes.get("provider") != null ? (Provider)attributes.get("provider") : null;
	}

	// Todo : 추후 계정 상태 체크 로직 추가

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

}
