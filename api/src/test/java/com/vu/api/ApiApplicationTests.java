package com.vu.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class ApiApplicationTests {

	@Test
	void contextLoads() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println("ADMIN_HASH: " + encoder.encode("admin"));
		System.out.println("STAFF_HASH: " + encoder.encode("staff"));
	}

}
