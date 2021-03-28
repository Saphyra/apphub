package com.github.saphyra.apphub.service.skyxplore.data.save_game.player;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PlayerRepository extends CrudRepository<PlayerEntity, String> {
    void deleteByGameId(String gameId);

    List<PlayerEntity> getByUserId(String userId);
}
