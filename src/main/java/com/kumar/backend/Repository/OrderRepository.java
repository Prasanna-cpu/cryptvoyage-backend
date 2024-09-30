package com.kumar.backend.Repository;

import com.kumar.backend.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {


    @Query("select o from Order o where o.user.id = ?1")
    Optional<List<Order>> findByUserId(Long userId);
}
