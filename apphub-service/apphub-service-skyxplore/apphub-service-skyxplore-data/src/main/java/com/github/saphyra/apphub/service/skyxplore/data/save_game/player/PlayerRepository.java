package com.github.saphyra.apphub.service.skyxplore.data.save_game.player;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface PlayerRepository extends CrudRepository<PlayerEntity, String> {
    @Transactional
    @Modifying
    @Query("DELETE FROM PlayerEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<PlayerEntity> getByUserId(String userId);

    List<PlayerEntity> getByGameId(String gameId);
}
