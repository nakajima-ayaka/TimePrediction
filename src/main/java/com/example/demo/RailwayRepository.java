package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //このクラスがデータアクセス用のクラスであることを示す
public interface RailwayRepository extends JpaRepository <Railway, Integer>{


}
