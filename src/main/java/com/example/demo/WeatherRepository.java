package com.example.demo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //このクラスがデータアクセス用のクラスであることを示す
public interface WeatherRepository extends JpaRepository<Weather, Integer> {

	//天候名で検索
	Optional<Weather> findByElement(String element);

	//天候番号で検索
	Optional<Weather> findByCode(int code);

}
