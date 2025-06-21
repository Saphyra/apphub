package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.convoy;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface ConvoyRepository extends CrudRepository<ConvoyEntity, String> {
    @Modifying
    @Query("DELETE FROM ConvoyEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<ConvoyEntity> getByGameId(String gameId, PageRequest pageRequest);
}
