package com.optimiza.ehope.web.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CustomPasswordEncoder extends BCryptPasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		return super.encode(rawPassword);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return super.matches(rawPassword, encodedPassword);
	}
}
