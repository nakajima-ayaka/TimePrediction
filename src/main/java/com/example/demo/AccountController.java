package com.example.demo;

import java.time.LocalTime;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AccountController {

	//他クラスとの繋がりを宣言する記述が大幅に減るアノテーション
	@Autowired
	//セッションスコープを使えるようにする
	HttpSession session;

	@Autowired
	//UserRepositoryを使えるようにする
	private UserRepository userRepository;

	@Autowired
	//WeatherRepositoryを使えるようにする
	private WeatherRepository weatherRepository;

	@Autowired
	//RailwayRepositoryを使えるようにする
	private RailwayRepository railwayRepository;

	//ログインまたはログアウト→ログイン
	@RequestMapping(value = { "/", "/login", "/logout", "/weather" })
	public String login() {

		//セッション情報はクリアする
		session.invalidate();

		//ログインへ遷移
		return "login";
	}

	//ログイン(入力済み)
	@PostMapping("/login")
	public ModelAndView login(
			@RequestParam("email") String email,
			@RequestParam("password") String password,
			ModelAndView mv) {

		//メールアドレスorパスワードが空の場合にエラーとする
		if (email == null || email.length() == 0 || password == null || password.length() == 0) {

			//表示のオブジェクト
			mv.addObject("message", "入力してください");

			//ログインへ遷移を指定
			mv.setViewName("login");

			return mv;
		}

		//メールアドレス検索結果からuserを取得
		User user = findEmail(email);

		//user情報が取得できているか及びパスワードが一致するか確認
		if (user != null && password.equals(user.getPassword())) {

			//会員情報をセッションスコープに設定
			session.setAttribute("user", user);

			int code1 = user.getCommuterCode1();
			int code2 = user.getCommuterCode2();
			int code3 = user.getCommuterCode3();

			String railwayName1 = findRailway(code1);
			String railwayName2 = findRailway(code2);
			String railwayName3 = findRailway(code3);

			//登録路線名をセッションスコープに設定
			session.setAttribute("railwayName1", railwayName1);
			session.setAttribute("railwayName2", railwayName2);
			session.setAttribute("railwayName3", railwayName3);

			//天候リストを表示
			mv.addObject("weathers", weatherRepository.findAll());

			//天候選択へ遷移を指定
			mv.setViewName("weather");

			return mv;

		}

		//不一致verのオブジェクトの生成
		mv.addObject("message", "メールアドレスもしくはパスワードが間違っています");

		//ログインへ遷移を指定
		mv.setViewName("login");

		return mv;

	}

	//メールアドレス検索
	public User findEmail(String email) {

		//メールアドレスでの検索結果を取得
		Optional<User> record = userRepository.findByEmail(email);

		//recordの有無の判定
		if (!record.isEmpty()) {
			return record.get();
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

	//新規登録
	@RequestMapping("/signup")
	public ModelAndView signup(ModelAndView mv) {

		//新規登録へ遷移
		mv.setViewName("signup");

		return mv;

	}

	//新規登録(入力済み)
	@PostMapping("/signup")
	public ModelAndView signup(
			@RequestParam("email") String email,
			@RequestParam("password") String password,
			@RequestParam("leaveHomeTime") LocalTime leaveHomeTime,
			@RequestParam(value = "homeStationTime", defaultValue = "0") int homeStationTime,
			@RequestParam(value = "commuterCode1", defaultValue = "1") int commuterCode1,
			@RequestParam(value = "commuterCode2", defaultValue = "1") int commuterCode2,
			@RequestParam(value = "commuterCode3", defaultValue = "1") int commuterCode3,
			@RequestParam(value = "stationCompanyTime", defaultValue = "0") int stationCompanyTime,
			ModelAndView mv) {

		//メールアドレス、パスワードがある場合エラーとする
		//使用路線の1つ目が選択されていない場合もエラーとする
		if (email.isEmpty() || email.length() == 0 ||
				password.isEmpty() || password.length() == 0 ||
				commuterCode1 == 1

		) {

			//表示のオブジェクト
			mv.addObject("message", "未記入箇所があります");

			//登録へ遷移を指定
			return signup(mv);
		}

		//パラメータからオブジェクトを生成
		User user = new User(
				email, password, leaveHomeTime, homeStationTime, commuterCode1,
				commuterCode2, commuterCode3, stationCompanyTime);

		//usersテーブルへの登録
		userRepository.saveAndFlush(user);

		//ログインへ遷移を指定
		mv.setViewName("login");

		return mv;

	}

}
