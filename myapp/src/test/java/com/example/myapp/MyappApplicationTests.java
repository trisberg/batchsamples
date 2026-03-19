package com.example.myapp;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MyappApplicationTests {

	@BeforeAll
	static void setSystemProperties() {
		System.setProperty("spring.batch.jdbc.schema.legacy", "true");
	}

	@Test
	void contextLoads() {
	}

}
