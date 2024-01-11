package com.project.danatix;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.project.danatix.models.DTOs.NewsDTO;
import com.project.danatix.models.News;
import com.project.danatix.repositories.NewsRepository;
import com.project.danatix.services.NewsService;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class NewsServiceIntegrationTests {

    @Autowired
    private NewsService newsService;

    @Autowired
    private NewsRepository newsRepository;


    @BeforeEach
    void setUp() {
        newsRepository.deleteAll();
    }
    @Test
    void contextLoads() {
    }

    @Test
    public void testFindAllNews() {

        News news1 = new News();
        news1.setTitle("Title");
        news1.setContent("Content");
        news1.setPublishDate(new Date());

        News news2 = new News();
        news2.setTitle("Title");
        news2.setContent("Content");
        news2.setPublishDate(new Date());

        newsService.saveNews(news1);
        newsService.saveNews(news2);


        List<News> result = newsService.findAllNews();

        assertEquals(2, result.size());
        assertEquals(news1.getTitle(), result.get(0).getTitle());
        assertEquals(news2.getTitle(), result.get(1).getTitle());
    }

    @Test
    public void testFindAllNewsAndConvertToDTO() {
        News news1 = new News();
        news1.setTitle("Title");
        news1.setContent("Content");
        news1.setPublishDate(new Date());

        News news2 = new News();
        news2.setTitle("Title");
        news2.setContent("Content");
        news2.setPublishDate(new Date());

        newsService.saveNews(news1);
        newsService.saveNews(news2);

        List<NewsDTO> dtoList = newsService.findAllNewsAndConvertToDTO();

        assertEquals(2, dtoList.size());
        assertEquals(news1.getContent(), dtoList.get(0).getContent());
        assertEquals(news1.getTitle(), dtoList.get(0).getTitle());
    }
}