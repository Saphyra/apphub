package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.skill;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface SkillRepository extends CrudRepository<SkillEntity, String> {
    @Modifying
    @Query("DELETE FROM SkillEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<SkillEntity> getByCitizenId(String citizenId);
}
