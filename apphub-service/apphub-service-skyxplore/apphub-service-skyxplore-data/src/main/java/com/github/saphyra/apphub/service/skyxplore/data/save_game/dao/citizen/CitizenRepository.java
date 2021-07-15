package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface CitizenRepository extends CrudRepository<CitizenEntity, String> {
    @Modifying
    @Query("DELETE FROM CitizenEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<CitizenEntity> getByLocation(String location);
}
