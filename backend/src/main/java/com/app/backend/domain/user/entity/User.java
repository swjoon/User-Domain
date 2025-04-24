package com.app.backend.domain.user.entity;

import java.time.LocalDate;

import com.app.backend.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "tbl_users")
@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", nullable = false, updatable = false)
	private Long id;

	@Column(unique = true, nullable = false)
	private String username;

	@Column
	private String password;

	@Column(nullable = false, length = 20)
	private String name;

	@Column(unique = true, length = 30)
	private String nickname;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(unique = true, nullable = false)
	private String phone;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(nullable = false)
	private LocalDate birthDate;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private SocialType socialType = SocialType.LOCAL;

	private String providerId;

	private String profileImageUrl;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private Role role = Role.ROLE_USER;
}
