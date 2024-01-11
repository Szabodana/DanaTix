package com.project.danatix.services;

import com.project.danatix.models.DTOs.NewsDTO;
import com.project.danatix.models.News;
import com.project.danatix.repositories.NewsRepository;
import com.project.danatix.services.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {
    private final NewsRepository newsRepository;

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }


    public List<News> findAllNews() {
        return newsRepository.findAll();
    }

    public List<NewsDTO> findAllNewsAndConvertToDTO(){
        List<News> newsList = findAllNews();
        return newsList.stream()
                .map(NewsDTO::new)
                .collect(Collectors.toList());
    }

    public void saveNews(News news) {
        newsRepository.save(news);
    }
}
