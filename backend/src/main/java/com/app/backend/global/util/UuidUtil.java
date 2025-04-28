package com.app.backend.global.util;

import java.util.UUID;

public class UuidUtil {

	public static String getUuid(final int length) {
		return UUID.randomUUID().toString().substring(0, length);
	}

	public static String getUuid(final String prefix, final int length) {
		return prefix + UUID.randomUUID().toString().substring(0, length);
	}
}
