package com.example.demo;

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
	public  ModelAndView managingWeather(ModelAndView mv) {

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
		if (name.equals("")){

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
	@PostMapping("/railways")
	public String managingRailway() {

		//管理者画面へ遷移
		return "manage";
	}

	//遅延情報の追加・削除画面表示
	@PostMapping("/delays")
	public String managingDelay() {

		//管理者画面へ遷移
		return "manage";
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
