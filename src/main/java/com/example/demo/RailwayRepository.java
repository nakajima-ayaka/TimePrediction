package com.example.demo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //このクラスがデータアクセス用のクラスであることを示す
public interface RailwayRepository extends JpaRepository<Railway, Integer> {

	//鉄道名で検索
	Optional<Railway> findByName(String name);

}
