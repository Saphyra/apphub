package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerRepository extends JpaRepository<PlayerEntity, String> {
    @Modifying
    @Query("DELETE FROM PlayerEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<PlayerEntity> getByUserId(String userId);

    List<PlayerEntity> getByGameId(String gameId);

    List<PlayerEntity> getByGameId(String gameId, PageRequest pageRequest);
}
