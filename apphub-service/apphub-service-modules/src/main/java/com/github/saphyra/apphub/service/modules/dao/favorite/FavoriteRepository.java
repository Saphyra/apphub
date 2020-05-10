package com.github.saphyra.apphub.service.modules.dao.favorite;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//TODO unit test
interface FavoriteRepository extends CrudRepository<FavoriteEntity, FavoriteEntityKey> {
    @Query("SELECT f FROM FavoriteEntity f WHERE f.key.userId = :userId")
    List<FavoriteEntity> getByUserId(@Param("userId") String userId);
}
