package me.castleuk.springbootdeveloperblog.repository;

import me.castleuk.springbootdeveloperblog.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

//JpaRepository를 상속 받을때, 엔티티 Article과 엔티티의 PK타입 Long을 인수로 넣는다.
public interface BlogRepository extends JpaRepository<Article, Long> {

}
