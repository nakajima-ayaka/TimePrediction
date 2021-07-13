package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RailwayTest {

	@Autowired
	RailwayRepository railwayRepository;

	@Test
	void コンストラクタ全部入りで値を設定_Getterで値が取得できるかどうか() {

		//コンストラクタで値を設定
		Railway railway = new Railway(16, "テスト路線", 2);

		//各種フィールド取得
		int code = railway.getCode();
		String name = railway.getName();
		int delayFrequency = railway.getDelayFrequency();

		assertEquals(code, 16);
		assertEquals(name, "テスト路線");
		assertEquals(delayFrequency, 2);
	}

	@Test
	void 登録用コンストラクタで値を設定_Getterで値が取得できるかどうか() {

		//コンストラクタで値を設定
		Railway railway = new Railway("a路線", 15);

		//各種フィールド取得
		int code = railway.getCode();
		String name = railway.getName();
		int delayFrequency = railway.getDelayFrequency();

		assertEquals(code, 0);
		assertEquals(name, "a路線");
		assertEquals(delayFrequency, 15);
	}

	@Test
	void 遅延頻度取得処理で3以外の場合nullが返ってくるかどうか() {

		//Railwayを宣言
		Railway railway = new Railway();

		//京王線を検索
		Optional<Railway> record = railwayRepository.findById(2);

		//メソッドの結果をresultへ
		String result = railway.DelayFrequency(record);

		assertEquals(result, null);
	}

	@Test
	void 遅延頻度取得処理で3の場合鉄道名が返ってくるかどうか() {

		//Railwayを宣言
		Railway railway = new Railway();

		//小田急線を検索
		Optional<Railway> record = railwayRepository.findById(3);

		//メソッドの結果をresultへ
		String result = railway.DelayFrequency(record);

		assertEquals(result, "小田急線");
	}

	@Test
	void 遅延頻度取得処理でレコードがnullの場合nulが返ってくるかどうか() {

		//Railwayを宣言
		Railway railway = new Railway();

		//存在しないIDを検索
		Optional<Railway> record = railwayRepository.findById(0);

		//メソッドの結果をresultへ
		String result = railway.DelayFrequency(record);

		assertEquals(result, null);
	}

	@Test
	void 徒歩時間計算処理で小雨の場合の計算結果が正しいかどうか() {

		//Railwayを宣言
		Railway railway = new Railway();

		//メソッドの結果をdelayWalkTimeへ
		int delayWalkTime = railway.DelayWalk(10, 10, 1);

		assertEquals(delayWalkTime, 2);
	}

	@Test
	void 徒歩時間計算処理で雨の場合の計算結果が正しいかどうか() {

		//Railwayを宣言
		Railway railway = new Railway();

		//メソッドの結果をdelayWalkTimeへ
		int delayWalkTime = railway.DelayWalk(20, 15, 2);

		assertEquals(delayWalkTime, 7);
	}

	@Test
	void 徒歩時間計算処理で大雨の場合の計算結果が正しいかどうか() {

		//Railwayを宣言
		Railway railway = new Railway();

		//メソッドの結果をdelayWalkTimeへ
		int delayWalkTime = railway.DelayWalk(30, 17, 3);

		assertEquals(delayWalkTime, 18);
	}

}
