package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class WeatherTest {

	@Test
	void コンストラクタ引数ありで値が設定されCodeに値が設定されていないかどうか() {
		Weather weather = new Weather("小雨", 0.2);

		//各種フィールド取得
		int code = weather.getCode();
		String element = weather.getElement();
		double coefficient = weather.getCoefficient();

		assertEquals(code, 0);
		assertEquals(element, "小雨");
		assertEquals(coefficient, 0.2);
	}

	@Test
	void Setterでcodeとelementとcoefficientを設定_Getterで値が取得できるかどうか() {
		Weather weather = new Weather();

		//各種フィールド設定
		weather.setCode(1);
		weather.setElement("小雨");
		weather.setCoefficient(0.2);

		//各種フィールド取得
		int code = weather.getCode();
		String element = weather.getElement();
		double coefficient = weather.getCoefficient();

		assertEquals(code, 1);
		assertEquals(element, "小雨");
		assertEquals(coefficient, 0.2);
	}
}
