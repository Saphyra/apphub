package com.github.saphyra.apphub.service.skyxplore.data.save_game.alliance;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
public interface AllianceRepository extends CrudRepository<AllianceEntity, String> {
    void deleteByGameId(String gameId);
}
