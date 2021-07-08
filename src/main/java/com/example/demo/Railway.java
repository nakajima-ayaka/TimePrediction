package com.example.demo;

import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;

//このクラスがJPAのエンティティ・クラスである意味
@Entity
//エンティティに対応するテーブル名を指定※クラス名がテーブル名と同じでない時に使用
@Table(name = "railway")
public class Railway {

	@Autowired
	//RailwayRepositoryを使えるようにする
	private RailwayRepository railwayRepository;

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
	public String DelayFrequency(int code) {

		//Railway宣言
		Railway railway = null;

		//鉄道番号での検索結果を取得
		Optional<Railway> record = railwayRepository.findById(code);

		//recordの有無の判定
		if (record.isEmpty()) {
			return null;
		}

		//一件のrailwayの情報取得
		railway = record.get();

		//遅延頻度の取得
		int delay = railway.getDelayFrequency();

		//3であればString型の鉄道名で返す、それ以外はnullで返す分岐
		if (delay != 3) {
			return null;

		}
		//3の場合の処理

		return railway.getName();

	}

}
