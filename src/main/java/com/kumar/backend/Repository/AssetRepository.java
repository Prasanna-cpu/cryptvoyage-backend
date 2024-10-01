package com.kumar.backend.Repository;

import com.kumar.backend.Model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset,Long> {


    @Query("select a from Asset a where a.user.id = ?1")
    Optional<List<Asset>> findAllByUserId(Long userId);

    @Query("select a from Asset a where a.user.id = ?1 and a.coin.id = ?2")
    Optional<Asset> findByUserIdAndCoinId(Long userId, String coinId);


}
