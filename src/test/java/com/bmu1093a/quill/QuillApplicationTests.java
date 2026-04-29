package com.bmu1093a.quill;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class QuillApplicationTest {

	@Test
	void contextLoads() {
		QuillApplication.main(new String[]{});

		assertNotNull(QuillApplication.class);
	}
}