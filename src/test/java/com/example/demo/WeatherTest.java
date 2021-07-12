package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class WeatherTest {

	@Test
	void コンストラクタ引数ありでelementを設定_elementに値が設定されCodeに値が設定されていないかどうか() {
		Weather weather = new Weather("小雨");

		//各種フィールド取得
		int code = weather.getCode();
		String element = weather.getElement();

		assertEquals(code, 0);
		assertEquals(element, "小雨");
	}

	@Test
	void Setterでcodeとelementを設定_Getterで値が取得できるかどうか() {
		Weather weather = new Weather();

		//各種フィールド設定
		weather.setCode(1);
		weather.setElement("小雨");

		//各種フィールド取得
		int code = weather.getCode();
		String element = weather.getElement();

		assertEquals(code, 1);
		assertEquals(element, "小雨");
	}
}
