package com.github.saphyra.apphub.service.skyxplore.data.save_game.game;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface GameRepository extends CrudRepository<GameEntity, String> {
    //TODO unit test
    List<GameEntity> getByHost(String userId);
}
