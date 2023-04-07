package org.babinkuk.validator;

import java.util.Arrays;

public enum ValidatorRole {
	
	ROLE_EMPLOYEE,
	ROLE_ADMIN,
	ROLE_MANAGER;
	
	public static ValidatorRole valueOfIgnoreCase(String str) {
		return Arrays.stream(ValidatorRole.values())
				.filter(e -> e.name().equalsIgnoreCase(str))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("Cannot find enum constant for " + str));
	}
}
