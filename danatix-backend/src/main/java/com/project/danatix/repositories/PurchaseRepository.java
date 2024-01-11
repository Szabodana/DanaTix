package com.project.danatix.repositories;


import com.project.danatix.models.Purchase;
import com.project.danatix.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findAllByUser(User user);

    Purchase findById(long id);

}
