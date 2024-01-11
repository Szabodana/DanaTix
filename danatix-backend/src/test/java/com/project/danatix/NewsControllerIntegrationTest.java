package com.project.danatix;

import com.project.danatix.models.News;
import com.project.danatix.services.NewsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class NewsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NewsService newsService;

    @Test
    public void testGetNews() throws Exception {
        News news1 = new News();
        news1.setTitle("Title 1");
        news1.setContent("Content 1");
        news1.setPublishDate(new Date());

        News news2 = new News();
        news2.setTitle("Title 2");
        news2.setContent("Content 2");
        news2.setPublishDate(new Date());

        newsService.saveNews(news1);
        newsService.saveNews(news2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/articles"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.articles[0].title").value(news1.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.articles[0].content").value(news1.getContent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.articles[1].title").value(news2.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.articles[1].content").value(news2.getContent()));
    }
}