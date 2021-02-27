package com.github.saphyra.apphub.service.skyxplore.data.save_game.reserved_storage;

import org.springframework.data.repository.CrudRepository;

interface ReservedStorageRepository extends CrudRepository<ReservedStorageEntity, String> {
    void deleteByGameId(String gameId);
}
