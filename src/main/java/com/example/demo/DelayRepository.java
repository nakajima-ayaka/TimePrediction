package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //このクラスがデータアクセス用のクラスであることを示す
public interface DelayRepository extends JpaRepository<Delay, Integer> {

	//天候番号と鉄道番号を指定して、該当の遅延情報Listを取得
	List<Delay> findByWeatherCodeAndRailwayCode(int weatherCode, int railwayCode);
}
