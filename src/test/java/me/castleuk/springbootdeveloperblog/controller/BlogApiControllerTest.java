package me.castleuk.springbootdeveloperblog.controller;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.Principal;
import java.util.List;
import me.castleuk.springbootdeveloperblog.config.error.ErrorCode;
import me.castleuk.springbootdeveloperblog.domain.Article;
import me.castleuk.springbootdeveloperblog.domain.User;
import me.castleuk.springbootdeveloperblog.dto.AddArticleRequest;
import me.castleuk.springbootdeveloperblog.dto.UpdateArticleRequest;
import me.castleuk.springbootdeveloperblog.repository.BlogRepository;
import me.castleuk.springbootdeveloperblog.repository.UserRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest // 테스트용 어플리케이션 컨텍스트
@AutoConfigureMockMvc // MockMvc 생성
class BlogApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;


    //자바 객체를 JSON 데이터로 변환하는 직렬화 또는 반대로 JSON 데이터를 자바에서 사용하기 위해 자바 객체로 변환하는 역직렬화를 할때 사용한다.
    @Autowired
    protected ObjectMapper objectMapper;


    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        blogRepository.deleteAll();
    }

    @BeforeEach
    void setSecurityContext() {
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                                       .email("user@gamil.com")
                                       .password("test")
                                       .build());

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(),
            user.getAuthorities()));
    }


    @DisplayName("addArticle: 블로그 글 추가에 성공한다.")
    @Test
    public void addArticle() throws Exception {
        // Given
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);

        // 객체 JSON으로 직렬화
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName())
               .thenReturn("username");

        // When
        // 설정한 내용을 바탕으로 요청 전송
        ResultActions result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON)
                                                        .principal(principal)
                                                        .content(requestBody));

        // Then
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles.size()).isEqualTo(1);
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);

    }

    @DisplayName("findAllArticles: 블로그 글 목록 조회에 성공한다.")
    @Test
    public void findAllArticles() throws Exception {
        // Given
        final String url = "/api/articles";
        Article savedArticle = createDefaultArticle();

        // When
        final ResultActions resultActions = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));

        // Then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].content").value(savedArticle.getContent()))
            .andExpect(jsonPath("$[0].title").value(savedArticle.getTitle()));
    }

    @DisplayName("findArticle : 블로그 글 조회에 성공한다.")
    @Test
    public void findArticle() throws Exception {
        // Given
        final String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();

        // When
        final ResultActions resultActions = mockMvc.perform(get(url, savedArticle.getId()));

        // Then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value(savedArticle.getContent()))
            .andExpect(jsonPath("$.title").value(savedArticle.getTitle()));
    }

    @DisplayName("deleteArticle: 블로그 글 삭제에 성공한다.")
    @Test
    public void deleteArticle() throws Exception {
        // Given
        final String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();

        // When
        mockMvc.perform(delete(url, savedArticle.getId()))
               .andExpect(status().isOk());

        // Then
        List<Article> articles = blogRepository.findAll();

        assertThat(articles).isEmpty();
    }

    @DisplayName("updateArticle : 블로그 글 수정에 성공한다.")
    @Test
    public void updateArticle() throws Exception {
        // Given
        final String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();

        final String newTitle = "new title";
        final String newContent = "new content";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);

        // When
        ResultActions result = mockMvc.perform(
            put(url, savedArticle.getId()).contentType(MediaType.APPLICATION_JSON_VALUE)
                                          .content(objectMapper.writeValueAsString(request)));

        // Then
        result.andExpect(status().isOk());

        Article article = blogRepository.findById(savedArticle.getId()).get();

        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);
    }

    private Article createDefaultArticle() {
        return blogRepository.save(Article.builder()
                                          .title("title")
                                          .author(user.getUsername())
                                          .content("content")
                                          .build());
    }

    @DisplayName("addArticle : 아티클 추가할 때 title이 null이면 실패한다.")
    @Test
    public void addArticleNullValidation() throws Exception {
        // Given
        final String url = "/api/articles";
        final String title = null;
        final String content = "content";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);

        final String requestBody = objectMapper.writeValueAsString(userRequest);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName())
               .thenReturn("username");

        // When
        ResultActions result = mockMvc.perform(
            post(url).contentType(MediaType.APPLICATION_JSON_VALUE)
                     .principal(principal)
                     .content(requestBody));

        // Then
        result.andExpect(status().isBadRequest());
    }

    @DisplayName("addArticle: 아티클 추가할 때 title이 10자를 넘으면 실패한다.")
    @Test
    public void addArticlesSizeValidation() throws Exception {
        // Given
        Faker faker = new Faker();

        final String url = "/api/articles";
        final String title = faker.lorem().characters(11);
        final String content = "content";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);

        final String requestBody = objectMapper.writeValueAsString(userRequest);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName())
               .thenReturn("username");

        // When
        ResultActions result = mockMvc.perform(
            post(url).contentType(MediaType.APPLICATION_JSON_VALUE)
                     .principal(principal)
                     .content(requestBody));

        // Then
        result.andExpect(status().isBadRequest());
    }

    @DisplayName("findArticle: 잘못된 HTTP 메서드로 아티클을 조회하려고 하면 조회에 실패한다.")
    @Test
    public void invalidHttpMethod() throws Exception {
        // Given
        final String url = "/api/articles/{id}";
        // When
        final ResultActions resultActions = mockMvc.perform(post(url, 1));

        // Then
        resultActions.andDo(print())
                     .andExpect(status().isMethodNotAllowed())
                     .andExpect(jsonPath("$.message").value(
                         ErrorCode.METHOD_NOT_ALLOWED.getMessage()));
    }

    @DisplayName("findArticle: 존재하지 않는 아티클을 조회하려고 하면 조회에 실패한다.")
    @Test
    public void findArticleInvalidArticle() throws Exception {
        // Given
        final String url = "/api/articles/{id}";
        final long invalidId = 1;

        // When
        final ResultActions resultActions = mockMvc.perform(get(url, invalidId));

        // Then
        resultActions.andDo(print())
                     .andExpect(status().isNotFound())
                     .andExpect(
                         jsonPath("$.message").value(ErrorCode.ARTICLE_NOT_FOUND.getMessage()))
                     .andExpect(jsonPath("$.code").value(ErrorCode.ARTICLE_NOT_FOUND.getCode()));
    }

}