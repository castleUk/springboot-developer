package me.castleuk.springbootdeveloperblog.config.error.exception;


import me.castleuk.springbootdeveloperblog.config.error.ErrorCode;

public class ArticleNotFoundException extends NotFoundException {
    public ArticleNotFoundException() {
        super(ErrorCode.ARTICLE_NOT_FOUND);
    }

}
