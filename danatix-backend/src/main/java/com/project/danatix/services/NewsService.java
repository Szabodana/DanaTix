package com.project.danatix.services;

import com.project.danatix.models.DTOs.NewsDTO;
import com.project.danatix.models.News;

import java.util.List;

public interface NewsService {
    List<News> findAllNews();
    void saveNews(News news);
    List<NewsDTO> findAllNewsAndConvertToDTO();
}
