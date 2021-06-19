package com.github.saphyra.apphub.service.skyxplore.data.save_game.stored_resource;

import org.springframework.data.repository.CrudRepository;

interface StoredResourceRepository extends CrudRepository<StoredResourceEntity, String> {
    void deleteByGameId(String gameId);
}
