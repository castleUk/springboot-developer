package me.castleuk.springbootdeveloperblog.repository;

import java.util.Optional;
import me.castleuk.springbootdeveloperblog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // Email로 사용자 정보를 가져옴
}
