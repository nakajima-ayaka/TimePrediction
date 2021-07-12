package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Time;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

class UserTest {

	@Test
	void コンストラクタ全部入りで値を設定_Getterで値が取得できるかどうか() {
		User user = new User(
				1, "test@gmail.com", "test", Time.valueOf("07:30:00"), 10, 1, 2, 3, 10);

		//各種フィールド取得
		int code = user.getCode();
		String email = user.getEmail();
		String password = user.getPassword();
		Time leaveHomeTime = user.getLeaveHomeTime();
		int homeStationTime = user.getHomeStationTime();
		int commuterCode1 = user.getCommuterCode1();
		int commuterCode2 = user.getCommuterCode2();
		int commuterCode3 = user.getCommuterCode3();
		int stationCompanyTime = user.getStationCompanyTime();

		assertEquals(code, 1);
		assertEquals(email, "test@gmail.com");
		assertEquals(password, "test");
		assertEquals(leaveHomeTime, LocalTime.parse("07:30:00"));
		assertEquals(homeStationTime, 10);
		assertEquals(commuterCode1, 1);
		assertEquals(commuterCode2, 2);
		assertEquals(commuterCode3, 3);
		assertEquals(stationCompanyTime, 10);
	}

	@Test
	void Setterで値が設定できるかどうか() {
		User user = new User();

		//各種フィールド設定
		user.setCode(1);
		user.setEmail("test@gmail.com");
		user.setPassword("test");
		user.setLeaveHomeTime(Time.valueOf("07:30:00"));
		user.setHomeStationTime(10);
		user.setCommuterCode1(1);
		user.setCommuterCode2(2);
		user.setCommuterCode3(3);
		user.setStationCompanyTime(10);

		//各種フィールド取得
		int code = user.getCode();
		String email = user.getEmail();
		String password = user.getPassword();
		Time leaveHomeTime = user.getLeaveHomeTime();
		int homeStationTime = user.getHomeStationTime();
		int commuterCode1 = user.getCommuterCode1();
		int commuterCode2 = user.getCommuterCode2();
		int commuterCode3 = user.getCommuterCode3();
		int stationCompanyTime = user.getStationCompanyTime();

		assertEquals(code, 1);
		assertEquals(email, "test@gmail.com");
		assertEquals(password, "test");
		assertEquals(leaveHomeTime, Time.valueOf("07:30:00"));
		assertEquals(homeStationTime, 10);
		assertEquals(commuterCode1, 1);
		assertEquals(commuterCode2, 2);
		assertEquals(commuterCode3, 3);
		assertEquals(stationCompanyTime, 10);
	}

	@Test
	void 新規登録時に使用するコンストラクタで値が設定できるかどうか() {
		User user = new User(
				"test@gmail.com", "test", Time.valueOf("07:30:00"), 10, 1, 2, 3, 10);

		//各種フィールド取得
		String email = user.getEmail();
		String password = user.getPassword();
		Time leaveHomeTime = user.getLeaveHomeTime();
		int homeStationTime = user.getHomeStationTime();
		int commuterCode1 = user.getCommuterCode1();
		int commuterCode2 = user.getCommuterCode2();
		int commuterCode3 = user.getCommuterCode3();
		int stationCompanyTime = user.getStationCompanyTime();

		assertEquals(email, "test@gmail.com");
		assertEquals(password, "test");
		assertEquals(leaveHomeTime, LocalTime.parse("07:30:00"));
		assertEquals(homeStationTime, 10);
		assertEquals(commuterCode1, 1);
		assertEquals(commuterCode2, 2);
		assertEquals(commuterCode3, 3);
		assertEquals(stationCompanyTime, 10);

	}

}
