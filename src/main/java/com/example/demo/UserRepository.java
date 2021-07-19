package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //このクラスがデータアクセス用のクラスであることを示す
public interface UserRepository extends JpaRepository<User, Integer> {

	// select * from users where email = ? と同じ意味になる
	Optional<User> findByEmail(String email);

	//登録路線をor検索
	List<User> findByCommuterCode1OrCommuterCode2OrCommuterCode3(int commuterCode1, int commuterCode2,
			int commuterCode3);

}