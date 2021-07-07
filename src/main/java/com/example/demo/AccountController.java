package com.example.demo;

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

	@RequestMapping("/")
	public String login() {

		// セッション情報はクリアする
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

		//メールアドレス検索結果からパスワードを取得
		findEmail(email);

		String pass = findEmail(email);

		//パスワードが一致するか確認
		if (pass.equals(password)) {

			// セッションスコープにuser情報を格納する
			session.setAttribute("user", userRepository.findAll());

			//天候選択へ遷移を指定
			mv.setViewName("weather");

			return mv;

		} else {
			//不一致verのオブジェクトの生成
			mv.addObject("message", "メールアドレスもしくはパスワードが間違っています");

			//ログインへ遷移を指定
			mv.setViewName("login");

			return mv;
		}
	}

	//メールアドレス検索
	public String findEmail(String email) {

		//Userの宣言
		User user;

		//メールアドレスでの検索結果を取得
		Optional<User> record = userRepository.findByEmail(email);

		//recordの有無の判定
		if (!record.isEmpty()) {
			user = record.get();
		} else {
			return null;
		}

		//情報取得
		String password = user.getPassword();

		//顧客情報をセッションスコープに設定
		//session.setAttribute("customerInfo", user);

		return password;
	}

	//		@RequestParam("leaveHomeTime") int leaveHomeTime,
	//		@RequestParam("commuterCode1") int commuterCode1,
	//		@RequestParam("commuterCode2") int commuterCode2,
	//		@RequestParam("commuterCode3") int commuterCode3,
	//		@RequestParam("stationCompanyTime") int stationCompanyTime,

}
