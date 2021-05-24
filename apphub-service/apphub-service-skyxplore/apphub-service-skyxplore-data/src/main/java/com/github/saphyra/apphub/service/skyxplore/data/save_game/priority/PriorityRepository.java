package com.github.saphyra.apphub.service.skyxplore.data.save_game.priority;

import org.springframework.data.repository.CrudRepository;

interface PriorityRepository extends CrudRepository<PriorityEntity, PriorityPk> {
    void deleteByGameId(String gameId);
}
