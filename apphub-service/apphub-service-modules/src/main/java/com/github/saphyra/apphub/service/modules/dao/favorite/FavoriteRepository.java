package com.github.saphyra.apphub.service.modules.dao.favorite;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
//TODO unit test
interface FavoriteRepository extends CrudRepository<FavoriteEntity, FavoriteEntityKey> {
    @Query("SELECT f FROM FavoriteEntity f WHERE f.key.userId = :userId")
    List<FavoriteEntity> getByUserId(@Param("userId") String userId);

    @Query("DELETE FavoriteEntity f WHERE f.key.userId = :userId")
    @Modifying
    @Transactional
    //TODO unit test
    void deleteByUserId(@Param("userId") String userId);
}
