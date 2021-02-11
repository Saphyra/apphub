package com.github.saphyra.apphub.service.skyxplore.data.save_game.game;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
interface GameRepository extends CrudRepository<GameEntity, String> {
    void deleteByGameId(String gameId);
}
