package com.example.demo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //このクラスがデータアクセス用のクラスであることを示す
public interface UserRepository extends JpaRepository <User, Integer>{

	// select * from users where email = ? と同じ意味になる
	Optional<User> findByEmail(String email);

}
