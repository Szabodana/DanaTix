package com.project.danatix;

import com.project.danatix.models.DTOs.NewsDTO;
import com.project.danatix.models.News;
import com.project.danatix.repositories.NewsRepository;
import com.project.danatix.services.NewsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class NewsServiceTests {
    @Mock
    private NewsRepository newsRepository;

    private NewsServiceImpl newsService;

    @Test
    void contextLoads() {
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        newsService = new NewsServiceImpl(newsRepository);
    }

    @Test
    public void testFindAllNews() {
        News news1 = new News();
        news1.setId(1L);
        news1.setTitle("Title1");
        news1.setContent("Content1");
        news1.setPublishDate(new Date());

        News news2 = new News();
        news2.setId(2L);
        news2.setTitle("Title2");
        news2.setContent("Content2");
        news2.setPublishDate(new Date());

        when(newsRepository.findAll()).thenReturn(Arrays.asList(news1, news2));

        List<News> result = newsService.findAllNews();

        assertEquals(2, result.size());
        assertEquals(news1, result.get(0));
        assertEquals(news2, result.get(1));
    }

    @Test
    public void testFindAllNewsAndConvertToDTO() {
        News news1 = new News();
        news1.setId(1L);
        news1.setTitle("Title");
        news1.setContent("Content");
        news1.setPublishDate(new Date());
        News news2 = new News();
        news2.setId(2L);
        news2.setTitle("Title");
        news2.setContent("Content");
        news2.setPublishDate(new Date());
        List<News> newsList = Arrays.asList(news1, news2);

        when(newsRepository.findAll()).thenReturn(Arrays.asList(news1, news2));

        List<NewsDTO> dtoList = newsService.findAllNewsAndConvertToDTO();

        assertEquals(newsList.size(), dtoList.size());
        assertEquals(dtoList.get(0).getContent(),newsList.get(0).getContent());
        assertEquals(dtoList.get(0).getTitle(),newsList.get(0).getTitle());
    }

}
