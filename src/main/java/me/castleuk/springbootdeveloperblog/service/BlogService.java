package me.castleuk.springbootdeveloperblog.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.castleuk.springbootdeveloperblog.domain.Article;
import me.castleuk.springbootdeveloperblog.dto.AddArticleRequest;
import me.castleuk.springbootdeveloperblog.dto.UpdateArticleRequest;
import me.castleuk.springbootdeveloperblog.repository.BlogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor // final이 붙거나 @NotNull이 붙은 필드의 생성자 추가
@Service // 빈으로 등록
public class BlogService {

    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request, String userName) {
        return blogRepository.save(request.toEntity(userName));
    }

    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    public Article findById(long id) {
        return blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found : " + id));
    }

    public void delete(long id) {
        Article article = blogRepository.findById(id)
                                        .orElseThrow(() -> new IllegalArgumentException(
                                            "not found : " + id));

        authorizeArticleAuthor(article);
        blogRepository.delete(article);
    }

    @Transactional //매칭한 메서드를 하나의 트랜잭션으로 묶는 역할을 한다.
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());

        return article;
    }

    // 게시글을 작성한 유저인지 확인
    private static void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext()
                                               .getAuthentication()
                                               .getName();

        if (!article.getAuthor()
                    .equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }

    }
}
