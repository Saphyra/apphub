package com.github.saphyra.apphub.service.skyxplore.data.save_game.stored_resource;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

interface StoredResourceRepository extends CrudRepository<StoredResourceEntity, String> {
    @Transactional
    @Modifying
    @Query("DELETE FROM StoredResourceEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);
}
