package com.app.backend.global.util;

import com.app.backend.domain.user.dto.request.CreateUserLocalDto;
import com.app.backend.domain.user.entity.User;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin;

public class FixtureMonkeyUtil {

	public static final FixtureMonkey fixtureMonkeyBuilder = FixtureMonkey.builder()
		.objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
		.defaultNotNull(true)
		.build();

	public static final FixtureMonkey fixtureMonkeyRecord = FixtureMonkey.builder()
		.objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
		.defaultNotNull(true)
		.build();

	public static final FixtureMonkey fixtureMonkeyValidation = FixtureMonkey.builder()
		.objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
		.defaultNotNull(true)
		.plugin(new JakartaValidationPlugin())
		.build();

	public static User createUser(final String username) {
		return fixtureMonkeyBuilder
			.giveMeBuilder(User.class)
			.set("id", null)
			.set("username", username)
			.set("password", "testpassword")
			.set("name", "testname")
			.set("nickname", "testnickname")
			.sample();
	}
}
