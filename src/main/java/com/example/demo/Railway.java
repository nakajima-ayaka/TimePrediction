package com.example.demo;

import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//このクラスがJPAのエンティティ・クラスである意味
@Entity
//エンティティに対応するテーブル名を指定※クラス名がテーブル名と同じでない時に使用
@Table(name = "railway")
public class Railway {

	//フィールド
	@Id //主キーの指定
	//↓シリアルで設定されているコードについてつける＠
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int code; //鉄道番号
	private String name; //鉄道名
	@Column(name = "delay_frequency")
	private int delayFrequency; //遅延頻度

	//コンストラクタ(引数なし)
	public Railway() {

	}

	//コンストラクタ(全部入り)
	public Railway(int code, String name, int delayFrequency) {
		this.code = code;
		this.name = name;
		this.delayFrequency = delayFrequency;
	}

	//アクセッサ・メソッド(ゲッタ)
	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public int getDelayFrequency() {
		return delayFrequency;
	}

	//遅延頻度取得処理
	public String DelayFrequency(Optional<Railway> record) {

		//recordの有無の判定
		if (record.isEmpty()) {
			return null;
		}

		//一件のrailwayの情報取得
		Railway railway = record.get();

		//遅延頻度の取得
		int delay = railway.getDelayFrequency();

		//3以外はnullで返す分岐
		if (delay != 3) {
			return null;

		}
		//3の場合の処理

		return railway.getName();

	}

	//徒歩時間計算処理
	public int DelayWalk(int homeStationTime, int stationCompanyTime, int weatherCode) {

		//係数の宣言
		double coefficient = 0;

		//天気による分岐で係数を決定
		switch (weatherCode) {
		case 1:
			coefficient = 0.1;
			break;
		case 2:
			coefficient = 0.2;
			break;
		case 3:
			coefficient = 0.4;
			break;
		}

		//徒歩遅延時間の算出
		int delayWalkTime = (int) (homeStationTime * coefficient + stationCompanyTime * coefficient);

		return delayWalkTime;

	}

}
