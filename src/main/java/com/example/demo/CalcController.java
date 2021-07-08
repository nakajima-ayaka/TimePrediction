package com.example.demo;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CalcController {

	//他クラスとの繋がりを宣言する記述が大幅に減るアノテーション
	@Autowired
	//セッションスコープを使えるようにする
	HttpSession session;

	@Autowired
	//WeatherRepositoryを使えるようにする
	private WeatherRepository weatherRepository;

	//天候選択画面へ遷移
	@PostMapping("/weather")
	public ModelAndView weather(ModelAndView mv) {

		//天候一覧を取得
		mv.addObject("weathers", weatherRepository.findAll());

		//天候選択画面へ遷移を指定
		mv.setViewName("weather");

		return mv;
	}
}
