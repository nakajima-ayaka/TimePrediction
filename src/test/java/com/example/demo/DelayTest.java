package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class DelayTest {

	@Test
	void コンストラクタ全部入りで値を設定_Getterで値が取得できるかどうか() {
		Delay delay = new Delay(1, 1, 2, 5, Date.valueOf("2021-07-01"));

		//各種フィールド取得
		int code = delay.getCode();
		int weatherCode = delay.getWeatherCode();
		int railwayCode = delay.getRailwayCode();
		int delayTime = delay.getDelayTime();
		Date date = delay.getDate();

		assertEquals(code, 1);
		assertEquals(weatherCode, 1);
		assertEquals(railwayCode, 2);
		assertEquals(delayTime, 5);
		assertEquals(date, Date.valueOf("2021-07-01"));
	}

	@Test
	void Setterで値が設定できるかどうか() {
		Delay delay = new Delay();

		//各種フィールド設定
		delay.setCode(1);
		delay.setWeatherCode(1);
		delay.setRailwayCode(2);
		delay.setDelayTime(5);
		delay.setDate(Date.valueOf("2021-07-01"));

		//各種フィールド取得
		int code = delay.getCode();
		int weatherCode = delay.getWeatherCode();
		int railwayCode = delay.getRailwayCode();
		int delayTime = delay.getDelayTime();
		Date date = delay.getDate();

		assertEquals(code, 1);
		assertEquals(weatherCode, 1);
		assertEquals(railwayCode, 2);
		assertEquals(delayTime, 5);
		assertEquals(date, Date.valueOf("2021-07-01"));
	}

	@Test
	void 登録時に使用するコンストラクタで値が設定できるかどうか() {
		Delay delay = new Delay(1, 2, 5, Date.valueOf("2021-07-01"));

		//各種フィールド取得
		int code = delay.getCode();
		int weatherCode = delay.getWeatherCode();
		int railwayCode = delay.getRailwayCode();
		int delayTime = delay.getDelayTime();
		Date date = delay.getDate();

		assertEquals(code, 0);
		assertEquals(weatherCode, 1);
		assertEquals(railwayCode, 2);
		assertEquals(delayTime, 5);
		assertEquals(date, Date.valueOf("2021-07-01"));
	}

	@Test
	void averageメソッドで平均値が取得できるかどうか() {
		Delay delay = new Delay();
		List<Delay> delays = new ArrayList<>();
		delays.add(new Delay(1, 1, 2, 5, Date.valueOf("2021-07-01")));
		delays.add(new Delay(1, 2, 2, 10, Date.valueOf("2021-07-02")));
		delays.add(new Delay(1, 3, 2, 15, Date.valueOf("2021-07-03")));

		int average = delay.average(delays);

		if (average == 10) {
			assertTrue(true);
			return;
		}
		fail("結果に相違があります。");
	}

}
