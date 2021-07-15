package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.stored_resource;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface StoredResourceRepository extends CrudRepository<StoredResourceEntity, String> {
    @Modifying
    @Query("DELETE FROM StoredResourceEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<StoredResourceEntity> getByLocation(String location);
}
