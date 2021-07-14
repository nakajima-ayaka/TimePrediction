package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Time;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

//@SpringBootTest
//@ExtendWith(SpringExtension.class)
//@WebMvcTest(AccountController.class)
@SpringBootTest //repositoryなど他のクラスを使用する際に使うテスト用アノテーション
@EnableWebMvc // WebアプリケーションのMVCモデルをテストで使用可能にするアノテーション
class AccountControllerTest {

	private MockMvc mockMvc;

	@Autowired
	UserRepository userRepository;

	@Autowired
	WebApplicationContext webApplicationContext;

	@Autowired
	AccountController accountController;

	@Autowired
	WeatherRepository weatherRepository;

	@Autowired
	MockHttpSession session;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void ログイン画面に遷移できるかどうか() throws Exception {
		mockMvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("login"));
	}

	@Test
	void ログイン処理が実行できるかどうか() throws Exception {
		//DBに登録されている天候を取得
		List<Weather> dbWeathers = weatherRepository.findAll();

		MvcResult result = mockMvc.perform(
				post("/login")
						.param("email", "a@gmail.com")
						.param("password", "a"))
				.andExpect(status().isOk())
				.andExpect(view().name("weather"))
				.andReturn();

		//セッションを取得
		MockHttpSession mockSession = (MockHttpSession) result.getRequest().getSession();

		//railwayName1をテスト
		assertEquals(mockSession.getAttribute("railwayName1"), "京王線");

		//railwayName2をテスト
		assertEquals(mockSession.getAttribute("railwayName2"), "JR中央線");

		//railwayName3をテスト
		assertEquals(mockSession.getAttribute("railwayName3"), "JR埼京線");

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
	void メールアドレスの間違いを検知できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(
				post("/login")
						.param("email", "aa@gmail.com")
						.param("password", "a"))
				.andReturn();

		// mvに追加されたweathersの値を取得
		String message = (String) result.getModelAndView().getModel().get("message");

		//メッセージをテスト
		assertEquals(message, "メールアドレスもしくはパスワードが間違っています");
	}

	@Test
	void パスワードの間違いを検知できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(
				post("/login")
						.param("email", "a@gmail.com")
						.param("password", "aa"))
				.andReturn();

		// mvに追加されたweathersの値を取得
		String message = (String) result.getModelAndView().getModel().get("message");

		//メッセージをテスト
		assertEquals(message, "メールアドレスもしくはパスワードが間違っています");
	}

	@Test
	void メールアドレスが空なことを検知できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(
				post("/login")
						.param("email", "")
						.param("password", "a"))
				.andReturn();

		// mvに追加されたweathersの値を取得
		String message = (String) result.getModelAndView().getModel().get("message");

		//メッセージをテスト
		assertEquals(message, "入力してください");
	}

	@Test
	void パスワードが空なことを検知できるかどうか() throws Exception {

		MvcResult result = mockMvc.perform(
				post("/login")
						.param("email", "a@gmail.com")
						.param("password", ""))
				.andReturn();

		// mvに追加されたweathersの値を取得
		String message = (String) result.getModelAndView().getModel().get("message");

		//メッセージをテスト
		assertEquals(message, "入力してください");
	}

	@Test
	void メールアドレスを検索してUserが取得できているかどうか() {

		//データベースから取得
		User user = accountController.findEmail("a@gmail.com");

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

	@Test
	void 鉄道番号から登録路線名の検索ができるかどうか() {
		//データベースから取得
		String railwayName = accountController.findRailway(2);

		//比較
		assertEquals(railwayName, "京王線");

	}

	@Test
	void 存在しない鉄道番号で登録路線名の検索した場合_nullが返ってくるかどうか() {
		//データベースから取得
		String railwayName = accountController.findRailway(0);

		//比較
		assertEquals(railwayName, null);

	}

	@Test
	void 新規登録画面に遷移できるかどうか() throws Exception {
		mockMvc.perform(get("/signup"))
				.andExpect(status().isOk())
				.andExpect(view().name("signup"));
	}

	@Test
	//JUnitは標準でロールバックしてくれる
	@Transactional
	void 新規登録処理が実行できるかどうか() throws Exception {
		mockMvc.perform(post("/signup")
				.param("email", "f@gmail.com")
				.param("password", "f")
				.param("leaveHomeTime", "07:55")
				.param("homeStationTime", "20")
				.param("commuterCode1", "12")
				.param("commuterCode2", "11")
				.param("commuterCode3", "1")
				.param("stationCompanyTime", "5"))
				.andExpect(status().isOk())
				.andExpect(view().name("login"));

		//テスト用に追加用レコードを作成
		User testUser = new User(
				"test@gmail.com", "test", Time.valueOf("07:30:00"), 10, 1, 2, 3, 10);

		//テスト用レコードの追加
		testUser = userRepository.saveAndFlush(testUser);

		//テスト用を追加したコードから1を引いた数値を取得
		int size = testUser.getCode() - 1;

		//mockで追加したレコードを取得
		Optional<User> record = userRepository.findById(size);
		User user = record.get();

		//追加されたものと比較
		assertEquals(user.getEmail(), "f@gmail.com");
		assertEquals(user.getPassword(), "f");
		assertEquals(user.getLeaveHomeTime(), Time.valueOf("07:55:00"));
		assertEquals(user.getHomeStationTime(), 20);
		assertEquals(user.getCommuterCode1(), 12);
		assertEquals(user.getCommuterCode2(), 11);
		assertEquals(user.getCommuterCode3(), 1);
		assertEquals(user.getStationCompanyTime(), 5);

	}

	@Test
	//JUnitは標準でロールバックしてくれる
	@Transactional
	void 新規登録処理でメールアドレスが空なことを検知できるかどうか() throws Exception {
		MvcResult result = mockMvc.perform(post("/signup")
				.param("email", "")
				.param("password", "f")
				.param("leaveHomeTime", "07:55")
				.param("homeStationTime", "20")
				.param("commuterCode1", "12")
				.param("commuterCode2", "11")
				.param("commuterCode3", "1")
				.param("stationCompanyTime", "5"))
				.andExpect(status().isOk())
				.andExpect(view().name("signup"))
				.andReturn();

		// mvに追加されたweathersの値を取得
		String message = (String) result.getModelAndView().getModel().get("message");

		//メッセージをテスト
		assertEquals(message, "未記入箇所があります");

	}

	@Test
	//JUnitは標準でロールバックしてくれる
	@Transactional
	void 新規登録処理でパスワードが空なことを検知できるかどうか() throws Exception {
		MvcResult result = mockMvc.perform(post("/signup")
				.param("email", "zzz@gmail.com")
				.param("password", "")
				.param("leaveHomeTime", "07:55")
				.param("homeStationTime", "20")
				.param("commuterCode1", "12")
				.param("commuterCode2", "11")
				.param("commuterCode3", "1")
				.param("stationCompanyTime", "5"))
				.andExpect(status().isOk())
				.andExpect(view().name("signup"))
				.andReturn();

		// mvに追加されたweathersの値を取得
		String message = (String) result.getModelAndView().getModel().get("message");

		//メッセージをテスト
		assertEquals(message, "未記入箇所があります");

	}

	@Test
	//JUnitは標準でロールバックしてくれる
	@Transactional
	void 新規登録処理でメールアドレスの重複を検知できるかどうか() throws Exception {
		MvcResult result = mockMvc.perform(post("/signup")
				.param("email", "a@gmail.com")
				.param("password", "f")
				.param("leaveHomeTime", "07:55")
				.param("homeStationTime", "20")
				.param("commuterCode1", "12")
				.param("commuterCode2", "11")
				.param("commuterCode3", "1")
				.param("stationCompanyTime", "5"))
				.andExpect(status().isOk())
				.andExpect(view().name("signup"))
				.andReturn();

		// mvに追加されたweathersの値を取得
		String message = (String) result.getModelAndView().getModel().get("message");

		//メッセージをテスト
		assertEquals(message, "お使いのメールアドレスは既に登録されています。");

	}

}
