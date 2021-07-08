package com.example.demo;

import java.sql.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//このクラスがJPAのエンティティ・クラスである意味
@Entity
//エンティティに対応するテーブル名を指定※クラス名がテーブル名と同じでない時に使用
@Table(name = "delay")
public class Delay {

	//フィールド
	@Id //主キーの指定
	//↓シリアルで設定されているコードについてつける＠
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int code; //遅延情報番号

	@Column(name = "weather_code")
	private int weatherCode; //天候番号

	@Column(name = "railway_code")
	private int railwayCode; //鉄道番号

	@Column(name = "delay_time")
	private int delayTime; //遅延時間(分)

	private Date date; //遅延が発生した日付

	//コンストラクタ(引数なし)
	public Delay() {

	}

	//コンストラクタ(全部入り)
	public Delay(int code, int weatherCode, int railwayCode, int delayTime, Date date) {
		this.code = code;
		this.weatherCode = weatherCode;
		this.railwayCode = railwayCode;
		this.delayTime = delayTime;
		this.date = date;
	}

	//登録の時はこのコンストラクタ(DBがシリアルだから)
	public Delay(int weatherCode, int railwayCode, int delayTime, Date date) {
		this.weatherCode = weatherCode;
		this.railwayCode = railwayCode;
		this.delayTime = delayTime;
		this.date = date;
	}

	//アクセッサ・メソッド(セッタ＆ゲッタ)
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getWeatherCode() {
		return weatherCode;
	}

	public void setWeatherCode(int weatherCode) {
		this.weatherCode = weatherCode;
	}

	public int getRailwayCode() {
		return railwayCode;
	}

	public void setRailwayCode(int railwayCode) {
		this.railwayCode = railwayCode;
	}

	public int getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	//遅延平均時間取得処理
	public int average(List<Delay> list) {

		//変数を宣言
		int total = 0;

		//遅延時間の合計を算出
		for (Delay delay : list) {
			//totalに該当の遅延時間を合算
			total += delay.delayTime;
		}

		//平均時間(分)を返す
		return total / list.size();
	}

	//遅延最大時間取得処理
	public int max(int delayTime1, int delayTime2, int delayTime3) {

		//最大値を返す
		return Math.max(delayTime1, Math.max(delayTime2, delayTime3));

	}

}
