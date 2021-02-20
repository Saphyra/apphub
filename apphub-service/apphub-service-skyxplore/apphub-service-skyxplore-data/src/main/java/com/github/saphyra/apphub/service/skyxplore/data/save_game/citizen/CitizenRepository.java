package com.github.saphyra.apphub.service.skyxplore.data.save_game.citizen;

import org.springframework.data.repository.CrudRepository;

interface CitizenRepository extends CrudRepository<CitizenEntity, String> {
    void deleteByGameId(String gameId);
}
