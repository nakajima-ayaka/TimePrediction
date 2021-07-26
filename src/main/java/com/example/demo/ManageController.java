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
	@PostMapping("/manage")
	public ModelAndView manage(ModelAndView mv) {

		//管理者画面へ遷移
		mv.setViewName("manage");

		return mv;
	}

	//ユーザ一覧の表示および削除画面
	@PostMapping("/users")
	public ModelAndView user(ModelAndView mv) {

		//全ユーザ情報リスト取得
		List<User> users = userRepository.findAll();

		//鉄道日本語化用Listを宣言
		List<String> railwayNames1 = new ArrayList<String>();
		List<String> railwayNames2 = new ArrayList<String>();
		List<String> railwayNames3 = new ArrayList<String>();

		//鉄道名日本語変換
		for (User user : users) {

			//鉄道の日本語化
			Optional<Railway> record1 = railwayRepository.findById(user.getCommuterCode1());
			Optional<Railway> record2 = railwayRepository.findById(user.getCommuterCode2());
			Optional<Railway> record3 = railwayRepository.findById(user.getCommuterCode3());

			Railway railway1 = record1.get();
			Railway railway2 = record2.get();
			Railway railway3 = record3.get();

			railwayNames1.add(railway1.getName());
			railwayNames2.add(railway2.getName());
			railwayNames3.add(railway3.getName());

		}

		//ユーザ情報リストをmvに設定
		mv.addObject("users", userRepository.findAll());

		//鉄道名(日本語)をmvに設定
		mv.addObject("railwayNames1", railwayNames1);
		mv.addObject("railwayNames2", railwayNames2);
		mv.addObject("railwayNames3", railwayNames3);

		//ユーザ一覧へ遷移の指定
		mv.setViewName("users");
		return mv;
	}

	//ユーザの削除
	@PostMapping("/users/{code}/delete")
	public ModelAndView userDelete(
			ModelAndView mv,
			@PathVariable("code") int code) {
		//指定したコードのユーザ情報を削除
		Optional<User> record = userRepository.findById(code);

		if (!record.isEmpty()) {
			userRepository.deleteById(code);
		}
		return user(mv); // ユーザ情報一覧表示
	}

	//天候の追加・削除画面
	@PostMapping("/weathers")
	public ModelAndView weather(ModelAndView mv) {

		//天候リストの取得
		List<Weather> weathers = weatherRepository.findAll();

		// Thymeleafで表示する準備を行う
		mv.addObject("weathers", weathers);

		//遅延係数が設定されているか確認
		if (mv.getModel().get("coefficient") == null) {
			//遅延係数の初期値を設定
			mv.addObject("coefficient", "0.1");
		}

		//天候一覧へ遷移の指定
		mv.setViewName("weathers");
		return mv;
	}

	//天候の削除
	@PostMapping("/weathers/{code}/delete")
	public ModelAndView weatherDelete(
			ModelAndView mv,
			@PathVariable("code") int code) {

		//指定したコードの天候情報を取得
		Optional<Weather> record = weatherRepository.findById(code);

		//削除対象無しの場合
		if (record.isEmpty()) {
			//表示する内容を準備
			mv.addObject("message", "削除対象がありません。");
			// 天候情報一覧表示
			return weather(mv);
		}

		//該当の天候情報を取得
		Weather weather = record.get();

		//該当の天候が登録されている天候情報を取得
		List<Delay> delays = delayRepository.findByWeatherCode(code);
		//
		if (delays.size() != 0) {
			//表示する内容を準備
			mv.addObject("message", weather.getElement() + "は他のテーブルで使用されています。");
			// 天候情報一覧表示
			return weather(mv);
		}

		//削除処理
		weatherRepository.deleteById(code);

		return weather(mv); // 天候情報一覧表示
	}

	//天候追加処理(入力済み)
	@PostMapping("/weathers/add")
	public ModelAndView weatherAdd(
			@RequestParam("element") String element,
			@RequestParam(value = "coefficient", defaultValue = "3") double coefficient,
			ModelAndView mv) {

		//入力チェック
		if (element.equals("") || coefficient > 2) {

			//表示する内容を準備
			mv.addObject("message", "入力してください");

			//遷移先の指定
			return weather(mv);
		}

		//天候名の重複検知
		Weather weatheFindName = findByRecordWeatherName(element);
		if (weatheFindName != null) {

			//表示のオブジェクト
			mv.addObject("message", element + "は既に登録されています。");

			//一覧表示へ遷移を指定
			return weather(mv);
		}

		//追加用レコードを作成
		Weather weather = new Weather(element, coefficient);

		//railwayテーブルへの登録
		weatherRepository.saveAndFlush(weather);

		return weather(mv);

	}

	//天候名からレコードを取得
	public Weather findByRecordWeatherName(String element) {

		//天候名での検索結果を取得
		Optional<Weather> record = weatherRepository.findByElement(element);

		//recordの有無の判定
		if (!record.isEmpty()) {
			return record.get();
		}

		//recordが無い場合はnullを返す
		return null;

	}

	//天候更新処理
	@PostMapping("/weathers/{code}/update")
	public ModelAndView weatherUpdate(
			@RequestParam(name = "code") int code,
			@RequestParam(name = "element") String element,
			@RequestParam(name = "coefficient") double coefficient,
			ModelAndView mv) {

		//選択コードの内容表示
		mv.addObject("code", code);
		mv.addObject("element", element);
		mv.addObject("coefficient", coefficient);

		return weather(mv);
	}

	//天候更新処理(入力済み)
	@PostMapping("/weathers/{code}/edit")
	public ModelAndView weatherEdit(
			@RequestParam(name = "code", defaultValue = "0") int code,
			@RequestParam(name = "element") String element,
			@RequestParam(name = "coefficient", defaultValue = "3") double coefficient,
			ModelAndView mv) {

		//未選択チェック
		if (code == 0) {

			//表示する内容を準備
			mv.addObject("result", "一覧から選択してください");

			//選択コードの再内容表示
			mv.addObject("element", element);
			mv.addObject("coefficient", coefficient);

			return weather(mv);

		}

		//入力チェック
		if (element.equals("") || coefficient > 2) {

			//表示する内容を準備
			mv.addObject("result", "入力してください");

			//選択コードの再内容表示
			mv.addObject("code", code);
			mv.addObject("element", element);
			mv.addObject("coefficient", coefficient);

			return weather(mv);

		}

		//天候名の重複検知
		Weather weatheFindName = findByRecordWeatherName(element);
		if (weatheFindName == null) {
			//変更するエンティティのインスタンスを生成
			Weather weather = new Weather(code, element, coefficient);

			//Weatherエンティティをusersテーブルに登録(更新)
			weatherRepository.saveAndFlush(weather);

			//引数にこのメソッド結果を格納＆全件検索の呼び出し
			return weather(mv);

		}

		//天候番号が一致するか確認
		//		Weather weatheFindCode = findByRecordWeatherCode(code);

		if (weatheFindName.getCode() != code) {

			//表示のオブジェクト
			mv.addObject("result", "【" + element + "】は既に登録されています。");

			//一覧表示へ遷移を指定
			return weather(mv);
		}

		//変更するエンティティのインスタンスを生成
		Weather weather = new Weather(code, element, coefficient);

		//Weatherエンティティをusersテーブルに登録(更新)
		weatherRepository.saveAndFlush(weather);

		//引数にこのメソッド結果を格納＆全件検索の呼び出し
		return weather(mv);

	}

	//天候番号からレコードを取得
	public Weather findByRecordWeatherCode(int code) {

		//天候名での検索結果を取得
		Optional<Weather> record = weatherRepository.findByCode(code);

		//recordの有無の判定
		if (!record.isEmpty()) {
			return record.get();
		}

		//recordが無い場合はnullを返す
		return null;

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
		Optional<Railway> record = railwayRepository.findById(code);

		//recordの有無の判定
		if (record.isEmpty()) {

			//表示のオブジェクト
			mv.addObject("message", "削除対象の鉄道番号：" + code + "が見つかりません");

			return railway(mv);
		}

		//該当の鉄道情報を取得
		Railway railway = record.get();

		//該当の鉄道が登録されている遅延情報を取得
		List<Delay> delays = delayRepository.findByRailwayCode(code);
		//
		if (delays.size() != 0) {
			//表示する内容を準備
			mv.addObject("message", railway.getName() + "は他のテーブルで使用されています。");
			// 天候情報一覧表示
			return railway(mv);
		}

		//該当の鉄道が登録されているユーザ情報を取得
		List<User> users = userRepository.findByCommuterCode1OrCommuterCode2OrCommuterCode3(code, code, code);
		//
		if (users.size() != 0) {
			//表示する内容を準備
			mv.addObject("message", railway.getName() + "は他のテーブルで使用されています。");
			// 天候情報一覧表示
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

	//鉄道更新処理
	@PostMapping("/railways/{code}/update")
	public ModelAndView railwayUpdate(
			@RequestParam(name = "code") int code,
			@RequestParam(name = "name") String name,
			@RequestParam(name = "delayFrequencys") String delayFrequencys,
			ModelAndView mv) {

		//選択コードの内容表示
		mv.addObject("code", code);
		mv.addObject("name", name);

		Optional<Railway> record1 = railwayRepository.findById(code);
		Railway railway1 = record1.get();
		Integer delaynum = railway1.getDelayFrequency();

		mv.addObject("delay", delaynum);

		return railway(mv);
	}

	//鉄道更新処理(入力済み)
	@PostMapping("/railways/{code}/edit")
	public ModelAndView railwayEdit(
			@RequestParam(name = "code", defaultValue = "0") int code,
			@RequestParam(name = "name") String name,
			@RequestParam(name = "delayFrequencys") int delayFrequencys,
			ModelAndView mv) {

		//未選択チェック
		if (code == 0) {

			//表示する内容を準備
			mv.addObject("result", "一覧から選択してください");

			//選択コードの再内容表示
			mv.addObject("name", name);
			mv.addObject("delayFrequencys", delayFrequencys);

			return railway(mv);

		}

		//入力チェック
		if (name.equals("")) {

			//表示する内容を準備
			mv.addObject("result", "入力してください");

			//選択コードの再内容表示
			mv.addObject("code", code);
			mv.addObject("name", name);
			mv.addObject("delayFrequencys", delayFrequencys);

			return railway(mv);
		}

		//鉄道名の重複検知
		Railway railwayFindName = findByRecordRailWayName(name);
		if (railwayFindName == null) {
			//変更するエンティティのインスタンスを生成
			Railway railway = new Railway(code, name, delayFrequencys);

			//railwayエンティティをusersテーブルに登録(更新)
			railwayRepository.saveAndFlush(railway);

			//引数にこのメソッド結果を格納＆全件検索の呼び出し
			return railway(mv);

		}

		//鉄道番号が一致するか確認
		if (railwayFindName.getCode() != code) {

			//表示のオブジェクト
			mv.addObject("result", "【" + name + "】は既に登録されています。");

			//一覧表示へ遷移を指定
			return railway(mv);
		}

		//変更するエンティティのインスタンスを生成
		Railway railway = new Railway(code, name, delayFrequencys);

		//Weatherエンティティをusersテーブルに登録(更新)
		railwayRepository.saveAndFlush(railway);

		//引数にこのメソッド結果を格納＆全件検索の呼び出し
		return railway(mv);
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

		//遅延係数が設定されているか確認
		if (mv.getModel().get("delayTime") == null) {
			//遅延係数の初期値を設定
			mv.addObject("delayTime", "0");
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

	//遅延情報更新処理
	@PostMapping("/delays/{code}/update")
	public ModelAndView delayUpdate(
			@RequestParam(name = "code") int code,
			@RequestParam("weatherElement") String weatherElement,
			@RequestParam("railwayName") String railwayName,
			@RequestParam(value = "delayTime") int delayTime,
			@RequestParam("date") String delayDate,
			ModelAndView mv) {

		//選択コードの内容表示
		mv.addObject("code", code);
		mv.addObject("weatherElement", weatherElement);
		mv.addObject("railwayName", railwayName);
		mv.addObject("delayTime", delayTime);
		mv.addObject("delayDate", delayDate);

		return delay(mv);
	}

	//遅延情報更新処理(入力済み)
	@PostMapping("/delays/{code}/edit")
	public ModelAndView delayEdit(
			@RequestParam(name = "code", defaultValue = "0") int code,
			@RequestParam("weatherElement") int weatherElement,
			@RequestParam("railwayName") int railwayName,
			@RequestParam(value = "delayTime", defaultValue = "0") int delayTime,
			@RequestParam("delayDate") String delayDate,
			ModelAndView mv) {

		//未選択チェック
		if (code == 0) {

			//表示する内容を準備
			mv.addObject("result", "一覧から選択してください");

			return delay(mv);

		}

		//遅延時間が0の場合にエラーとする
		if (delayTime == 0) {

			//表示する内容を準備
			mv.addObject("result", "遅延時間を入力してください");

			Optional<Delay> record = delayRepository.findById(code);
			Delay delay = record.get();
			Integer weatherNum = delay.getWeatherCode();
			Integer railwayNum = delay.getRailwayCode();

			Optional<Weather> weather = weatherRepository.findById(weatherNum);
			Optional<Railway> railway = railwayRepository.findById(railwayNum);

			String weatherCodeName = weather.get().getElement();
			String railwayCodeName = railway.get().getName();

			//選択コードの再内容表示
			mv.addObject("code", code);
			mv.addObject("delayTime", delayTime);
			mv.addObject("delayDate", delayDate);

			mv.addObject("weatherElement", weatherCodeName);
			mv.addObject("railwayName", railwayCodeName);

			return delay(mv);
		}

		//日付を変換
		Date date = Date.valueOf(delayDate);

		//登録情報の検索
		Delay delayRecord = findByRecordDelay(weatherElement, railwayName, delayTime, date);

		//登録情報の重複検知(遅延番号以外が完全一致の場合はエラーとする)
		if (delayRecord != null) {

			//表示のオブジェクト
			mv.addObject("result", "既に登録されています。");

			Optional<Delay> record = delayRepository.findById(code);
			Delay delay = record.get();
			Integer weatherNum = delay.getWeatherCode();
			Integer railwayNum = delay.getRailwayCode();

			Optional<Weather> weather = weatherRepository.findById(weatherNum);
			Optional<Railway> railway = railwayRepository.findById(railwayNum);

			String weatherCodeName = weather.get().getElement();
			String railwayCodeName = railway.get().getName();

			//選択コードの再内容表示
			mv.addObject("code", code);
			mv.addObject("delayTime", delayTime);
			mv.addObject("delayDate", delayDate);

			mv.addObject("weatherElement", weatherCodeName);
			mv.addObject("railwayName", railwayCodeName);


			//一覧表示へ遷移を指定
			return delay(mv);
		}

		//変更するエンティティのインスタンスを生成
		Delay delay = new Delay(code, weatherElement, railwayName, delayTime, date);

		//Delayエンティティをusersテーブルに登録(更新)
		delayRepository.saveAndFlush(delay);

		//引数にこのメソッド結果を格納＆全件検索の呼び出し
		return delay(mv);

	}

	//遅延情報の検索
	public Delay findByRecordDelay(int weatherCode, int railwayCode, int delayTime, Date date) {

		//天候番号・鉄道番号・遅延時間・遅延日付での検索結果を取得
		Optional<Delay> record = delayRepository.findByWeatherCodeAndRailwayCodeAndDelayTimeAndDate(weatherCode,
				railwayCode, delayTime, date);

		//recordの有無の判定
		if (!record.isEmpty()) {
			return record.get();

		} else {

			//recordが無い場合はnullを返す
			return null;
		}
	}

}