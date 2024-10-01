package com.kumar.backend.Repository;

import com.kumar.backend.Model.WatchList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WatchListRepository extends JpaRepository<WatchList, Long> {
    Optional<WatchList> findByUserId(Long userId);
}
