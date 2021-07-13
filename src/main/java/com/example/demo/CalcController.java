package com.example.demo;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	@Autowired
	//DelayRepositoryを使えるようにする
	private DelayRepository delayRepository;

	@Autowired
	//RailwayRepositoryを使えるようにする
	private RailwayRepository railwayRepository;

	@Autowired
	//UserRepositoryを使えるようにする
	private UserRepository userRepository;

	//天候選択画面へ遷移
	@PostMapping("/weather")
	public ModelAndView weather(ModelAndView mv) {

		//天候一覧を取得
		mv.addObject("weathers", weatherRepository.findAll());

		//天候選択画面へ遷移を指定
		mv.setViewName("weather");

		return mv;
	}

	//結果表示画面へ遷移
	@PostMapping("/weather/result")
	public ModelAndView weather(
			@RequestParam("weatherCode") int weatherCode,
			ModelAndView mv) {

		//エンティティクラスのインスタンス生成
		Delay delay = new Delay();
		Railway railway = new Railway();

		//セッションからUserを取得
		User user = (User) session.getAttribute("user");

		//1.天候コードと通勤経路1を、「検索して遅延平均時間を計算するメソッド」へ引数として渡し、その結果を変数に格納する。
		List<Delay> list = delayRepository.findByWeatherCodeAndRailwayCode(weatherCode, user.getCommuterCode1());
		int FirstAverage = delay.average(list);

		//2.天候コードと通勤経路2を、「検索して遅延平均時間を計算するメソッド」へ引数として渡し、その結果を変数に格納する。
		list = delayRepository.findByWeatherCodeAndRailwayCode(weatherCode, user.getCommuterCode2());
		int SecondAverage = delay.average(list);

		//3.天候コードと通勤経路3を、「検索して遅延平均時間を計算するメソッド」へ引数として渡し、その結果を変数に格納する。
		list = delayRepository.findByWeatherCodeAndRailwayCode(weatherCode, user.getCommuterCode3());
		int ThirdAverage = delay.average(list);

		//4.上の1,2,3で取得した変数を、「3つの中から最大値取得処理を実行するメソッド」へ引数として渡し、その結果を変数に格納する。
		int max = delay.max(FirstAverage, SecondAverage, ThirdAverage);

		//5.通勤経路1,2,3を、「railwayテーブルから遅延頻度を検索して、結果の文面を決定するメソッド」へ引数として渡し、その結果を変数に格納する。
		Optional<Railway> record1 = railwayRepository.findById(user.getCommuterCode1());
		Optional<Railway> record2 = railwayRepository.findById(user.getCommuterCode2());
		Optional<Railway> record3 = railwayRepository.findById(user.getCommuterCode3());

		List<String> railwayName = new ArrayList<String>();
		//リストへの追加
		railwayName.add(railway.DelayFrequency(record1));
		railwayName.add(railway.DelayFrequency(record2));
		railwayName.add(railway.DelayFrequency(record3));

		String message = Message(railwayName);
		mv.addObject("message", message);

		//6.天候コード、user情報のhome_station_time及びstation_company_timeを、「徒歩遅延時間を算出処理するメソッド」へ引数として渡し、その結果を変数に格納する。
		int delayWalkTime = railway.DelayWalk(user.getHomeStationTime(), user.getStationCompanyTime(), weatherCode);

		//7.4,6で取得した変数を、「登録した出社時間から4,6合計値を引いて、目安の出社時間を算出するメソッド」へ引数として渡し、その結果を変数に格納する。
		String result = Result(user.getLeaveHomeTime(), max, delayWalkTime);
		mv.addObject("result", result);

		//8./weatherを呼び出す
		return weather(mv);
	}

	//遅延頻度の文章処理
	public String Message(List<String> railwayName) {
		String message = null;

		//リストのあるnullの削除
		railwayName.removeAll(Collections.singleton(null));

		//リストが0(すべてnull)の場合の処理
		if (railwayName.size() == 0) {
			message = "遅延の可能性が高い路線はありません。";
			return message;
		}

		message = String.join("と", railwayName);

		return message + "は遅延しやすいので注意！";
	}

	//出社時間の計算処理と文章処理
	public String Result(Time time, int max, int delayWalkTime) {
		String result = null;

		//合計遅延時間の算出
		int delayTotal = max + delayWalkTime;

		//LocalTimeへ変換
		LocalTime goingTime = time.toLocalTime();

		//時刻の減算処理
		goingTime = goingTime.minusMinutes(delayTotal);

		result = goingTime + "に家を出ると間に合います！";

		return result;
	}

}
