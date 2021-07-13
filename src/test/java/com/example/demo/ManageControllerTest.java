package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootTest //repositoryなど他のクラスを使用する際に使うテスト用アノテーション
@EnableWebMvc // WebアプリケーションのMVCモデルをテストで使用可能にするアノテーション
class ManageControllerTest {

	private MockMvc mockMvc;

	@Autowired
	UserRepository userRepository;

	@Autowired
	WebApplicationContext webApplicationContext;

	@Autowired
	ManageController manageController;

	@Autowired
	RailwayRepository railwayRepository;

	@Autowired
	DelayRepository delayRepository;

	@Autowired
	MockHttpSession session;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void 鉄道一覧表示画面に遷移できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(post("/railways"))
				.andExpect(status().isOk())
				.andExpect(view().name("railway"))
				.andReturn();

		// mvに追加されたrailwaysの値を取得
		List<Railway> railways = (List<Railway>) result.getModelAndView().getModel().get("railways");

		//railwaysをテスト
		assertEquals(railways.size(), 15);

		// mvに追加されたrailwaysの値を取得
		List<String> delayFrequency = (List<String>) result.getModelAndView().getModel().get("delayFrequencys");

		//delayFrequencyをmapで集計
		Map<String, List<String>> map = delayFrequency.stream().collect(Collectors.groupingBy(i -> i));

		//delayFrequencyをテスト(各行数を比較)
		assertEquals(map.get("低い").size(), 7);
		assertEquals(map.get("普通").size(), 3);
		assertEquals(map.get("高い").size(), 5);
	}

	@Test
	@Transactional
	void 鉄道の追加処理が実行できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(post("/railways/add")
				.param("railwayName", "テスト線")
				.param("delayFrequency", "2"))
				.andExpect(status().isOk())
				.andExpect(view().name("railway"))
				.andReturn();

		//サイズ取得用に追加用レコードを作成
		Railway testRailway = new Railway("test1", 1);

		//テスト用レコードの追加
		testRailway = railwayRepository.saveAndFlush(testRailway);

		//テスト用を追加したコードから1を引いた数値を取得
		int size = testRailway.getCode() - 1;

		//mockで追加したレコードを取得
		Optional<Railway> record = railwayRepository.findById(size);
		Railway railway = record.get();

		//追加されたものと比較
		assertEquals(railway.getName(), "テスト線");
		assertEquals(railway.getDelayFrequency(), 2);
	}

	@Test
	@Transactional
	void 鉄道の追加処理で鉄道名の重複を判定できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(post("/railways/add")
				.param("railwayName", "京王線")
				.param("delayFrequency", "2"))
				.andExpect(status().isOk())
				.andExpect(view().name("railway"))
				.andReturn();

		// mvに追加されたmessageの値を取得
		String message = (String) result.getModelAndView().getModel().get("message");

		//メッセージをテスト
		assertEquals(message, "京王線は既に登録されています。");
	}

	@Test
	@Transactional
	void 鉄道の追加処理で鉄道名の未記入を判定できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(post("/railways/add")
				.param("railwayName", "")
				.param("delayFrequency", "2"))
				.andExpect(status().isOk())
				.andExpect(view().name("railway"))
				.andReturn();

		// mvに追加されたmessageの値を取得
		String message = (String) result.getModelAndView().getModel().get("message");

		//メッセージをテスト
		assertEquals(message, "入力してください");
	}

	@Test
	@Transactional
	void 鉄道の削除処理が実行できるかどうか() throws Exception {

		//テスト用に追加用レコードを作成
		Railway testRailway = new Railway("test1", 1);

		//テスト用レコードの追加
		testRailway = railwayRepository.saveAndFlush(testRailway);

		//テスト用を追加したコードを取得
		String size = testRailway.getCode() + "";

		//処理実行
		MvcResult result = mockMvc.perform(post("/railways/delete")
				.param("code", size))
				.andExpect(status().isOk())
				.andExpect(view().name("railway"))
				.andReturn();

		//テストで追加したレコードを取得
		Optional<Railway> record = railwayRepository.findById(testRailway.getCode());

		//削除されていればTrue
		assertTrue(record.isEmpty());
	}

	@Test
	@Transactional
	void 鉄道の削除処理で存在しない鉄道番号の場合にメッセージを表示できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(post("/railways/delete")
				.param("code", "999"))
				.andExpect(status().isOk())
				.andExpect(view().name("railway"))
				.andReturn();

		// mvに追加されたweathersの値を取得
		String message = (String) result.getModelAndView().getModel().get("message");

		//メッセージをテスト
		assertEquals(message, "削除対象の鉄道番号：999が見つかりません");
	}

	@Test
	void 遅延情報一覧表示画面に遷移できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(post("/delays"))
				.andExpect(status().isOk())
				.andExpect(view().name("delay"))
				.andReturn();

		// mvに追加されたdelaysの値を取得
		List<Delay> delays = (List<Delay>) result.getModelAndView().getModel().get("delays");

		//delaysをテスト
		assertEquals(delays.size(), 45);

		// mvに追加されたrailwaysの値を取得
		List<Railway> railways = (List<Railway>) result.getModelAndView().getModel().get("railways");

		//railwaysをテスト
		assertEquals(railways.size(), 15);

		// mvに追加されたweathersの値を取得
		List<Weather> weathers = (List<Weather>) result.getModelAndView().getModel().get("weathers");

		//weathersをテスト
		assertEquals(weathers.size(), 3);

		// mvに追加されたweatherElementsの値を取得
		List<String> weatherElements = (List<String>) result.getModelAndView().getModel().get("weatherElements");

		//weatherElementsを比較
		assertEquals(weatherElements.get(0), "小雨");
		assertEquals(weatherElements.get(44), "大雨");

		// mvに追加されたrailwayNamesの値を取得
		List<String> railwayNames = (List<String>) result.getModelAndView().getModel().get("railwayNames");

		//railwayNamesを比較
		assertEquals(railwayNames.get(0), "未登録");
		assertEquals(railwayNames.get(44), "都営地下鉄大江戸線");

	}

	@Test
	@Transactional
	void 遅延情報の追加処理が実行できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(post("/delays/add")
				.param("weatherCode", "1")
				.param("commuterCode", "2")
				.param("delayTime", "10")
				.param("delayDate", "2021-07-13"))
				.andExpect(status().isOk())
				.andExpect(view().name("delay"))
				.andReturn();

		//サイズ取得用に追加用レコードを作成
		Delay testDelay = new Delay(2, 10, 22, Date.valueOf("2021-01-01"));

		//テスト用レコードの追加
		testDelay = delayRepository.saveAndFlush(testDelay);

		//テスト用を追加したコードから1を引いた数値を取得
		int size = testDelay.getCode() - 1;

		//mockで追加したレコードを取得
		Optional<Delay> record = delayRepository.findById(size);
		Delay delay = record.get();

		//追加されたものと比較
		assertEquals(delay.getWeatherCode(), 1);
		assertEquals(delay.getRailwayCode(), 2);
		assertEquals(delay.getDelayTime(), 10);
		assertEquals(delay.getDate(), Date.valueOf("2021-07-13"));
	}

	@Test
	@Transactional
	void 遅延情報の追加処理で遅延時間が0であることを判定できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(post("/delays/add")
				.param("weatherCode", "1")
				.param("commuterCode", "2")
				.param("delayTime", "0")
				.param("delayDate", "2021-07-13"))
				.andExpect(status().isOk())
				.andExpect(view().name("delay"))
				.andReturn();

		// mvに追加されたmessageの値を取得
		String message = (String) result.getModelAndView().getModel().get("message");

		//メッセージをテスト
		assertEquals(message, "遅延時間を入力してください");

	}

	@Test
	@Transactional
	void 遅延情報の削除処理が実行できるかどうか() throws Exception {

		//テスト用に追加用レコードを作成
		Delay testDelay = new Delay(2, 10, 22, Date.valueOf("2021-01-01"));

		//テスト用レコードの追加
		testDelay = delayRepository.saveAndFlush(testDelay);

		//テスト用を追加したコードを取得
		String size = testDelay.getCode() + "";

		//処理実行
		MvcResult result = mockMvc.perform(post("/delays/delete")
				.param("code", size))
				.andExpect(status().isOk())
				.andExpect(view().name("delay"))
				.andReturn();

		//テストで追加したレコードを取得
		Optional<Delay> record = delayRepository.findById(testDelay.getCode());

		//削除されていればTrue
		assertTrue(record.isEmpty());
	}

	@Test
	@Transactional
	void 遅延情報の削除処理で存在しない遅延番号の場合にメッセージを表示できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(post("/delays/delete")
				.param("code", "999"))
				.andExpect(status().isOk())
				.andExpect(view().name("delay"))
				.andReturn();

		// mvに追加されたweathersの値を取得
		String message = (String) result.getModelAndView().getModel().get("message");

		//メッセージをテスト
		assertEquals(message, "削除対象の遅延番号：999が見つかりません");
	}

	@Test
	void 天候検索処理で天候番号から天候を取得できるかどうか() throws Exception {

		//天候番号1の天候を取得
		String element = manageController.findWeatherElement(1);

		//メッセージをテスト
		assertEquals(element, "小雨");
	}

	@Test
	void 天候検索処理で存在しない天候番号からnullを取得できるかどうか() throws Exception {

		//天候番号1の天候を取得
		String element = manageController.findWeatherElement(10);

		//メッセージをテスト
		assertEquals(element, null);
	}

	@Test
	void 鉄道検索処理で鉄道番号から鉄道名を取得できるかどうか() throws Exception {

		//鉄道番号2の鉄道名を取得
		String railwayName = manageController.findRailway(2);

		//メッセージをテスト
		assertEquals(railwayName, "京王線");
	}

	@Test
	void 鉄道検索処理で存在しない鉄道番号からnullを取得できるかどうか() throws Exception {

		//鉄道番号2の鉄道名を取得
		String railwayName = manageController.findRailway(100);

		//メッセージをテスト
		assertEquals(railwayName, null);
	}

}