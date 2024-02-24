package me.castleuk.springbootdeveloperblog.controller;

import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.castleuk.springbootdeveloperblog.domain.Article;
import me.castleuk.springbootdeveloperblog.dto.AddArticleRequest;
import me.castleuk.springbootdeveloperblog.dto.ArticleResponse;
import me.castleuk.springbootdeveloperblog.dto.UpdateArticleRequest;
import me.castleuk.springbootdeveloperblog.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController // HTTP Response Body에 객체 데이터를 JSON 형식으로 반환하는 컨트롤러
public class BlogApiController {

    private final BlogService blogService;

    //HTTP 메서드가 POST일 때 전달받은 URL와 동일하면 메서드로 매핑
    //@RequestBody 어노테이션은 HTTP를 요청할 때 응답에 해당하는 값을 @RequestBody 애너테이션이 붙은 대상 객체인 AddArticleRequest에 매핑한다.
    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request, Principal principal) {

        Article savedArticle = blogService.save(request, principal.getName());

        //요청한 자원이 성공적으로 생성되었으며 저장된 블로그 글 정보를 응답 객체에 담아 전송
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);

        /*HTTP 응답 코드
        * 200 OK : 요청이 성공적으로 수행
        * 201 Created : 요청이 성공적으로 수행되었고, 새로운 리소스가 생성되었음
        * 400 Bad Request : 요청 값이 잘못되어 요청이 실채함
        * 403 Forbidden : 권한이 없어서 요청에 실패했음
        * 404 Not Found : 요청 값으로 찾은 리소스가 없어 요청에 실패
        * 500 Internal Server Error : 서버 상에 문제가 있어 요청에 실패
        * */

    }

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        List<ArticleResponse> articles = blogService.findAll().stream().map(ArticleResponse::new).toList();

        return ResponseEntity.ok().body(articles);
    }

    @GetMapping("/api/articles/{id}") // URL에서 id에 해당하는 값이 id로 들어옴 @PathVariable 사용
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id) {
        Article article = blogService.findById(id);

        return ResponseEntity.ok().body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id) {
        blogService.delete(id);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable long id, @RequestBody
        UpdateArticleRequest request) {
        Article updateArticle = blogService.update(id, request);

        return ResponseEntity.ok().body(updateArticle);
    }



}
