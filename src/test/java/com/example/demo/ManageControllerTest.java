package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
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
	WeatherRepository weatherRepository;

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

		//DBに登録されている情報を取得
		List<Railway> dbRailways = railwayRepository.findAll();

		//DBのdelayFrequencyをリスト化
		List<String> dbDelayFrequency = new ArrayList<String>();
		for (Railway railway : dbRailways) {
			switch (railway.getDelayFrequency()) {
			case 1:
				dbDelayFrequency.add("低い");
				break;
			case 2:
				dbDelayFrequency.add("普通");
				break;
			case 3:
				dbDelayFrequency.add("高い");
				break;
			}
		}

		//dbDelayFrequencyをmapで集計
		Map<String, List<String>> dbMap = dbDelayFrequency.stream().collect(Collectors.groupingBy(i -> i));

		MvcResult result = mockMvc.perform(post("/railways"))
				.andExpect(status().isOk())
				.andExpect(view().name("railway"))
				.andReturn();

		// mvに追加されたrailwaysの値を取得
		List<Railway> railways = (List<Railway>) result.getModelAndView().getModel().get("railways");

		//railwaysをテスト
		assertEquals(railways.size(), dbRailways.size());

		// mvに追加されたrailwaysの値を取得
		List<String> delayFrequency = (List<String>) result.getModelAndView().getModel().get("delayFrequencys");

		//delayFrequencyをmapで集計
		Map<String, List<String>> map = delayFrequency.stream().collect(Collectors.groupingBy(i -> i));

		//delayFrequencyをテスト(各行数を比較)
		assertEquals(map.get("低い").size(), dbMap.get("低い").size());
		assertEquals(map.get("普通").size(), dbMap.get("普通").size());
		assertEquals(map.get("高い").size(), dbMap.get("高い").size());
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

		//テスト用に追加用レコードを作成
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
	@Transactional
	void 鉄道の削除処理で遅延情報テーブルで使われている場合にメッセージを表示できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(post("/railways/delete")
				.param("code", "2"))
				.andExpect(status().isOk())
				.andExpect(view().name("railway"))
				.andReturn();

		// mvに追加されたweathersの値を取得
		String message = (String) result.getModelAndView().getModel().get("message");

		//メッセージをテスト
		assertEquals(message, "京王線は他のテーブルで使用されています。");
	}

	@Test
	@Transactional
	void 鉄道の削除処理でユーザテーブルで使われている場合にメッセージを表示できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(post("/railways/delete")
				.param("code", "1"))
				.andExpect(status().isOk())
				.andExpect(view().name("railway"))
				.andReturn();

		// mvに追加されたweathersの値を取得
		String message = (String) result.getModelAndView().getModel().get("message");

		//メッセージをテスト
		assertEquals(message, "未登録は他のテーブルで使用されています。");
	}

	@Test
	void 遅延情報一覧表示画面に遷移できるかどうか() throws Exception {

		//DBに登録されている情報を取得
		List<Weather> dbWeathers = weatherRepository.findAll();
		List<Delay> dbDelays = delayRepository.findAll();
		List<Railway> dbRailways = railwayRepository.findAll();

		MvcResult result = mockMvc.perform(post("/delays"))
				.andExpect(status().isOk())
				.andExpect(view().name("delay"))
				.andReturn();

		// mvに追加されたdelaysの値を取得
		List<Delay> delays = (List<Delay>) result.getModelAndView().getModel().get("delays");

		//delaysをテスト
		assertEquals(delays.size(), dbDelays.size());

		// mvに追加されたrailwaysの値を取得
		List<Railway> railways = (List<Railway>) result.getModelAndView().getModel().get("railways");

		//railwaysをテスト
		assertEquals(railways.size(), dbRailways.size());

		// mvに追加されたweathersの値を取得
		List<Weather> weathers = (List<Weather>) result.getModelAndView().getModel().get("weathers");

		//weathersをテスト
		assertEquals(weathers.size(), dbWeathers.size());

		// mvに追加されたweatherElementsの値を取得
		List<String> weatherElements = (List<String>) result.getModelAndView().getModel().get("weatherElements");

		//weatherElementsを比較
		assertEquals(weatherElements.get(0), "小雨");

		// mvに追加されたrailwayNamesの値を取得
		List<String> railwayNames = (List<String>) result.getModelAndView().getModel().get("railwayNames");

		//railwayNamesを比較
		assertEquals(railwayNames.get(0), "京王線");

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

	@Test
	void 天候の追加削除画面に遷移できるかどうか() throws Exception {

		//DBに登録されている情報を取得
		List<Weather> dbWeathers = weatherRepository.findAll();

		MvcResult result = mockMvc.perform(post("/weathers"))
				.andExpect(status().isOk())
				.andExpect(view().name("weathers"))
				.andReturn();

		// mvに追加されたweathersの値を取得
		List<Weather> weathers = (List<Weather>) result.getModelAndView().getModel().get("weathers");

		//weathersが取得できているかをテスト
		//weathers分繰り返す
		for (int i = 0; i < weathers.size(); i++) {
			//weathersからweatherを取得
			Weather weather = weathers.get(i);
			//codeを比較
			assertEquals(weather.getCode(), dbWeathers.get(i).getCode());
			//elementを比較
			assertEquals(weather.getElement(), dbWeathers.get(i).getElement());
			//coefficientを比較
			assertEquals(weather.getCoefficient(), dbWeathers.get(i).getCoefficient());

		}

	}

	@Test
	void 天候名から対象のレコードを取得できるかどうか() throws Exception {
		Weather weather = manageController.findByRecordWeatherName("小雨");

		//codeを比較
		assertEquals(weather.getCode(), 1);
		//elementを比較
		assertEquals(weather.getElement(), "小雨");
		//coefficientを比較
		assertEquals(weather.getCoefficient(), 0.10);

	}

	@Test
	void 存在しない天候名からnullを取得できるかどうか() throws Exception {
		Weather weather = manageController.findByRecordWeatherName("テスト天候");

		//nullと比較
		assertEquals(weather, null);
	}

	@Test
	void 管理者画面に遷移できるかどうか() throws Exception {
		mockMvc.perform(post("/manage"))
				.andExpect(status().isOk())
				.andExpect(view().name("manage"));
	}

	@Test
	@Transactional
	void ユーザの削除処理が実行できるかどうか() throws Exception {

		//テスト用に追加用レコードを作成
		User testUser = new User(
				"test@gmail.com", "test", Time.valueOf("07:30:00"), 10, 1, 2, 3, 10);

		//テスト用レコードの追加
		testUser = userRepository.saveAndFlush(testUser);

		//テスト用を追加したコードを取得
		int size = testUser.getCode();

		//実行前にuserが存在することを確認
		Optional<User> testRecord = userRepository.findById(size);
		assertFalse(testRecord.isEmpty());

		//処理実行
		mockMvc.perform(post("/users/" + size + "/delete"))
				.andExpect(status().isOk())
				.andExpect(view().name("users"));

		//実行後にuserが存在しないことを確認
		Optional<User> record = userRepository.findById(size);
		assertTrue(record.isEmpty());

	}

	@Test
	@Transactional
	void 天候の削除処理が実行できるかどうか() throws Exception {

		//テスト用に追加用レコードを作成
		Weather testWeather = new Weather("テスト天候", 0.2);

		//テスト用レコードの追加
		testWeather = weatherRepository.saveAndFlush(testWeather);

		//テスト用を追加したコードを取得
		int size = testWeather.getCode();

		//実行前にweatherが存在することを確認
		Optional<Weather> weather = weatherRepository.findById(size);
		assertFalse(weather.isEmpty());

		//処理実行
		mockMvc.perform(post("/weathers/" + size + "/delete"))
				.andExpect(status().isOk())
				.andExpect(view().name("weathers"));

		//実行後にuserが存在しないことを確認
		weather = weatherRepository.findById(size);
		assertTrue(weather.isEmpty());

	}

	@Test
	@Transactional
	void 天候の削除処理で対象がない場合を検知できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(post("/weathers/99/delete"))
				.andExpect(status().isOk())
				.andReturn();

		// mvに追加されたweathersの値を取得
		String message = (String) result.getModelAndView().getModel().get("message");

		//メッセージをテスト
		assertEquals(message, "削除対象がありません。");

	}

	@Test
	@Transactional
	void 天候の削除処理で遅延情報テーブルで使われている場合にメッセージを表示できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(post("/weathers/2/delete"))
				.andExpect(status().isOk())
				.andReturn();

		// mvに追加されたweathersの値を取得
		String message = (String) result.getModelAndView().getModel().get("message");

		//メッセージをテスト
		assertEquals(message, "雨は他のテーブルで使用されています。");

	}

	@Test
	@Transactional
	void 天候の追加処理が実行できるかどうか() throws Exception {

		mockMvc.perform(post("/weathers/add")
				.param("element", "テスト天候")
				.param("coefficient", "0.5"))
				.andExpect(status().isOk())
				.andExpect(view().name("weathers"));

		//テスト用に追加用レコードを作成
		Weather testWeather = new Weather("テスト天候2", 0.2);

		//テスト用レコードの追加
		testWeather = weatherRepository.saveAndFlush(testWeather);

		//テスト用を追加したコードから1を引いた数値を取得
		int size = testWeather.getCode() - 1;

		//mockで追加したレコードを取得
		Optional<Weather> record = weatherRepository.findById(size);
		Weather weather = record.get();

		//追加されたものと比較
		assertEquals(weather.getElement(), "テスト天候");
		assertEquals(weather.getCoefficient(), 0.50);
	}

	@Test
	@Transactional
	void 天候の追加処理で天候の重複を判定できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(post("/weathers/add")
				.param("element", "小雨")
				.param("coefficient", "0.5"))
				.andExpect(status().isOk())
				.andExpect(view().name("weathers"))
				.andReturn();

		// mvに追加されたmessageの値を取得
		String message = (String) result.getModelAndView().getModel().get("message");

		//メッセージをテスト
		assertEquals(message, "小雨は既に登録されています。");
	}

	@Test
	@Transactional
	void 天候の追加処理で天候の未記入を判定できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(post("/weathers/add")
				.param("element", "")
				.param("coefficient", "0.5"))
				.andExpect(status().isOk())
				.andExpect(view().name("weathers"))
				.andReturn();

		// mvに追加されたmessageの値を取得
		String message = (String) result.getModelAndView().getModel().get("message");

		//メッセージをテスト
		assertEquals(message, "入力してください");
	}

	@Test
	@Transactional
	void 天候の追加処理で遅延係数の未記入を判定できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(post("/weathers/add")
				.param("element", "テスト天候")
				.param("coefficient", ""))
				.andExpect(status().isOk())
				.andExpect(view().name("weathers"))
				.andReturn();

		// mvに追加されたmessageの値を取得
		String message = (String) result.getModelAndView().getModel().get("message");

		//メッセージをテスト
		assertEquals(message, "入力してください");
	}

}