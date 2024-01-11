package com.project.danatix.repositories;

import com.project.danatix.models.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News,Long> {
}
