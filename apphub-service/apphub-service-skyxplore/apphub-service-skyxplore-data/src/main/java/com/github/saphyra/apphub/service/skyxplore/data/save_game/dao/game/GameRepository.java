package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface GameRepository extends CrudRepository<GameEntity, String> {
    List<GameEntity> getByHost(String userId);

    @Query("SELECT e FROM GameEntity e WHERE e.markedForDeletion = true")
    List<GameEntity> getGamesMarkedForDeletion();
}
