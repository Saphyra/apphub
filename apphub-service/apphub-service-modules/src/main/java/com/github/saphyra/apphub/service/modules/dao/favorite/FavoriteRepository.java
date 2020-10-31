package com.github.saphyra.apphub.service.modules.dao.favorite;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

interface FavoriteRepository extends CrudRepository<FavoriteEntity, FavoriteEntityKey> {
    @Query("SELECT f FROM FavoriteEntity f WHERE f.key.userId = :userId")
    List<FavoriteEntity> getByUserId(@Param("userId") String userId);

    @Query("DELETE FavoriteEntity f WHERE f.key.userId = :userId")
    @Modifying
    @Transactional
    void deleteByUserId(@Param("userId") String userId);
}
