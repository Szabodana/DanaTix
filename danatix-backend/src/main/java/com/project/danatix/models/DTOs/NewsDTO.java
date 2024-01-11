package com.project.danatix.models.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.danatix.models.News;

import java.util.Date;

public class NewsDTO {
    private Long id;
    private String title;
    private String content;
    @JsonProperty("publish_date")
    private Date publishDate;
    public NewsDTO() {
    }

    public NewsDTO(News news){
        this.id= news.getId();
        this.title=news.getTitle();
        this.content=news.getContent();
        this.publishDate =news.getPublishDate();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }
}
