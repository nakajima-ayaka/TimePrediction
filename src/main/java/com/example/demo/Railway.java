package com.example.demo;

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


}
