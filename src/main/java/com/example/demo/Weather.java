package com.example.demo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//このクラスがJPAのエンティティ・クラスである意味
@Entity
//エンティティに対応するテーブル名を指定※クラス名がテーブル名と同じでない時に使用
@Table(name = "weather")
public class Weather {

	//フィールド
	@Id //主キーの指定
	//↓シリアルで設定されているコードについてつける＠
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int code; //天候番号
	private String element; //天候
	private double coefficient; //遅延係数

	//コンストラクタ(引数なし)
	public Weather() {
	}

	//コンストラクタ(引数あり)
	public Weather(String element, double coefficient) {
		this.element = element;
		this.coefficient = coefficient;
	}

	//コンストラクタ(全部入り)
	public Weather(int code, String element, double coefficient) {
		this.code = code;
		this.element = element;
		this.coefficient = coefficient;
	}

	//アクセッサ・メソッド(セッタ＆ゲッタ)
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public double getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(double coefficient) {
		this.coefficient = coefficient;
	}

}
