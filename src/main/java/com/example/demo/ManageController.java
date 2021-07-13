package com.example.demo;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ManageController {

	//他クラスとの繋がりを宣言する記述が大幅に減るアノテーション
	@Autowired
	//セッションスコープを使えるようにする
	HttpSession session;

	@Autowired
	//UserRepositoryを使えるようにする
	private UserRepository userRepository;

	@Autowired
	//RailwayRepositoryを使えるようにする
	private RailwayRepository railwayRepository;

	@Autowired
	//WeatherRepositoryを使えるようにする
	private WeatherRepository weatherRepository;

	@Autowired
	//DelayRepositoryを使えるようにする
	private DelayRepository delayRepository;

	//管理者画面に遷移
	@RequestMapping("/manage")
	public String manage() {

		//管理者画面へ遷移
		return "manage";
	}

	//ユーザ一覧の表示および削除画面
	@PostMapping("/users")
	public ModelAndView managingUser(ModelAndView mv) {

		//全ユーザ情報取得
		List<User> users = userRepository.findAll();

		// Thymeleafで表示する準備を行う
		mv.addObject("users", users);

		//ユーザ一覧へ遷移の指定
		mv.setViewName("users");
		return mv;
	}

	//天候一覧の表示および削除画面
	@PostMapping("/weathers")
	public ModelAndView managingWeather(ModelAndView mv) {

		//全天候情報の取得
		List<Weather> weathers = weatherRepository.findAll();

		// Thymeleafで表示する準備を行う
		mv.addObject("weathers", weathers);

		//天候一覧へ遷移の指定
		mv.setViewName("weathers");
		return mv;
	}

	//天候の削除
	@PostMapping("/weathers/{code}/delete")
	public ModelAndView deleteWeathers(
			ModelAndView mv,
			@PathVariable("code") int code) {
		//指定したコードの天候情報を削除
		Optional<Weather> record = weatherRepository.findById(code);

		if (!record.isEmpty()) {
			weatherRepository.deleteById(code);
		}
		return managingWeather(mv); // 天候情報一覧表示
	}

	//天候の新規登録
	@RequestMapping("/weather/new")
	public ModelAndView addWeather(ModelAndView mv) {

		//天候登録へ遷移指定
		mv.setViewName("addweather");

		return mv;
	}

	//天候の新規登録(入力済み)
	@PostMapping("/weather/newset")
	public ModelAndView addWeather(
			@RequestParam(name = "name") String name,
			ModelAndView mv) {

		//入力チェック
		if (name.equals("")) {

			//表示する内容を準備
			mv.addObject("message", "入力してください。");

			//遷移先の指定
			mv.setViewName("addWeather");

			return mv;

		} else {//全て入力済みなら↓

			///登録するエンティティのインスタンスを生成
			Weather weather = new Weather(name);

			//Weatherエンティティをweatherテーブルに登録
			weatherRepository.saveAndFlush(weather);

			//引数にこのメソッド結果を格納＆全件検索の呼び出し
			return managingWeather(mv);
		}
	}

	//鉄道の追加・削除画面表示
	@PostMapping(value = "/railways")
	public ModelAndView railway(ModelAndView mv) {

		//鉄道リストを取得
		List<Railway> railways = railwayRepository.findAll();

		//遅延頻度日本語化用Listを宣言
		List<String> delayFrequency = new ArrayList<String>();

		//遅延頻度日本語変換
		for (Railway railway : railways) {
			switch (railway.getDelayFrequency()) {
			case 1:
				delayFrequency.add("低い");
				break;
			case 2:
				delayFrequency.add("普通");
				break;
			case 3:
				delayFrequency.add("高い");
				break;
			}
		}

		//鉄道リストをmvに設定
		mv.addObject("railways", railwayRepository.findAll());

		//遅延頻度(日本語)をmvに設定
		mv.addObject("delayFrequencys", delayFrequency);

		//鉄道一覧表示画面へ遷移を指定
		mv.setViewName("railway");

		//ログインへ遷移
		return mv;
	}

	//鉄道追加処理(入力済み)
	@PostMapping("/railways/add")
	public ModelAndView railwayAdd(
			@RequestParam("railwayName") String railwayName,
			@RequestParam("delayFrequency") int delayFrequency,
			ModelAndView mv) {

		//鉄道名が空の場合にエラーとする
		if (railwayName == null || railwayName.length() == 0) {

			//表示のオブジェクト
			mv.addObject("message", "入力してください");

			return railway(mv);
		}

		//鉄道名の重複検知
		Railway railwayFindName = findByRecordRailWayName(railwayName);
		if (railwayFindName != null) {

			//表示のオブジェクト
			mv.addObject("message", railwayName + "は既に登録されています。");

			//一覧表示へ遷移を指定
			return railway(mv);
		}

		//追加用レコードを作成
		Railway railway = new Railway(railwayName, delayFrequency);

		//railwayテーブルへの登録
		railwayRepository.saveAndFlush(railway);

		return railway(mv);
	}

	//鉄道削除処理
	@PostMapping("/railways/delete")
	public ModelAndView railwayDelete(
			@RequestParam("code") int code,
			ModelAndView mv) {

		//鉄道番号で検索
		Optional<Railway> railway = railwayRepository.findById(code);

		//recordの有無の判定
		if (railway.isEmpty()) {

			//表示のオブジェクト
			mv.addObject("message", "削除対象の鉄道番号：" + code + "が見つかりません");

			return railway(mv);
		}

		//削除処理
		railwayRepository.deleteById(code);

		return railway(mv);
	}

	//鉄道名からレコードを取得
	public Railway findByRecordRailWayName(String name) {

		//鉄道名での検索結果を取得
		Optional<Railway> record = railwayRepository.findByName(name);

		//recordの有無の判定
		if (!record.isEmpty()) {
			return record.get();
		}

		//recordが無い場合はnullを返す
		return null;

	}

	//遅延情報の画面表示
	@PostMapping(value = "/delays")
	public ModelAndView delay(ModelAndView mv) {

		//遅延リストを取得
		List<Delay> delays = delayRepository.findAll();

		//天候日本語化用Listを宣言
		List<String> weatherElements = new ArrayList<String>();

		//鉄道日本語化用Listを宣言
		List<String> railwayNames = new ArrayList<String>();

		//天候と鉄道名日本語変換
		for (Delay delay : delays) {

			//天候の日本語化
			String element = findWeatherElement(delay.getWeatherCode());
			weatherElements.add(element);

			//鉄道の日本語化
			Optional<Railway> record = railwayRepository.findById(delay.getRailwayCode());
			Railway railway = record.get();
			railwayNames.add(railway.getName());
		}

		//遅延リストをmvに設定
		mv.addObject("delays", delayRepository.findAll());

		//鉄道リストをmvに設定
		mv.addObject("railways", railwayRepository.findAll());

		//天候リストをmvに設定
		mv.addObject("weathers", weatherRepository.findAll());

		//天候(日本語)をmvに設定
		mv.addObject("weatherElements", weatherElements);

		//鉄道名(日本語)をmvに設定
		mv.addObject("railwayNames", railwayNames);

		//鉄道一覧表示画面へ遷移を指定
		mv.setViewName("delay");

		//ログインへ遷移
		return mv;

	}

	//遅延情報追加処理(入力済み)
	@PostMapping("/delays/add")
	public ModelAndView delayAdd(
			@RequestParam("weatherCode") int weatherCode,
			@RequestParam("commuterCode") int commuterCode,
			@RequestParam(value = "delayTime", defaultValue = "0") int delayTime,
			@RequestParam("delayDate") String delayDate,
			ModelAndView mv) {

		//遅延時間が0の場合にエラーとする
		if (delayTime == 0) {

			//表示のオブジェクト
			mv.addObject("message", "遅延時間を入力してください");

			return delay(mv);
		}

		//日付を変換
		Date date = Date.valueOf(delayDate);

		//追加用レコードを作成
		Delay delay = new Delay(weatherCode, commuterCode, delayTime, date);

		//delayテーブルへの登録
		delayRepository.saveAndFlush(delay);

		return delay(mv);
	}

	//遅延情報削除処理
	@PostMapping("/delays/delete")
	public ModelAndView delayDelete(
			@RequestParam("code") int code,
			ModelAndView mv) {

		//遅延番号で検索
		Optional<Delay> delay = delayRepository.findById(code);

		//recordの有無の判定
		if (delay.isEmpty()) {

			//表示のオブジェクト
			mv.addObject("message", "削除対象の遅延番号：" + code + "が見つかりません");

			return delay(mv);
		}

		//削除処理
		delayRepository.deleteById(code);

		return delay(mv);
	}

	//天候検索
	public String findWeatherElement(int code) {

		//天候番号での検索結果を取得
		Optional<Weather> record = weatherRepository.findById(code);

		//天候を取得
		if (!record.isEmpty()) {
			Weather weather = record.get();
			return weather.getElement();
		}

		//recordが無い場合はnullを返す
		return null;

	}

	//登録路線名の検索
	public String findRailway(int code) {

		//Railway宣言
		Railway railway;
		//通勤経路での検索結果を取得
		Optional<Railway> record = railwayRepository.findById(code);

		//recordの有無の判定
		if (!record.isEmpty()) {
			railway = record.get();
			return railway.getName();

		} else {

			//recordが無い場合はnullを返す
			return null;
		}
	}

	//ユーザの削除
	@PostMapping("/users/{code}/delete")
	public ModelAndView deleteUser(
			ModelAndView mv,
			@PathVariable("code") int code) {
		//指定したコードのユーザ情報を削除
		Optional<User> record = userRepository.findById(code);

		if (!record.isEmpty()) {
			userRepository.deleteById(code);
		}
		return managingUser(mv); // ユーザ情報一覧表示
	}

}
