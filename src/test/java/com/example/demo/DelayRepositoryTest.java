package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class findByWeatherCodeAndRailwayCode {

	@Autowired
	DelayRepository delayRepository;

	@Test
	void 天候番号と鉄道番号から該当の遅延情報Listを取得できるかどうか() {

		//小雨・京王線を指定
		//select * from delay where railway_code = 2 and weather_code=1;
		// code | weather_code | railway_code | delay_time |    date
		//------+--------------+--------------+------------+------------
		//    4 |            1 |            2 |          5 | 2021-07-01
		List<Delay> delays = delayRepository.findByWeatherCodeAndRailwayCode(1, 2);
		Delay delay = delays.get(0);

		//各種フィールド取得
		int code = delay.getCode();
		int weatherCode = delay.getWeatherCode();
		int railwayCode = delay.getRailwayCode();
		int delayTime = delay.getDelayTime();
		Date date = delay.getDate();

		assertEquals(delays.size(), 1);
		assertEquals(code, 1);
		assertEquals(weatherCode, 1);
		assertEquals(railwayCode, 2);
		assertEquals(delayTime, 5);
		assertEquals(date, Date.valueOf("2021-07-01"));

	}

}
