package com.project.danatix.repositories;

import com.project.danatix.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findProductById(long id);
    Optional<Product> findProductByName(String name);
}