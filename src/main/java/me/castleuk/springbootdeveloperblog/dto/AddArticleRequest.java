package me.castleuk.springbootdeveloperblog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.castleuk.springbootdeveloperblog.domain.Article;

//DTO(data transfer object) : 계층끼리 데이터를 교환하기 위해 사용하는 객체
@NoArgsConstructor //기본 생성자 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 추가
@Getter
public class AddArticleRequest {

    @NotNull
    @Size(min = 1, max = 10)
    private String title;

    @NotNull
    private String content;

    public Article toEntity(String author) { // 생성자를 사용해 객체 생성
        return Article.builder()
                      .title(title)
                      .content(content)
                      .author(author)
                      .build();
    }


}
