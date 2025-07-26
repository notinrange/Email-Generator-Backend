package com.email.writer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmailWriterSbApplicationTests {

	@Test
	void contextLoads() {
	}

}


// import redis.embedded.RedisServer;

// @SpringBootTest
// class EmailWriterSbApplicationTests {
//   private static RedisServer redis;

//   @BeforeAll
//   static void startRedis() throws IOException {
//     redis = new RedisServer(6379);
//     redis.start();
//   }

//   @AfterAll
//   static void stopRedis() {
//     redis.stop();
//   }

//   @Test
//   void contextLoads() { }
// }

