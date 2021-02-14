package com.github.saphyra.apphub.service.skyxplore.data.save_game.system_connection;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
interface SystemConnectionRepository extends CrudRepository<SystemConnectionEntity, String> {
    void deleteByGameId(String gameId);
}
