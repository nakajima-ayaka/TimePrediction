package com.example.demo;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//このクラスがJPAのエンティティ・クラスである意味
@Entity
//エンティティに対応するテーブル名を指定※クラス名がテーブル名と同じでない時に使用
@Table(name = "users")
public class User {

	//フィールド
	@Id //主キーの指定
	//↓シリアルで設定されているコードについてつける＠
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int code; //会員番号
	private String email; //メールアドレス
	private String password; //パスワード

	@Column(name = "leave_home_time")
	private LocalTime leaveHomeTime; //いつも家を出る時間

	@Column(name = "home_station_time")
	private int homeStationTime; //自宅から最寄り駅までの所要時間(分)

	@Column(name = "commuter_code1")
	private int commuterCode1; //通勤経路１つめ

	@Column(name = "commuter_code2")
	private int commuterCode2; //通勤経路２つめ

	@Column(name = "commuter_code3")
	private int commuterCode3; //通勤経路３つめ

	@Column(name = "station_company_time")
	private int stationCompanyTime; //駅から会社までの所要時間(分)

	//コンストラクタ(引数なし)
	public User() {

	}

	//コンストラクタ(全部入り)
	public User(int code, String email, String password, LocalTime leaveHomeTime, int homeStationTime,
			int commuterCode1, int commuterCode2, int commuterCode3, int stationCompanyTime) {
		this.code = code;
		this.email = email;
		this.password = password;
		this.leaveHomeTime = leaveHomeTime;
		this.homeStationTime = homeStationTime;
		this.commuterCode1 = commuterCode1;
		this.commuterCode2 = commuterCode2;
		this.commuterCode3 = commuterCode3;
		this.stationCompanyTime = stationCompanyTime;
	}

	//登録の時はこのコンストラクタ(DBがシリアルだから)
	public User(String email, String password, LocalTime leaveHomeTime, int homeStationTime,
			int commuterCode1, int commuterCode2, int commuterCode3, int stationCompanyTime) {
		this.email = email;
		this.password = password;
		this.leaveHomeTime = leaveHomeTime;
		this.homeStationTime = homeStationTime;
		this.commuterCode1 = commuterCode1;
		this.commuterCode2 = commuterCode2;
		this.commuterCode3 = commuterCode3;
		this.stationCompanyTime = stationCompanyTime;
	}

	//アクセッサ・メソッド(セッタ＆ゲッタ)
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LocalTime getLeaveHomeTime() {
		return leaveHomeTime;
	}

	public void setLeaveHomeTime(LocalTime leaveHomeTime) {
		this.leaveHomeTime = leaveHomeTime;
	}

	public int getHomeStationTime() {
		return homeStationTime;
	}

	public void setHomeStationTime(int homeStationTime) {
		this.homeStationTime = homeStationTime;
	}

	public int getCommuterCode1() {
		return commuterCode1;
	}

	public void setCommuterCode1(int commuterCode1) {
		this.commuterCode1 = commuterCode1;
	}

	public int getCommuterCode2() {
		return commuterCode2;
	}

	public void setCommuterCode2(int commuterCode2) {
		this.commuterCode2 = commuterCode2;
	}

	public int getCommuterCode3() {
		return commuterCode3;
	}

	public void setCommuterCode3(int commuterCode3) {
		this.commuterCode3 = commuterCode3;
	}

	public int getStationCompanyTime() {
		return stationCompanyTime;
	}

	public void setStationCompanyTime(int stationCompanyTime) {
		this.stationCompanyTime = stationCompanyTime;
	}

}
