package com.project.danatix.repositories;

import com.project.danatix.models.ProductOrder;
import com.project.danatix.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {

    List<ProductOrder> findProductOrdersByUser(User user);

    ProductOrder findProductOrderById(Long id);
}
