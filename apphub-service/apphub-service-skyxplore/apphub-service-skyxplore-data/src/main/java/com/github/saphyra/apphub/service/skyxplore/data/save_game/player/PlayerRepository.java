package com.github.saphyra.apphub.service.skyxplore.data.save_game.player;

import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<PlayerEntity, String> {
    void deleteByGameId(String gameId);
}
