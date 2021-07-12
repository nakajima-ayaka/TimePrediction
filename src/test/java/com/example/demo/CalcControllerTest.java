package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootTest //repositoryなど他のクラスを使用する際に使うテスト用アノテーション
@EnableWebMvc //WebアプリケーションのMVCモデルをテストで使用可能にするアノテーション

class CalcControllerTest {

	private MockMvc mockMvc;

	@Autowired
	WebApplicationContext webApplicationContext;

	@Autowired
	CalcController calcController;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void 天候一覧を取得_天候選択画面へ遷移できるかどうか() throws Exception {
		String[] elements = { "小雨", "雨", "大雨" };

		User user = new User(
				1, "test@gmail.com", "test", Time.valueOf("07:30:00"), 10, 1, 2, 3, 10);

		MvcResult result = mockMvc.perform(
				post("/weather")
						.sessionAttr("user", user)
						.sessionAttr("railwayName1", "京王線")
						.sessionAttr("railwayName2", "JR山手線")
						.sessionAttr("railwayName3", "未登録"))
				.andExpect(status().isOk())
				.andExpect(view().name("weather"))
				.andReturn();

		// mvに追加されたweathersの値を取得
		List<Weather> weathers = (List<Weather>) result.getModelAndView().getModel().get("weathers");

		//weathersが取得できているかをテスト
		//weathers分繰り返す
		for (int i = 0; i < weathers.size(); i++) {
			//weathersからweatherを取得
			Weather weather = weathers.get(i);
			//codeを比較
			assertEquals(weather.getCode(), i + 1);
			//elementを比較
			assertEquals(weather.getElement(), elements[i]);
		}
	}

	@Test
	void 遅延頻度の文章が表示されるかどうか() throws Exception {
		//登録路線のリストを作成
		List<String> railwayName = new ArrayList<>();

		//リストに項目を設定
		railwayName.add(null);
		railwayName.add("小田急線");
		railwayName.add("JR埼京線");

		//上のリストをcalcController内のMessage(List<String> railwayName)で実行し、結果をmessage変数に格納
		String message = calcController.Message(railwayName);

		//message変数の内容が一致するか
		assertEquals(message, "小田急線とJR埼京線は遅延しやすいので注意！");
	}

	@Test
	void 出社時間の文章が表示されるかどうか() throws Exception {

		//値の設定
		Time time = Time.valueOf("07:30:00");
		int max = 10;
		int delayWalkTime = 4;

		//上のリストをcalcController内のResult(Time time, int max, int delayWalkTime)で実行し、結果をresult変数に格納
		String result = calcController.Result(time, max, delayWalkTime);

		//result変数の内容が一致するか
		assertEquals(result, "07:16に家を出ると間に合います！");

	}
}
