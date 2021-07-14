package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Time;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserRepositoryTest {

	@Autowired
	UserRepository userRepository;

	@Test
	void メールアドレス検索でユーザ情報がすべて取得できるかどうか() {
		Optional<User> users = userRepository.findByEmail("a@gmail.com");
		User user = users.get();

		//比較
		assertEquals(user.getCode(), 2);
		assertEquals(user.getEmail(), "a@gmail.com");
		assertEquals(user.getPassword(), "a");
		assertEquals(user.getLeaveHomeTime(), Time.valueOf("07:30:00"));
		assertEquals(user.getHomeStationTime(), 10);
		assertEquals(user.getCommuterCode1(), 2);
		assertEquals(user.getCommuterCode2(), 5);
		assertEquals(user.getCommuterCode3(), 8);
		assertEquals(user.getStationCompanyTime(), 10);

	}

}
