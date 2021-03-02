package com.github.saphyra.apphub.service.skyxplore.data.save_game.system_connection;

import org.springframework.data.repository.CrudRepository;

interface SystemConnectionRepository extends CrudRepository<SystemConnectionEntity, String> {
    void deleteByGameId(String gameId);
}
