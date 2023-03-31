package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.skill;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface SkillRepository extends JpaRepository<SkillEntity, String> {
    @Modifying
    @Query("DELETE FROM SkillEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<SkillEntity> getByCitizenId(String citizenId);

    //TODO unit test
    List<SkillEntity> getByGameId(String gameId, PageRequest pageRequest);
}
