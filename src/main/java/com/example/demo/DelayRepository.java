package com.example.demo;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //このクラスがデータアクセス用のクラスであることを示す
public interface DelayRepository extends JpaRepository<Delay, Integer> {

	//天候番号と鉄道番号を指定して、該当の遅延情報Listを取得
	List<Delay> findByWeatherCodeAndRailwayCode(int weatherCode, int railwayCode);

	//天候番号を指定して、該当の遅延情報を取得
	List<Delay> findByWeatherCode(int weatherCode);

	//鉄道番号を指定して、該当の遅延情報を取得
	List<Delay> findByRailwayCode(int railwayCode);

	//天候番号で検索
	Optional<Delay> findByCode(int code);

	//天候番号・鉄道番号・遅延時間・遅延日付で検索
	Optional<Delay> findByWeatherCodeAndRailwayCodeAndDelayTimeAndDate(int weatherCode, int railwayCode, int delayTime, Date date);

}
