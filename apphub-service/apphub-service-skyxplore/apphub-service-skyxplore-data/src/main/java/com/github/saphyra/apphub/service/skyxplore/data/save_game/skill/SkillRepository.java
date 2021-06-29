package com.github.saphyra.apphub.service.skyxplore.data.save_game.skill;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

interface SkillRepository extends CrudRepository<SkillEntity, String> {
    @Transactional
    @Modifying
    @Query("DELETE FROM SkillEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);
}
