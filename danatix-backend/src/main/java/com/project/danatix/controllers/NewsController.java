package com.project.danatix.controllers;

import com.project.danatix.services.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class NewsController {
    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/api/articles")
    public ResponseEntity<Object> getNews() {
        Map<String, Object> response = new HashMap<>();
        response.put("articles", newsService.findAllNewsAndConvertToDTO());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
